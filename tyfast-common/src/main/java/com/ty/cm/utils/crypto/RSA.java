package com.ty.cm.utils.crypto;

import com.ty.cm.utils.PropertiesUtil;
import com.ty.cm.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RAS 算法（使用第三方供应商实现： Bouncy Castle）
 *
 * 说明：
 * 由于sun虚拟机自带的RSA解密填充模式使用的都是特殊的PADDING模式，而js中使用的padding其实就是特殊处理的部分，
 * 实际加密时是nopadding模式，所以无法直接使用sun自带的RSA算法在服务器端解密。
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class RSA {

    /**
     * 第三方RSA供应商：Bouncy Castle，简称BC。<br/>
     * 可以先注册到虚拟机中,再通过名称BC使用;<br/>
     * 也可以不注册,直接传入后使用
     */
    private static final String PROVIDER = "BC";

    /**
     * RSA算法标准名称
     */
    private static final String ALGORITHM = "RSA";

    /**
     * 填充模式
     */
    private static final String PADDING = "RSA/ECB/PKCS1Padding"; // JAVA RSA默认填充模式：RSA/ECB/PKCS1Padding

    /**
     * 公钥密钥字符串
     */
    public static String PUBLICKEYSTRING;

    /**
     * 私钥密钥字符串
     */
    public static String PRIVATEKEYSTRING;

    /**
     * 随机数范围
     */
    private final static int RANDOMNUM = 66666;

    /**
     * 初始化
     */
    static {
        // 注册BC到虚拟机中
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 若存在公私钥，则初始化公私钥
        final Map<String, String> keyMap = PropertiesUtil.loadedProperties("rsa.properties");
        if (keyMap.size() > 0) {
            PUBLICKEYSTRING = keyMap.get("publicKey");
            PRIVATEKEYSTRING = keyMap.get("privateKey");
        }
    }

    /**
     * 通过“公钥字符串”得到“公钥Key对象”
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @return PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {

        byte[] keyBytes;
        keyBytes = BASE64Decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 通过“私钥字符串”得到“私钥Key对象”
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {

        byte[] keyBytes;
        keyBytes = BASE64Decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 通过“密钥Key对象”得到“密钥字符串”（经过base64编码）
     *
     * @param key
     *            密钥对象
     * @return String
     */
    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = BASE64Encode(keyBytes);
        return s;
    }

    /**
     * 使用<b>BASE64编码</b>字节数组
     *
     * @param bytes
     *            ----> 字节数组
     * @return 返回编码后的字符串
     */
    public static String BASE64Encode(byte[] bytes) {

        return new String(Base64.encode(bytes));
    }

    /**
     * 使用<b>BASE64解码</b>字符串
     *
     * @param encodeStr
     *            ----> 已编码的字符串
     * @return 返回解码后的字节数组
     * @throws Exception
     */
    public static byte[] BASE64Decode(String encodeStr) throws Exception {

        return Base64.decode(encodeStr);
    }

    /**
     * 生成RSA公钥与私钥
     *
     * @param seedKey
     *            ----> 密钥种子[可选](任意字符串即可，保证公私钥唯一)
     * @return 返回公私钥List集合： 第一个为公钥； 第二个为私钥
     * @throws Exception
     */
    public static List<Key> GenerateRSAKey(String... seedKey) throws Exception {

        final List<Key> rsaKey = new ArrayList<Key>();
        // 种子改变后,生成的密钥对也会发生变化
        final String secureKey = seedKey != null && seedKey.length > 0 ? seedKey[0] : new Long(new Date().getTime() + new Random().nextInt(RANDOMNUM)).toString();

        // 密钥对生成器（RSA提供者查询：keyPairGen.getProvider()）RAS第三方供应商：Bouncy Castle
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        // 密钥位数与安全随机数
        keyPairGen.initialize(1024, new SecureRandom(secureKey.getBytes()));

        // 密钥对
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        System.out.println("Exponent：" + publicKey.getPublicExponent().toString(16));
        System.out.println("Modulus：" + publicKey.getModulus().toString(16));
        System.out.println();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 获取公/私钥字符串
        PUBLICKEYSTRING = getKeyString(publicKey);
        PRIVATEKEYSTRING = getKeyString(privateKey);

        // 将公私钥放入List并返回给调用者
        rsaKey.add(publicKey);
        rsaKey.add(privateKey);
        log.info(("RSA供应商：" + keyPairGen.getProvider()));
        // System.out.println("安全密钥： " + secureKey);
        return rsaKey;
    }

    /**
     * RSA加密
     *
     * @param data
     *            ----> 需要加密的数据
     * @return String 返回加密后的数据(经过BASE64处理)
     * @throws Exception
     */
    public static String encrypt(String data) {

        try {
            // 加解密类
            Cipher cipher = Cipher.getInstance(PADDING, PROVIDER); // Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(PUBLICKEYSTRING));
            byte[] enBytes = cipher.doFinal(data.getBytes());
            return BASE64Encode(enBytes);
        } catch (Exception e) {
            log.error("加密失败：" + e.getMessage(), e);
        }
        return data;
    }

    /**
     * RSA解密
     *
     * @param data
     *            ----> 需要解密的数据
     * @return String 返回明文数据
     * @throws Exception
     */
    public static String decrypt(String data) {

        try {
            // 加解密类
            Cipher cipher = Cipher.getInstance(PADDING, PROVIDER); // Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(PRIVATEKEYSTRING));
            byte[] deBytes = cipher.doFinal(BASE64Decode(data));
            return new String(deBytes);
        } catch (Exception e) {
            log.error("解密失败：" + e.getMessage(), e);
        }
        return data;
    }

    // 生成密钥对
    public static void main(String[] args) throws Exception  {
        /*List<Key> keys =  GenerateRSAKey();
        System.out.println("公钥：");
        System.out.println(getKeyString(keys.get(0)));

        System.out.println("私钥：");
        System.out.println(getKeyString(keys.get(1)));
        System.out.println();*/

        String url = "/1001/system/usr-role/grant/1/2";
        String regex = "((\\/)*([\\d]+))?([a-z\\/\\_\\-]+)((\\/)[\\d\\/]+)?";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        String url2 = "";
        if (matcher.matches()) {
            System.out.println("租户TenantID：" + matcher.group(3));
            System.out.println("URL：" + (url2 = matcher.group(4)));
            System.out.println("参数：" + matcher.group(5));
        }
        System.out.println("优化之后：");
        System.out.println(StringUtil.trim(url2, "/", "/"));
    }
}
