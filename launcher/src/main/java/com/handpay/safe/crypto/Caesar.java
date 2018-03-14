package com.handpay.safe.crypto;

/**
 * @author sxshi on 2016/7/21.
 * @email:emotiona_xiaoshi@126.com
 */
public class Caesar {
    private final static String defaultKeys = "zAeOm40ZHjUnHbOOOklsdfsinjdfsgmlkhfdhbhjdgtrtvbhjbasdf";
    /**
     * 根据字符串获取凯撒密码数组
     * @param keyString  密码字符串
     * @return 密码数组
     */
    private static int[] getKeys(String keyString) {
        int[] keys = new int[keyString.length()];
        for (int i = 0; i < keyString.length(); i++) {
            keys[i] = (keyString.charAt(i)) % 26;
        }
        return keys;
    }
    /**
     * 凯撒加密
     *
     * @param plainString
     *            明文
     * @param keyString
     *            密码
     * @return 密文
     */
    public static String caesarEncryption(String plainString, String keyString) {
        int[] keys = getKeys(keyString);
        String result = "";
        for (int i = 0; i < plainString.length(); i++) {
            int key = keys[i % keys.length];
            int c = plainString.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (c - 'a' + key) % 26 + 'a';
            } else if (c >= 'A' && c <= 'Z') {
                c = (c - 'A' + key) % 26 + 'A';
            } else if (c >= '0' && c <= '9') {
                int k = key % 10;
                c = (c - '0' + k) % 10 + '0';
            }
            result = result + (char) c;
        }
        return result;
    }

    /**
     * 使用默认密码凯撒加密
     *
     * @param plainString
     *            明文
     * @return 密文
     */
    public static String caesarEncryption(String plainString) {
        return caesarEncryption(plainString,defaultKeys);

    }
    /**
     * 凯撒解密
     *
     * @param cipherString
     *            密文
     * @param keyString
     *            密码
     * @return 明文字符串
     */
    public static String caesarDeciphering(String cipherString, String keyString) {
        int[] keys = getKeys(keyString);
        String result = "";
        for (int i = 0; i < cipherString.length(); i++) {
            int key = keys[i % keys.length];
            int c = cipherString.charAt(i);
            if (c >= 'a' && c <= 'z') {
                c = (26 + c - 'a' - key) % 26 + 'a';
            } else if (c >= 'A' && c <= 'Z') {
                c = (26 + c - 'A' - key) % 26 + 'A';
            } else if (c >= '0' && c <= '9') {
                int k = key % 10;
                c = (10 + c - '0' - k) % 10 + '0';
            }
            result = result + (char) c;
        }
        return result;
    }

    /**
     * 使用默认密码凯撒解密
     *
     * @param cipherString
     *            密文
     * @return 明文字符串
     */
    public static String caesarDeciphering(String cipherString) {
        return caesarDeciphering(cipherString,defaultKeys);
    }

}
