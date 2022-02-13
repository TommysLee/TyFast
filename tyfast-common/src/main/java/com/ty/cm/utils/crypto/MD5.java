package com.ty.cm.utils.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

import static com.ty.cm.constant.Ty.DEFAULT_CHARSET;

/**
 * 基于Commons Codec实现的MD5加密
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class MD5 {

    /**
     * 对文本内容进行MD5加密
     *
     * @param text    ----> 需要加密的文本
     * @param charset ----> 字符集
     * @return String ----> 返回密文
     */
    public static String encrypt(String text, String... charset) {

        String cipherText = null;
        try {
            if (StringUtils.isNotBlank(text)) {
                String chartsetName = charset.length > 0 ? charset[0] : DEFAULT_CHARSET;
                cipherText = StringUtils.upperCase(DigestUtils.md5Hex(text.getBytes(chartsetName)));
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return cipherText;
    }
}
