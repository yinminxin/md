package com.course.ymx.jwt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RSA_EntryptUtil {
    //日志对象
    private static final Logger logger = LoggerFactory.getLogger(RSA_EntryptUtil.class);
    //加密方式
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 貌似默认是RSA/NONE/PKCS1Padding，未验证
     */
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    //字符集
    private static final String CHARSET = "UTF-8";
    private static Map<String, byte[]> keyMap;

    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024
     */
    public static final int KEY_SIZE = 2048;

    static {
        generateKeyBytes();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        // 加密
        PublicKey publicKey = restorePublicKey(keyMap.get(PUBLIC_KEY));
        byte[] encodedText = encodeBytes(publicKey, "123456".getBytes(CHARSET));
        logger.info("RSA 加密结果：【{}】", Base64.getEncoder().encodeToString(encodedText));

        // 解密
        PrivateKey privateKey = restorePrivateKey(keyMap.get(PRIVATE_KEY));
        byte[] decodetext = decodeBytes(privateKey,encodedText);
        logger.info("RSA 解密结果：【{}】", new String(decodetext));

        String encryptString = encryptString("我是谁2312313", PUBLIC_KEY);
        logger.info("RSA 加密结果：【{}】", encryptString);

        String decryptString = decryptString(encryptString, PRIVATE_KEY);
        logger.info("RSA 解密结果：【{}】", decryptString);

//        String s2 = compositeEncryption("tysy@2020adajkdjfkj5555ghhgfdswy");
//        logger.info("RSA+DES 复合加密结果：【{}】", s2);
//        //GXua8WtG4fHt2FGo4ySTpluqFLv6xog2xvKewbxgaQWESIsLNLsX3xN0oaUfBZzt9pW9i2ZjT0M2rMDoFVBL9Q==
//
//        String s = compositeDecryption(s2);
//        //ICIWqsppVSxN8kRaMAFoL/H0c+slkSbIovVoXFuX/vLa7zECnbES+4UNR7IA9/ZdsNrxu/KveLXsw7Kpttuc8vKRcjqmJ7yZU7lkecTVgHAgjzjtiOcFDAVGCloSpgmm
//        //MD5加密
////        String s1 = DigestUtils.md5DigestAsHex(String.valueOf(s).getBytes());
//        logger.info("RSA+DES 复合解密结果：【{}】", s);

//        byte[] decode = Base64.getDecoder().decode("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL/AD+hHkOkRDSxFQNBq0fqZf3kEB7fR2CLSpmOsba+Bjd48ob2iPlUx3sG/STjBn7TB/NhlyCHdAo3sWwIj4u8CAwEAAQ==");
//        PublicKey restorePublicKey = restorePublicKey(decode);
//        String des = AES_DES_EntryptUtil.encryptString("123456", AES_DES_EntryptUtil.SYMMETRY_ENCRYPT.DES);
//        byte[] encodedText111 = encodeBytes(restorePublicKey, "wandersgroup2020".getBytes(CHARSET));
//        logger.info("RSA 加密结果：【{}】", Base64.getEncoder().encodeToString(encodedText111));



//        String s3 = compositeEncryption("123456");
//        String s4 = DigestUtils.md5DigestAsHex(String.valueOf(s).getBytes());
//        logger.info("RSA+DES 复合加密结果：【{}】", s3);

        String dataEncrypt = "5eeGMGFoKI9bAUHHAJoSGEjdiQgqfUlSKxysK+xeiIv+BP+0u2CYsEr4TTKV1dI2gWlBipKuxHpXarMaKaueL4iROPZ136DXVkc9rRJACzdznNLLKZtaN7dXpk+sAe72MYwJoQjBYwcKl1EwENr0qA==";
        String aesKey = "VgVNDGwtMkTZZhifD0IB0I+1/LnfHPt3Nz/I+lSc02xTQs/SBcVKDKEhFFH+cWtXVlZYlzaoV6Plx9t11YC/hw==";

        String data = decryptionRsaAndAes(dataEncrypt, aesKey);
        System.out.println("*****************************");
        System.out.println(data);
        System.out.println("*****************************");

        //jdkRSA();

    }

    /**
     * 给AES密钥解密，再调用AES解密
     * @param encodedText 密文
     * @return
     */
    public static String decryptionRsaAndAes(String encodedText,String aesKeyEncrypt){
        //前端公钥
        //MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL/AD+hHkOkRDSxFQNBq0fqZf3kEB7fR2CLSpmOsba+Bjd48ob2iPlUx3sG/STjBn7TB/NhlyCHdAo3sWwIj4u8CAwEAAQ==
        //后端私钥
        //MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv8AP6EeQ6RENLEVA0GrR+pl/eQQHt9HYItKmY6xtr4GN3jyhvaI+VTHewb9JOMGftMH82GXIId0CjexbAiPi7wIDAQABAkEAlCBHaZWXcSQ1++QHvLk4OpHHcnHVkH/vqn72AHucQ/3GIAw4Mwiq3OtfMd0XkA7l46yAFCzlTgfs6OJw1KHU+QIhAPK/VudDiTvbuxABg/ioLL2DK1w8Ld+GElFHzDBnajsTAiEAyjf7O5xhzgt2OwgTBD6xXNApPUpSbZJtARfZ3GyQuDUCIAMiMS7/EjBxn7KPMnLtSNaRcOfZ5wWxp17hPGOLkjV9AiEAseRfgghHThBtOO8Yc5KHSsPgk4dvTe8TL3QA9tUW1sUCIDAXfP3r50XKvu7Q4cAWk2Z46iq9T1I8UlgDC8//gl/w
        // 解密
        byte[] decode = Base64.getDecoder().decode("MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv8AP6EeQ6RENLEVA0GrR+pl/eQQHt9HYItKmY6xtr4GN3jyhvaI+VTHewb9JOMGftMH82GXIId0CjexbAiPi7wIDAQABAkEAlCBHaZWXcSQ1++QHvLk4OpHHcnHVkH/vqn72AHucQ/3GIAw4Mwiq3OtfMd0XkA7l46yAFCzlTgfs6OJw1KHU+QIhAPK/VudDiTvbuxABg/ioLL2DK1w8Ld+GElFHzDBnajsTAiEAyjf7O5xhzgt2OwgTBD6xXNApPUpSbZJtARfZ3GyQuDUCIAMiMS7/EjBxn7KPMnLtSNaRcOfZ5wWxp17hPGOLkjV9AiEAseRfgghHThBtOO8Yc5KHSsPgk4dvTe8TL3QA9tUW1sUCIDAXfP3r50XKvu7Q4cAWk2Z46iq9T1I8UlgDC8//gl/w");
        PrivateKey privateKey = restorePrivateKey(decode);
        byte[] decodetext = decodeBytes(privateKey, Base64.getDecoder().decode(aesKeyEncrypt));
        //解密后aes密钥
        String aesKey = new String(decodetext);
        String data = AES_DES_EntryptUtil.decryptStringBySecret(encodedText,aesKey);
        return data;
//        return new String(decodetext);  //String decodeStr =
//        return AES_DES_EntryptUtil.decryptString(Objects.requireNonNull(decodeStr), AES_DES_EntryptUtil.SYMMETRY_ENCRYPT.AES);
    }

    /**
     * 先对称加密再非对称加密
     * @param decodetext 明文
     * @return
     */
    public static String compositeEncryption(String decodetext) throws UnsupportedEncodingException {
        //前端公钥
        //MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL/AD+hHkOkRDSxFQNBq0fqZf3kEB7fR2CLSpmOsba+Bjd48ob2iPlUx3sG/STjBn7TB/NhlyCHdAo3sWwIj4u8CAwEAAQ==
        //后端私钥
        //MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv8AP6EeQ6RENLEVA0GrR+pl/eQQHt9HYItKmY6xtr4GN3jyhvaI+VTHewb9JOMGftMH82GXIId0CjexbAiPi7wIDAQABAkEAlCBHaZWXcSQ1++QHvLk4OpHHcnHVkH/vqn72AHucQ/3GIAw4Mwiq3OtfMd0XkA7l46yAFCzlTgfs6OJw1KHU+QIhAPK/VudDiTvbuxABg/ioLL2DK1w8Ld+GElFHzDBnajsTAiEAyjf7O5xhzgt2OwgTBD6xXNApPUpSbZJtARfZ3GyQuDUCIAMiMS7/EjBxn7KPMnLtSNaRcOfZ5wWxp17hPGOLkjV9AiEAseRfgghHThBtOO8Yc5KHSsPgk4dvTe8TL3QA9tUW1sUCIDAXfP3r50XKvu7Q4cAWk2Z46iq9T1I8UlgDC8//gl/w
        // 加密
        String des = AES_DES_EntryptUtil.encryptString(decodetext, AES_DES_EntryptUtil.SYMMETRY_ENCRYPT.AES);
        byte[] decode = Base64.getDecoder().decode("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL/AD+hHkOkRDSxFQNBq0fqZf3kEB7fR2CLSpmOsba+Bjd48ob2iPlUx3sG/STjBn7TB/NhlyCHdAo3sWwIj4u8CAwEAAQ==");
        PublicKey restorePublicKey = restorePublicKey(decode);
        byte[] encodedText111 = encodeBytes(restorePublicKey, des.getBytes(CHARSET));
        return Base64.getEncoder().encodeToString(encodedText111);
    }

    /**
     * 先非对称解密再对称解密
     * @param encodedText 密文
     * @return
     */
    public static String compositeDecryption(String encodedText){
        //前端公钥
        //MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL/AD+hHkOkRDSxFQNBq0fqZf3kEB7fR2CLSpmOsba+Bjd48ob2iPlUx3sG/STjBn7TB/NhlyCHdAo3sWwIj4u8CAwEAAQ==
        //后端私钥
        //MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv8AP6EeQ6RENLEVA0GrR+pl/eQQHt9HYItKmY6xtr4GN3jyhvaI+VTHewb9JOMGftMH82GXIId0CjexbAiPi7wIDAQABAkEAlCBHaZWXcSQ1++QHvLk4OpHHcnHVkH/vqn72AHucQ/3GIAw4Mwiq3OtfMd0XkA7l46yAFCzlTgfs6OJw1KHU+QIhAPK/VudDiTvbuxABg/ioLL2DK1w8Ld+GElFHzDBnajsTAiEAyjf7O5xhzgt2OwgTBD6xXNApPUpSbZJtARfZ3GyQuDUCIAMiMS7/EjBxn7KPMnLtSNaRcOfZ5wWxp17hPGOLkjV9AiEAseRfgghHThBtOO8Yc5KHSsPgk4dvTe8TL3QA9tUW1sUCIDAXfP3r50XKvu7Q4cAWk2Z46iq9T1I8UlgDC8//gl/w
        // 解密
        byte[] decode = Base64.getDecoder().decode("MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv8AP6EeQ6RENLEVA0GrR+pl/eQQHt9HYItKmY6xtr4GN3jyhvaI+VTHewb9JOMGftMH82GXIId0CjexbAiPi7wIDAQABAkEAlCBHaZWXcSQ1++QHvLk4OpHHcnHVkH/vqn72AHucQ/3GIAw4Mwiq3OtfMd0XkA7l46yAFCzlTgfs6OJw1KHU+QIhAPK/VudDiTvbuxABg/ioLL2DK1w8Ld+GElFHzDBnajsTAiEAyjf7O5xhzgt2OwgTBD6xXNApPUpSbZJtARfZ3GyQuDUCIAMiMS7/EjBxn7KPMnLtSNaRcOfZ5wWxp17hPGOLkjV9AiEAseRfgghHThBtOO8Yc5KHSsPgk4dvTe8TL3QA9tUW1sUCIDAXfP3r50XKvu7Q4cAWk2Z46iq9T1I8UlgDC8//gl/w");
        PrivateKey privateKey = restorePrivateKey(decode);
        byte[] decodetext = decodeBytes(privateKey, Base64.getDecoder().decode(encodedText));
        String decodeStr = new String(decodetext);
        String s = AES_DES_EntryptUtil.decryptString(Objects.requireNonNull(decodeStr), AES_DES_EntryptUtil.SYMMETRY_ENCRYPT.AES);
        return s;
//        return new String(decodetext);  //String decodeStr =
//        return AES_DES_EntryptUtil.decryptString(Objects.requireNonNull(decodeStr), AES_DES_EntryptUtil.SYMMETRY_ENCRYPT.AES);
    }

    /**
     * 功能描述: <br>
     * 〈生成密钥对。注意这里是生成密钥对KeyPair，再由密钥对获取公私钥〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static Map<String, byte[]> generateKeyBytes() {

        if (null == keyMap) {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
                keyPairGenerator.initialize(KEY_SIZE);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

                keyMap = new HashMap<>();
                keyMap.put(PUBLIC_KEY, publicKey.getEncoded());
                keyMap.put(PRIVATE_KEY, privateKey.getEncoded());
                return keyMap;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 功能描述: <br>
     * 〈还原公钥，X509EncodedKeySpec 用于构建公钥的规范〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            return factory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能描述: <br>
     * 〈还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            return factory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 功能描述: <br>
     * 〈加密〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static byte[] encodeBytes(PublicKey key, byte[] plainText) {

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 功能描述: <br>
     * 〈解密〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static byte[] decodeBytes(PrivateKey key, byte[] encodedText) {

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encodedText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 功能描述: <br>
     * 〈字符串加密〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static String encryptString(String source, String publicKey) {
        try {
            if (null != keyMap) {
                PublicKey pk = restorePublicKey(keyMap.get(publicKey));
                byte[] sourceBytes = source.getBytes(CHARSET);
                byte[] encryptBytes = encodeBytes(pk, sourceBytes);
                return Base64.getEncoder().encodeToString(encryptBytes);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能描述: <br>
     * 〈字符串解密〉
     *
     * @author Blare
     * @date 2019/12/11
     */
    public static String decryptString(String source, String privateKey) {
        if (null != keyMap) {
            PrivateKey pk = restorePrivateKey(keyMap.get(privateKey));
            byte[] sourceBytes = Base64.getDecoder().decode(source);
            byte[] encryptBytes = decodeBytes(pk, sourceBytes);
            return null == encryptBytes ? null : new String(encryptBytes);
        }
        return null;
    }

    public static void jdkRSA() {
        try {
            // 1.初始化发送方密钥
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
            System.out.println("Public Key:" + Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
            System.out.println("Private Key:" + Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));

            // 2.私钥加密、公钥解密 ---- 加密
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal("rsa test".getBytes());
            System.out.println("私钥加密、公钥解密 ---- 加密:" + Base64.getEncoder().encodeToString(result));

            // 3.私钥加密、公钥解密 ---- 解密
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(result);
            System.out.println("私钥加密、公钥解密 ---- 解密:" + new String(result));

            // 4.公钥加密、私钥解密 ---- 加密
            X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
            KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
            PublicKey publicKey2 = keyFactory2.generatePublic(x509EncodedKeySpec2);
            Cipher cipher2 = Cipher.getInstance("RSA");
            cipher2.init(Cipher.ENCRYPT_MODE, publicKey2);
            byte[] result2 = cipher2.doFinal("rsa test".getBytes());
            System.out.println("公钥加密、私钥解密 ---- 加密:" + Base64.getEncoder().encodeToString(result2));

            // 5.私钥解密、公钥加密 ---- 解密
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
            KeyFactory keyFactory5 = KeyFactory.getInstance("RSA");
            PrivateKey privateKey5 = keyFactory5.generatePrivate(pkcs8EncodedKeySpec5);
            Cipher cipher5 = Cipher.getInstance("RSA");
            cipher5.init(Cipher.DECRYPT_MODE, privateKey5);
            byte[] result5 = cipher5.doFinal(result2);
            System.out.println("公钥加密、私钥解密 ---- 解密:" + new String(result5));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}