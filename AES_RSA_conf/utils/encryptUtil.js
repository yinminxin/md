// RSA公钥
var rsaPublicKey = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJuKaCkL12seqGwO/Vri8ZkBGqPqGn/uyZ6AW3e+8fSxDs6KDf69X4po4xhOMpFto/qZM2OmiCTcULsIlM/4+wcCAwEAAQ==';
// AES密钥(加密前)
var aesKey;
// AES密钥(加密后)
var aesKeyEncrypt;
//项目地址
var address = 'http://localhost:9001/';

/**
 * ajax请求封装
 * @param url POST请求地址
 * @param data 未加密的请求参数JSON
 * @param success 成功回调
 * @returns {string}
 */
function encryptAjaxPost(url,data,success) {
    $.ajax({
        url: url,
        contentType: "application/json; charset=utf-8",
        data:getEncryptData(data),
        type: "post",
        async:false,
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("token", aesKeyEncrypt);
            // console.log("aesKeyEncrypt====>" + aesKeyEncrypt);

            var authorization = localStorage.getItem("Authorization");
            if (!(authorization || verifyWhiteList(url))){
                location.href = address;
            }
            XMLHttpRequest.setRequestHeader("Authorization", authorization);
            // XMLHttpRequest.setRequestHeader("Authorization", localStorage.getItem("Authorization"));
            // console.log("Authorization====>" + localStorage.getItem("Authorization"));
        },
        success: function(data) {
            if(data.code == 401){
                location.href = address;
            }else{
                success(data);
            }
            // console.info(data);
            // if(data.code == '200'){
            //     var token = data.data.token;
            //     localStorage.setItem("Authorization",data.data.token);
            // }else {
            //     alert(data.message);
            // }
        },
        error : function() {
            console.info("错误");
        }
    });
}

/**
 * 加密参数
 * @param data
 * @returns {string}
 */
function getEncryptData(data) {
    return JSON.stringify({"data":encryptFun(JSON.stringify(data))});
}

/**
  * aes加密
  *@param word：需要加密的内容
  *@returns {*} ：返回加密的内容
  */
function encryptFun(word) {
    //初始化AES密钥
    aesKey = get32RandomNum();

    //RSA加密AES密钥
    var encrypt = new JSEncrypt();
    encrypt.setPublicKey(rsaPublicKey);
    aesKeyEncrypt = encrypt.encrypt(aesKey);
    // console.log("加密后AES密钥 ===> " + aesKeyEncrypt);

    //AES加密的密钥
    var key = CryptoJS.enc.Utf8.parse(aesKey);
    //AES加密的内容
    var srcs =CryptoJS.enc.Utf8.parse(word);
    ////AES加密
    var encrypted =CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
    //返回
    return encrypted.toString();
}

/**
 * 封装没有参数的GET请求
 * @param url 请求路径
 * @param success 成功回调
 */
function ajaxGetNoParam(url,success){
    $.ajax({
        url: url,
        type: "get",
        async:false,
        beforeSend: function (XMLHttpRequest) {
            // XMLHttpRequest.setRequestHeader("token", aesKeyEncrypt);
            var authorization = localStorage.getItem("Authorization");
            if (!(authorization || verifyWhiteList(url))){
                location.href = address;
            }
            XMLHttpRequest.setRequestHeader("Authorization", authorization);
        },
        success: function(data) {
            if(data.code == 401){
                location.href = address;
            }else{
                success(data);
            }
        },
        error : function() {
            console.info("错误");
        }
    });
}

/**
 * rsa加密
 */
function rasEncrypt() {
    var publicKey = "替换为Java后台生成的公钥";
    var encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    // 这里输出加密后的字符串
    console.log(encrypt.encrypt("你好asd1"));
}

/**
  * aes加密
  *@param word：需要加密的内容
  *@returns {*} ：返回加密的内容
  */
function encrypt(word) {
    varkey = CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
    var srcs =CryptoJS.enc.Utf8.parse(word);
    var encrypted =CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
    return encrypted.toString();
}

/**
  * aes解密
  * @param word
  * @returns {*}
  */
function decrypt(word) {
    var key =CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
    var decrypt =CryptoJS.AES.decrypt(word, key, { mode: CryptoJS.mode.ECB, padding:CryptoJS.pad.Pkcs7 });
    returnCryptoJS.enc.Utf8.stringify(decrypt).toString();
}

/**
 * 获取32位随机码
 * @returns {string}
 */
function get32RandomNum(){
    var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];
    var nums="";
    for(var i=0;i<32;i++){
        var id = parseInt(Math.random()*61);
        nums+=chars[id];
    }
    return nums;
}