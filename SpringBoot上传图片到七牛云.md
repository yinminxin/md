---

---

# SpringBoot上传图片到七牛云



#### 1. 七牛云官网查看accessKey/secretKey

##### 2. 七牛相关类

###### 1.配置文件属性类

```java
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfigProperties {

    //账号配置信息-七牛密钥之一
    private String accessKey;
    //账号配置信息-七牛密钥之一
    private String secretKey;
    //外链域名(如:image.domain.com)
    private String bucketUrl;
    //创建的存储空间名称
    private String bucketName;
    //凭证的有效时长
    private int expires;

	// 省略get/set方法和构造方法
	
}

```

###### 2.配置文件

```yaml
qiniu:
  accessKey: YOUR_ACCESS_KEY
  secretKey: YOUR_SECRET_KEY
  bucketUrl: YOUR_BUCKET_URL
  bucketName: YOUR_BUCKET_NAME
  expires: 43200
```

###### 3.七牛工具类

```java
@Component
public class QiniuUtils {

    private static final Logger log = LoggerFactory.getLogger(QiniuUtils.class);

    public static QiniuConfigProperties qiniuConfigProperties;

    @Autowired
    public void init(QiniuConfigProperties qiniuConfigProperties){
        // 初始化配置文件属性类
        QiniuUtils.qiniuConfigProperties = qiniuConfigProperties;
    }

    /**
     * 获取token凭证
     * @param key 覆盖的文件名称(正常传null就行)
     * @return
     */
    public static String getUploadToken(String key) {
        Auth auth = Auth.create(qiniuConfigProperties.getAccessKey(), qiniuConfigProperties.getSecretKey());
        StringMap policy = new StringMap();
        policy.put("returnBody", "{\"key\":${key},\"bucket\":${bucket},\"mimeType\":$(mimeType),\"duration\":$(avinfo.format.duration),\"name\":$(fname),\"size\":$(fsize),\"w\":$(imageInfo.width),\"h\":$(imageInfo.height),\"hash\":$(etag)}");
        return auth.uploadToken(qiniuConfigProperties.getBucketName(), key, qiniuConfigProperties.getExpires(), policy, true);
    }

    /**
     * 上传单个图片
     * @param byteFile 字节文件
     * @param filePath 文件全路径 (本地上传)
     * @param file 文件
     * @param key 文件名称
     * @return
     */
    public static String upload(byte[] byteFile, String filePath, File file , String key) {
        try {
            Configuration cfg = new Configuration(Zone.zone0());
            UploadManager uploadManager = new UploadManager(cfg);
            //默认不指定key的情况下，以文件内容的hash值作为文件名
            String uploadToken = getUploadToken(null);
            Response res = null;
            if (file != null) {
                // 调用put方法上传
                log.info("调用put方法上传");
                res = uploadManager.put(file, key, uploadToken);
            }else if (byteFile != null) {
                // 调用put方法上传
                log.info("调用put方法上传");
                res = uploadManager.put(byteFile, key, uploadToken);
            }else if (filePath != null) {
                // 调用put方法上传
                log.info("调用put方法上传");
                res = uploadManager.put(filePath, key, uploadToken);
            }else {
                log.info("上传失败异常信息:文件错误!");
                return "error";
            }
            // 打印返回的信息
            log.info("上传成功响应信息:" + res.bodyString());
            JSONObject result = JSONObject.parseObject(res.bodyString());
            if(null!=result && result.containsKey("key")){
                String url = result.getString("key");
                //返回七牛链接  bucketUrl + bucketName + url
                return qiniuConfigProperties.getBucketUrl() + url;
            }else{//上传失败
                return null;
            }
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            log.info("上传失败异常信息:" + r.toString());
            try {
                // 响应的文本信息
                log.info("上传失败文本信息:" + r.bodyString());
            } catch (QiniuException qe) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}
```

###### 4.文件工具类

