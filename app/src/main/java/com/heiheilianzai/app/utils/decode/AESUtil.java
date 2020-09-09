package com.heiheilianzai.app.utils.decode;

import android.annotation.SuppressLint;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.constant.RabbitConfig;
import com.heiheilianzai.app.utils.MyToash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class AESUtil {
    private static final String CipherMode = "AES/CFB/NoPadding";//使用CFB加密，需要设置IV
    private static final String VIPARA = BuildConfig.picture_vipara;//偏移量
    public static final String key = BuildConfig.picture_key;//加密秘钥
    public static final String desFile = App.getAppContext().getFilesDir().getAbsolutePath() + File.separator;

    //API初始向量（偏移）
    public static final String API_IV = RabbitConfig.ONLINE ? BuildConfig.api_decode_iv : BuildConfig.api_decode_iv_uat;
    //API私钥（密钥）
    public static final String API_ASE_KEY = RabbitConfig.ONLINE ? BuildConfig.api_decode_key : BuildConfig.api_decode_key_uat;

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    private static Cipher initAESCipher(String sKey, int cipherMode) {
        //创建Key gen
        Cipher cipher = null;
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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

    // /** 创建密钥 **/
    private static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append(password);
        while (sb.length() < 32) {
            sb.append("0");
        }
        if (sb.length() > 32) {
            sb.setLength(32);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    // /** 加密字节数据 **/
    private static byte[] encrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            System.out.println(key);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 加密(结果为16进制字符串) **/
    public static String encrypt(String password, String content) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, password);
        String result = byte2hex(data);
        return result;
    }

    // /** 解密字节数组 **/
    private static byte[] decrypt(byte[] content, String password) {

        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));

            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对文件进行AES加密
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @return
     */
    public static File encryptFile(String key, String sourceFilePath, String destFilePath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        File destFile = null;
        File sourceFile = null;
        try {
            sourceFile = new File(sourceFilePath);
            destFile = new File(destFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);
                //以加密流写入文件
                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = cipherInputStream.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                cipherInputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return destFile;
    }


    /**
     * AES方式解密文件
     *
     * @param key
     * @param in
     * @param destFilePath
     * @return
     */
    public static File decryptFile(String key, InputStream in, String destFilePath) {
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
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
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
     * 将文件转换成byte数组
     *
     * @param
     * @return
     */
    public static byte[] File2byte(File tradeFile) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    // /** 解密16进制的字符串为字符串 **/
    public static String decrypt(String password, String content) {
        byte[] data = null;
        try {
            data = hex2byte(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    // /** 字节数组转成16进制字符串 **/
    private static String byte2hex(byte[] b) { // 一个字节的数，
        StringBuilder sb = new StringBuilder(b.length * 2);
        String tmp = "";
        for (byte aB : b) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(aB & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }

    // /** 将hex字符串转换成字节数组 **/
    private static byte[] hex2byte(String inputString) {
        if (inputString == null || inputString.length() < 2) {
            return new byte[0];
        }
        inputString = inputString.toLowerCase();
        int l = inputString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    @SuppressLint("CheckResult")
    public static void getDecideFile(File resource, String url, OnFileResourceListener onFileResourceListener) {
        try {
            Observable.create((ObservableOnSubscribe<File>) emitter -> emitter.onNext(resource)).map(new Function<File, File>() {
                @Override
                public File apply(File file) throws Exception {
                    InputStream inputStream = new FileInputStream(file);
                    return AESUtil.decryptFile(AESUtil.key, inputStream, AESUtil.desFile + resource.getName());
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((Consumer<File>) file -> {
                        onFileResourceListener.onFileResource(file);
                    });
        } catch (Error e) {
            MyToash.LogE("DecideFile", e.toString());
        } catch (Exception e) {
            MyToash.LogE("DecideFile", e.toString());
        }
    }

    public interface OnFileResourceListener {
        void onFileResource(File f);
    }

    /**
     * 加密
     *
     * @param cleartext 加密前的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String cleartext, String key, String iv) {
        try {
            //加密后的字节数组
            byte[] encryptedData = getCipher(Cipher.ENCRYPT_MODE, key, iv).doFinal(cleartext.getBytes("UTF-8"));
            //对加密后的字节数组进行base64编码
            byte[] base64Data = org.apache.commons.codec.binary.Base64.encodeBase64(encryptedData);
            //将base64编码后的字节数组转化为字符串并返回
            return new String(base64Data);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //AES解密
    public static String decrypt(String content, String key, String iv) {
        try {
            byte[] encrypted1 = Base64Utils.decode(content);
            byte[] original = getCipher(Cipher.DECRYPT_MODE, key, iv).doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static Cipher getCipher(int mode, String key, String iv) throws Exception {
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivs = iv.getBytes();
        IvParameterSpec ips = new IvParameterSpec(ivs);
        cipher.init(mode, skeySpec, ips);
        return cipher;
    }
}