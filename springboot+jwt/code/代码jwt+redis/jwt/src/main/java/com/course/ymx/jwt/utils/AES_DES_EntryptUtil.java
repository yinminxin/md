
 /*
  * Copyright (C), 2014-2019, 万达信息股份有限公司
  * FileName: DesUtil
  * Author:   Blare
  * Date:     2019/12/11 10:54
  * Description: DES加密实现
  * History:
  * <author>          <time>          <version>          <desc>
  * 作者姓名           修改时间           版本号              描述
  */
 package com.course.ymx.jwt.utils;

 import org.apache.commons.codec.binary.Hex;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import javax.crypto.*;
 import javax.crypto.spec.DESKeySpec;
 import javax.crypto.spec.SecretKeySpec;
 import java.io.UnsupportedEncodingException;
 import java.security.InvalidKeyException;
 import java.security.Key;
 import java.security.NoSuchAlgorithmException;
 import java.security.SecureRandom;
 import java.security.spec.InvalidKeySpecException;
 import java.util.Base64;
 import java.util.Objects;

 /**
  * 〈一句话功能简述〉<br>
  * 〈AES和DES对称加密解密算法实现〉
  *
  * @author Blare
  * @create 2019/12/11
  * @since 1.0.0
  */
 public class AES_DES_EntryptUtil {

     //日志对象
     private static final Logger logger = LoggerFactory.getLogger(AES_DES_EntryptUtil.class);
     //DES算法要有一个随机数源,因为Random是根据时间戳生成的有限随机数,比较容易破解,所以在这里使用SecureRandom
     private static SecureRandom secureRandom = new SecureRandom();
     //自定义密钥
     private static final String DEFAULT_SECRET = "wandersgroup2020";
     //字符集
     private static final String CHARSET = "UTF-8";

     public static void main(String[] args) {
         desTestencrypt("我是谁2312313".getBytes());
         String encrypt = encryptString("我是谁2312313", SYMMETRY_ENCRYPT.AES);
         logger.info(encrypt);
         String decrypt = decryptString(Objects.requireNonNull("3egzvTepb6UdNGPq31H3Vw=="), SYMMETRY_ENCRYPT.AES);
         logger.info(decrypt);
     }

     /**
      * 功能描述: <br>
      * 〈字符串解密〉<密钥参数>
      *
      * @author Blare
      * @date 2019/12/11
      */
     public static String decryptStringBySecret(String source, String secret) {
         try {
             byte[] sourceBytes = Base64.getDecoder().decode(source);
             byte[] secretBytes = secret.getBytes(CHARSET);
             byte[] encryptBytes = decrypt(sourceBytes, secretBytes, SYMMETRY_ENCRYPT.AES);
             return new String(encryptBytes, CHARSET);
         } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
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
     public static String encryptString(String source, SYMMETRY_ENCRYPT encryptType) {
         try {
             byte[] sourceBytes = source.getBytes(CHARSET);
             byte[] secretBytes = DEFAULT_SECRET.getBytes(CHARSET);
             byte[] encryptBytes = encrypt(sourceBytes, secretBytes, encryptType);
             return Base64.getEncoder().encodeToString(encryptBytes);
         } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
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
     public static String decryptString(String source, SYMMETRY_ENCRYPT encryptType) {
         try {
             byte[] sourceBytes = Base64.getDecoder().decode(source);
             byte[] secretBytes = DEFAULT_SECRET.getBytes(CHARSET);
             byte[] encryptBytes = decrypt(sourceBytes, secretBytes, encryptType);
             return new String(encryptBytes, CHARSET);
         } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException e) {
             e.printStackTrace();
         }
         return null;
     }

     /**
      * 功能描述: <br>
      * 〈使用原始密钥数据转换为SecretKey对象〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     private static SecretKey getSecretKey(byte[] keyBytes, String type) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
         if ("DES".equals(type)) {
             // 创建一个密匙工厂
             SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(type);
             // 创建一个DESKeySpec对象
             DESKeySpec dks = new DESKeySpec(keyBytes);
             // 将DESKeySpec对象转换成SecretKey对象
             return keyFactory.generateSecret(dks);
         }

         return new SecretKeySpec(keyBytes, type);
     }

     /**
      * 功能描述: <br>
      * 〈DES加密〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     private static byte[] encrypt(byte[] contentArray, byte[] keyArray, SYMMETRY_ENCRYPT encryptType) throws InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeySpecException {
         return symmetryDE(contentArray, keyArray, Cipher.ENCRYPT_MODE, encryptType);
     }

     /**
      * 功能描述: <br>
      * 〈DES解密〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     private static byte[] decrypt(byte[] encryptArray, byte[] keyArray, SYMMETRY_ENCRYPT encryptType) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
         return symmetryDE(encryptArray, keyArray, Cipher.DECRYPT_MODE, encryptType);
     }

     /**
      * 功能描述: <br>
      * 〈Cipher 加密解密操作〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     private static byte[] symmetryDE(byte[] contentArray, byte[] keyArray, int mode, SYMMETRY_ENCRYPT encryptType) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
         //获取密钥对象
         SecretKey secretKey = getSecretKey(keyArray, encryptType.getType());
         //获取真正执行加/解密操作的Cipher
         Cipher cipher = Cipher.getInstance(encryptType.getEncrypt());
         //用密匙初始化Cipher对象
         cipher.init(mode, secretKey, secureRandom);
         //执行加/解密操作
         return cipher.doFinal(contentArray);
     }

     /**
      * 功能描述: <br>
      * 〈加密解密〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     public static void desTestencrypt(byte[] src) {

         try {
             //生成key
             KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
             keyGenerator.init(56);
             SecretKey secretKey = keyGenerator.generateKey();
             byte[] encoded = secretKey.getEncoded();
             logger.info("密钥【{}】", Hex.encodeHexString(encoded));
             //key转换
             Key key = new SecretKeySpec(encoded, "DES");
             logger.info("生成的key：【{}】", key);
             //加密
             Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
             cipher.init(Cipher.ENCRYPT_MODE, key);
             byte[] bytes = cipher.doFinal(src);
             logger.info("JDK自带DES加密结果：【{}】", Hex.encodeHexString(bytes));
             logger.info("JDK自带DES加密结果：【{}】", Base64.getEncoder().encodeToString(bytes));
             cipher.init(Cipher.DECRYPT_MODE, key);
             bytes = cipher.doFinal(bytes);
             logger.info("JDK自带DES解密结果：【{}】", new String(bytes));
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
             e.printStackTrace();
         }
     }

     /**
      * 功能描述: <br>
      * 〈加密方式〉
      *
      * @author Blare
      * @date 2019/12/11
      */
     enum SYMMETRY_ENCRYPT {
         AES("AES", "AES/ECB/PKCS5Padding"),
         DES("DES", "DES/ECB/PKCS5Padding");

         SYMMETRY_ENCRYPT(String type, String encrypt) {
             this.type = type;
             this.encrypt = encrypt;
         }

         private String type;
         private String encrypt;

         public String getType() {
             return type;
         }

         public String getEncrypt() {
             return encrypt;
         }
     }
 }