```java
package com.lk.hm.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Created by Fangcw on 2017/5/22.
 */
public class FileUtil {

    public static final String IMG_TYPE = ".jpg|.jepg|.gif|.png|.bmp";
    public static final String ALL_TYPE = ".jpg|.jepg|.gif|.png|.bmp|.gz|.rar|.zip|.pdf|.txt|.swf|.wmv";

    /**
     * 文件下载
     *
     * @param file
     * @param response
     */
    public static void downFile(File file, String fileName, HttpServletResponse response) {
        // 输出下载
        if (file != null) {
            BufferedInputStream buff = null;
            OutputStream myout = null;
            try {
                response.setContentType("text/html; charset=UTF-8");
                response.setContentType("application/x-msdownload");// 设置response的编码方式
                response.setContentLength((int) file.length());// 写明要下载的文件的大小
                String pdfName = URLEncoder.encode(fileName, "utf-8");
                response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + pdfName);//设置头部信息,重命名,并且解决中文乱码

                FileInputStream fis = new FileInputStream(file); // 读出文件到i/o流
                buff = new BufferedInputStream(fis);
                byte[] b = new byte[1024];// 相当于我们的缓存
                long k = 0;// 该值用于计算当前实际下载了多少字节
                myout = response.getOutputStream();// 从response对象中得到输出流,准备下载
                while (k < file.length()) {
                    int j = buff.read(b, 0, 1024);
                    k += j;
                    myout.write(b, 0, j);// 将b中的数据写到客户端的内存
                }
                myout.flush();// 将写入到客户端的内存的数据,刷新到磁盘
            } catch (FileNotFoundException e) {
                System.out.println("-------------->要下载的文件没有找到！");
            } catch (IOException e) {
                System.out.println("-------------->下载异常！");
            } finally {
                try {
                    if (myout != null) {
                        myout.close();
                    }
                    if (buff != null) {
                        buff.close();
                    }
                    if (file.delete()) {
                        System.out.println("-------------->文件删除成功！");
                    }
                } catch (IOException e) {
                    System.out.println("-------------->关闭流异常！");
                }
            }
        }
    }

    public static void setFileHeader(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("text/html; charset=UTF-8");
        response.setContentType("application/x-msdownload");// 设置response的编码方式
        String pdfName = URLEncoder.encode(fileName, "utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8'zh_cn'" + pdfName);
    }

    /**
     * @param request
     * @param response
     * @param fileName
     * @return
     */
    public static String setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        final String userAgent = request.getHeader("USER-AGENT");
        String finalFileName = "";
        try {
            //字符串的空格替换为\空格
            fileName = fileName.replaceAll(" ", "\\\\" + " ");
            if (StringUtils.contains(userAgent, "MSIE")) {//IE浏览器
                finalFileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (StringUtils.contains(userAgent, "Mozilla")) {//google,火狐浏览器
                finalFileName = new String(fileName.getBytes(), "ISO-8859-1");
            } else {
                finalFileName = URLEncoder.encode(fileName, "UTF-8");//其他浏览器
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("APPLICATION/OCTET-STREAM;charset=UTF-8");
            //这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开
            response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");
        } catch (UnsupportedEncodingException e) {
        }
        return finalFileName;
    }

    /**
     * 检查目录是否存在,不存在就创建
     *
     * @param filePath
     * @return
     */
    public static void checkFileDir(String filePath) {
        File file = new File(filePath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
        }
    }

    /**
     * 获取文件类型
     *
     * @param @param  fileName
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     */
    public static String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 获取文件名称
     *
     * @param @param  fileName
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     */
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 检查文件类型
     *
     * @param @param  fileName
     * @param @param  isImg
     * @param @return 设定文件
     * @return boolean    返回类型
     * @throws
     */
    public static boolean checkFileType(String fileName, boolean isImg) {
        String fileType = getFileType(fileName);
        if (isImg) {
            return IMG_TYPE.indexOf(fileType.toLowerCase()) == -1;
        } else {
            return ALL_TYPE.indexOf(fileType.toLowerCase()) == -1;
        }
    }

    /**
     * 读取webapp/resource/contact.txt文件中的手机号
     *
     * @param phonePath 文件路径
     * @return
     * @throws IOException
     */
    public static String getFilePhone(String phonePath) throws IOException {
        String phone = "";
        File file = new File(phonePath);
        if (file.exists()) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            phone = bufferedReader.readLine();
            read.close();
        }
        return phone;
    }

    //删除时不删除指定的文件夹
    public static void delNotDir(File file) {
        if (file.exists()) {
            File[] f = file.listFiles();
            if (f != null && f.length > 0) {
                for (File aF : f) {
                    if (aF.isDirectory()) {
                        delNotDir(aF);
                    }
                    //System.out.println(f[i].getPath());
                    aF.delete();
                }
            }
        }
    }

    //删除时同时删除指定的文件夹
    public static void deleteAll(File file) {

        if (file.isFile() || file.list().length == 0) {
            file.delete();
            //System.out.println(file.getPath());
        } else {
            for (File f : file.listFiles()) {
                deleteAll(f); // 递归删除每一个文件

            }
            file.delete(); // 删除文件夹
            //System.out.println(file.getPath());
        }
    }
}
```

