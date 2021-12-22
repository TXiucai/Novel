package com.heiheilianzai.app.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class FileUtils {

    /**
     * 获取文件编码
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getCharset(String fileName) throws IOException {
        String charset;
        FileInputStream fis = new FileInputStream(fileName);
        byte[] buf = new byte[4096];
        // (1)
        UniversalDetector detector = new UniversalDetector(null);
        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();
        // (4)
        charset = detector.getDetectedCharset();
        // (5)
        detector.reset();
        return charset;
    }

    /**
     * 根据路径获取文件名
     *
     * @param pathandname
     * @return
     */
    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return "";
        }

    }

    public static List<File> getSuffixFile(String filePath, String suffere) {
        List<File> files = new ArrayList<>();
        File f = new File(filePath);
        return getSuffixFile(files, f, suffere);
    }

    /**
     * 读取sd卡上指定后缀的所有文件
     *
     * @param files   返回的所有文件
     * @param f       路径(可传入sd卡路径)
     * @param suffere 后缀名称 比如 .gif
     * @return
     */
    public static List<File> getSuffixFile(List<File> files, File f, final String suffere) {
        if (!f.exists()) {
            return null;
        }

        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if (subFile.isHidden()) {
                continue;
            }
            if (subFile.isDirectory()) {
                getSuffixFile(files, subFile, suffere);
            } else if (subFile.getName().endsWith(suffere)) {
                files.add(subFile);
            } else {
                //非指定目录文件 不做处理
            }
//            Utils.printLog("filename",subFile.getName());
        }
        return files;
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static boolean isSimpleOrComplex(String str) {
        try {
            if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
                return true;
            } else {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return 解决首位0被省略问题
     * 解决超大文件问题
     */
    public static String getFileMD5(File file) {
        MappedByteBuffer[] mappedByteBuffers;
        int bufferCount;

        StringBuffer stringbuffer = null;
        try {
            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();

            long fileSize = ch.size();
            bufferCount = (int) Math.ceil((double) fileSize / (double) Integer.MAX_VALUE);
            mappedByteBuffers = new MappedByteBuffer[bufferCount];

            long preLength = 0;
            long regionSize = Integer.MAX_VALUE;
            for (int i = 0; i < bufferCount; i++) {
                if (fileSize - preLength < Integer.MAX_VALUE) {
                    regionSize = fileSize - preLength;
                }
                mappedByteBuffers[i] = ch.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
                preLength += regionSize;
            }

            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            for (int i = 0; i < bufferCount; i++) {
                messagedigest.update(mappedByteBuffers[i]);
            }
            byte[] bytes = messagedigest.digest();
            int n = bytes.length;
            stringbuffer = new StringBuffer(2 * n);
            for (int l = 0; l < n; l++) {
                byte bt = bytes[l];
                char c0 = hexDigits[(bt & 0xf0) >> 4];
                char c1 = hexDigits[bt & 0xf];
                stringbuffer.append(c0);
                stringbuffer.append(c1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringbuffer.toString();
    }

    /**
     * 过滤小说 特殊格式字符
     * 避免 tts 语音库无法识别 造成读书内容 异常
     */
    public static String getFilter2TTS(String str) {
        String newStr = "";
        byte[] bytes = new byte[]{(byte) 0xc2, (byte) 0xa0};
        String temp = null;
        try {
            temp = new String(bytes, Xml.Encoding.UTF_8.name());
            newStr = str.replaceAll(temp, "");
            return newStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}