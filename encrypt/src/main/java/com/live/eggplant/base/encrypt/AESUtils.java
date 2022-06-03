package com.live.eggplant.base.encrypt;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import it.sauronsoftware.base64.Base64;

/**
 * 用于m3u8文件加解密
 */
public class AESUtils {

    private static final String ALGORITHM = "AES";
    private static final String ALGORITHMECBNOPADDING = "AES/ECB/NoPadding";
    private static final String ALGORITHMECB = "AES/ECB/PKCS7Padding";
    private static final String ALGORITHMCBC = "AES/CBC/PKCS7Padding";
    private static final String ALGORITHMCBCPKCS5 = "AES/CBC/PKCS5Padding";
    //private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;
    private static final int CBC_POS_INDEX = 16;

    ///产生16位字节的数据
    private static byte[] GetRandomIvCode() {
        Random random = new Random();
        byte[] bytes = new byte[CBC_POS_INDEX];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) random.nextInt(256);
        }
        return bytes;
    }

    /**
     * <p>
     * 加密，对字节进行加密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {

        return encrypt(data, key, false, 0);
    }

    /**
     * <p>
     * 加密，对字节进行CBC加密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptWithCBC(byte[] data, String key) throws Exception {
        return encrypt(data, key, true, CBC_POS_INDEX);
    }

    /**
     * <p>
     * 字符串进行加密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptString(String data, String key) throws Exception {
        byte[] bytes = data.getBytes();
        byte[] encrptByte = encrypt(bytes, key);
        return bytes2Hex(encrptByte);

    }

    /**
     * <p>
     * 对字节进行解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        return decrypt(data, key, false, 0);
    }

    /**
     * <p>
     * 对字节进行CBC解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptWithCBC(byte[] data, String key) throws Exception {
        return decrypt(data, key, true, CBC_POS_INDEX);
    }

    /**
     * <p>
     * 字符串进行解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptString(String data, String key) throws Exception {
        byte[] bytes = hex2Bytes(data);
        byte[] decrptByte = decrypt(bytes, key);
        return new String(decrptByte);
    }

    /**
     * <p>
     * 加密，对字节进行加密
     * </p>
     *
     * @param data
     * @param key
     * @param cbcFlag,是否是要向量
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, String key, boolean cbcFlag, int pos) throws Exception {
        Key k = toKey(decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        if (cbcFlag) {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMCBC);
            byte[] ivCode = GetRandomIvCode();
            IvParameterSpec iv = new IvParameterSpec(ivCode);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(data);
            //合并byte返回
            byte[] comb = new byte[ivCode.length + encrypted.length];
            System.arraycopy(ivCode, 0, comb, 0, ivCode.length);
            System.arraycopy(encrypted, 0, comb, ivCode.length, encrypted.length);
            return comb;
        } else {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        }
    }

    /**
     * <p>
     * 解密，对字节进行解密
     * </p>
     *
     * @param data
     * @param key
     * @param cbcFlag
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, String key, boolean cbcFlag, int pos) throws Exception {
        Key k = toKey(decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        //
        if (cbcFlag) {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMCBC);
            byte[] cKey = new byte[pos];
            System.arraycopy(data, 0, cKey, 0, pos);
            IvParameterSpec iv = new IvParameterSpec(cKey);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] tData = new byte[data.length - pos];
            System.arraycopy(data, pos, tData, 0, tData.length);
            return cipher.doFinal(tData);
        } else {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMECB);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        }
    }

    /**
     * <p>
     * 解密，不補位,对字节进行解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptNoPadding(byte[] data, String key) throws Exception {
        Key k = toKey(decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHMECBNOPADDING);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void decryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);

        String tempFilePath = destFilePath;
        if (sourceFilePath.equals(destFilePath)) {
            tempFilePath = tempFilePath + "fortemp";
        }
        File destFile = new File(tempFilePath);

        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(destFile);
            Key k = toKey(decode(key));
            byte[] raw = k.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMECB);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            CipherOutputStream cout = new CipherOutputStream(out, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                cout.write(cache, 0, nRead);
                cout.flush();
            }
            cout.close();
            out.close();
            in.close();

            ///如果相同，move
            if (sourceFilePath.equals(destFilePath)) {
                sourceFile = new File(sourceFilePath);
                sourceFile.delete();
                destFile = new File(tempFilePath);
                destFile.renameTo(new File(destFilePath));
            }
        }
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFile
     * @throws Exception
     */
    public static byte[] decryptByte(String key, byte[] sourceFile) throws Exception {
        Key k = toKey(decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHMECB);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(sourceFile);
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFile
     * @throws Exception
     */
    public static byte[] encryptByte(String key, byte[] sourceFile) throws Exception {
        Key k = toKey(decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHMECB);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        cipher.doFinal(sourceFile);
        return cipher.doFinal(sourceFile);
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void encryptFile(String key, String sourceFilePath, String destFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);

        String tempFilePath = destFilePath;
        if (sourceFilePath.equals(destFilePath)) {
            tempFilePath = tempFilePath + "fortemp";
        }
        File destFile = new File(tempFilePath);

        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Key k = toKey(decode(key));
            byte[] raw = k.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            CipherInputStream cin = new CipherInputStream(in, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = cin.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            cin.close();
            in.close();
            ///如果相同，move
            if (sourceFilePath.equals(destFilePath)) {
                sourceFile = new File(sourceFilePath);
                sourceFile.delete();
                destFile = new File(tempFilePath);
                destFile.renameTo(new File(destFilePath));
            }

        }
    }

    public static String StringToMD5(String string) {
        if ("".equals(string)) {
            return "";
        }
        return ByteToMD5(string.getBytes());
    }

    public static String ByteToMD5(byte[] data) {

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(data);
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] decode(String base64) throws Exception {
        return Base64.decode(base64.getBytes());
    }

    /**
     * <p>
     * 转换密钥
     * </p>
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

    /**
     * byte数组 转换成 16进制小写字符串
     */
    public static String bytes2Hex(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制字符串 转换为对应的 byte数组
     */
    public static byte[] hex2Bytes(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        char[] hexChars = hex.toCharArray();
        // 如果 hex 中的字符不是偶数个, 则忽略最后一个
        byte[] bytes = new byte[hexChars.length / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt("" + hexChars[i * 2] + hexChars[i * 2 + 1], 16);
        }
        return bytes;
    }

    /**
     * AES方式解密文件
     *
     * @param key
     * @param in
     * @param destFilePath
     * @return
     */
    public static File decryptFile(String key, String iv, InputStream in, String destFilePath) {
        OutputStream out = null;
        File destFile = null;
        File sourceFile = null;
        try {
            destFile = new File(destFilePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            out = new FileOutputStream(destFile);
            Cipher cipher = initAESCipher(key, iv, Cipher.DECRYPT_MODE);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[CACHE_SIZE];
            int r;
            while ((r = in.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destFile;
    }

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    private static Cipher initAESCipher(String sKey, String iv, int cipherMode) {
        //创建Key gen
        Cipher cipher = null;
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), ALGORITHM);
            cipher = Cipher.getInstance(ALGORITHMCBCPKCS5);
            cipher.init(cipherMode, key, zeroIv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return cipher;
    }
}