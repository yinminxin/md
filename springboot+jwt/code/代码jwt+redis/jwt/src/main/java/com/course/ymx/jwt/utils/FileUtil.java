package com.course.ymx.jwt.utils;

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