###### 5.service层

```java
@Service
public class UploadServiceImpl implements UploadService {

    @Override
    public String uploadImg(MultipartFile upfile) throws IOException {
        //上传的图片名:xxx.png
        String fileName = upfile.getOriginalFilename();
        //获取上文类型:.png
        String imageType = FileUtil.getFileType(fileName);
        //另存到七牛的图片名:ueditor/xxx.png
        String key = "myitem/"+System.currentTimeMillis()+imageType;
        //返回上传后的图片url
        return QiniuUtils.upload(upfile.getBytes(), null, null, key);
    }

    @Override
    public List<String> uploadImgs(MultipartFile[] upfiles) throws IOException {
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < upfiles.length; i++) {
            String url = uploadImg(upfiles[i]);
            urls.add(url);
        }
        return urls;
    }
}
```

###### 6.Controller层

```java
@RestController
public class UploadController extends BaseController{

    @Autowired
    private UploadService uploadService;

    //单图片上传
    @RequestMapping("/uploadImg")
    public ResponseVO uploadImg(MultipartFile upfile){
        //校验文件是否图片
        if (upfile == null || !FileUtil.checkFileType(upfile.getName(),true)) {
            return getFailure("文件为空或图片格式不对!");
        }
        try {
            return getFromData(uploadService.uploadImg(upfile));
        } catch (IOException e) {
            e.printStackTrace();
            return getFailure("图片上传失败!");
        }
    }

    //多图片上传
    @RequestMapping("/uploadImgs")
    public ResponseVO uploadImgs(MultipartFile[] upfiles){
        //校验文件是否图片
        if (upfiles == null) {
            return getFailure("文件为空!");
        }
        for (int i = 0; i < upfiles.length; i++) {
            if (!FileUtil.checkFileType(upfiles[i].getName(),true)) {
                return getFailure("图片格式不对!");
            }
        }
//        if (Arrays.stream(upfiles).filter(
//        file ->!FileUtil.checkFileType(file.getName(),true)).count() > 0) {
//            return getFailure("图片格式不对!");
//        }
        try {
            List<String> urls = uploadService.uploadImgs(upfiles);
            return getFromData(urls);
        } catch (IOException e) {
            e.printStackTrace();
            return getFailure("图片上传失败!");
        }
    }
}
```

###### 7.BaseController

```java
public class BaseController {

    @Autowired
    protected HttpServletRequest request;
//    @Autowired
//    protected HttpServletResponse response;

    public void setSession(String key, Object object){
        request.getSession().setAttribute(key,object);
    }

    public Object getSession(String key){
        return request.getSession().getAttribute(key);
    }

    protected ResponseVO getSuccess(){
        return new ResponseVO(ResultCode.SUCCESS.getCode());
    }

    protected ResponseVO getFromData(Object data){
        ResponseVO responseVO = getSuccess();
        responseVO.setData(data);
        return responseVO;
    }

    protected ResponseVO getFailure(){
        return new ResponseVO(ResultCode.FAIL.getCode());
    }

    protected ResponseVO getFailure(String msg){
        return new ResponseVO(ResultCode.FAIL.getCode(),msg);
    }

    protected ResponseVO getFailure(String errorCode, String msg){
        return new ResponseVO(errorCode, msg);
    }
}
```

