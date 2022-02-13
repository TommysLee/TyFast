package com.ty.cm.utils.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算文件MD5值
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class FileMD5 {

    /**
     * 计算小文件的MD5值
     *
     * @param filePath
     * @return String
     */
    @SuppressWarnings("deprecation")
    public static String calcSmall(String filePath) {

        String md5Val = "";
        final File file = new File(filePath);
        if (file.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(file);
                md5Val = DigestUtils.md5Hex(IOUtils.toByteArray(fis));
                IOUtils.closeQuietly(fis);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return md5Val;
    }

    /**
     * 计算中等文件的MD5值
     *
     * 缺点：文件内存释放的不确定性，文件最大不能超过Integer.MAX_VALUE
     *
     * @param filePath
     * @return String
     */
    @SuppressWarnings("deprecation")
    public static String calcMedium(String filePath) {

        String md5Val = "";
        final File file = new File(filePath);
        if (file.exists()) {
            try {
                final FileInputStream fis = new FileInputStream(file);
                MappedByteBuffer byteBuffer = fis.getChannel().map(
                        FileChannel.MapMode.READ_ONLY, 0, file.length());
                fis.getChannel().close();
                IOUtils.closeQuietly(fis);

                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(byteBuffer);
                byte[] md5Bytes = md5.digest(); // 拿到结果，也是字节数组，包含16个元素
                BigInteger bi = new BigInteger(1, md5Bytes); // 转为十六进制
                md5Val = bi.toString(16);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
                log.error(e.getMessage(), e);
            }
        }
        return md5Val;
    }

    /**
     * 计算大文件的MD5值
     *
     * @param filePath
     * @return String
     */
    public static String calcLarge(String filePath) {

        String md5Val = calcHash(filePath, 1);
        return md5Val;
    }

    /**
     * 计算文件的SHA1值
     *
     * @param filePath
     * @return String
     */
    public static String calcSHA1(String filePath) {

        String md5Val = calcHash(filePath, 2);
        return md5Val;
    }

    /**
     * 计算文件的哈希值
     *
     * @param filePath
     * @param hashType
     *            ----> 哈希类型：1：MD5；2：SHA1
     * @return String
     */
    @SuppressWarnings("deprecation")
    public static String calcHash(String filePath, int hashType) {

        String md5Val = "";
        final File file = new File(filePath);
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        if (file.exists()) {
            try {
                MessageDigest messageDigest = 1 == hashType ? DigestUtils
                        .getMd5Digest() : DigestUtils.getSha1Digest();
                fileInputStream = new FileInputStream(file);
                digestInputStream = new DigestInputStream(fileInputStream,
                        messageDigest); // 此类可以在读取数据的同时，调用messageDigest.update();

                // read的过程中进行MD5处理，直到读完文件
                byte[] bytes = new byte[8192];
                while (digestInputStream.read(bytes) > 0);

                // 获取最终的MessageDigest
                messageDigest = digestInputStream.getMessageDigest();
                byte[] md5Bytes = messageDigest.digest(); // 拿到结果，也是字节数组，包含16个元素
                md5Val = Hex.encodeHexString(md5Bytes); // 转为十六进制

                // 关闭流
                IOUtils.closeQuietly(digestInputStream);
                IOUtils.closeQuietly(fileInputStream);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.warn(file.getName() + " 文件不存在！");
        }
        return md5Val;
    }
}
