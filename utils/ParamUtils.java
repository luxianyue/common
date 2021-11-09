package com.ys.healthcodelib.util;


import android.util.Base64;

import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Cipher;

public class ParamUtils {

    public static String sort(JSONObject params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String sort(Map<String, Object> params) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String sort(String[] array) {
        Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (String key : array) {
            sb.append(key);
        }
        return sb.toString();
    }

    public static String getStrPart(String str, int part) {
        int per = str.length() / part;
        int index = per - 1;
        StringBuilder sb = new StringBuilder();
        while (index < str.length()) {
            sb.append(str.charAt(index));
            index += per;
        }
        return sb.toString();
    }

    public static String encryptAESBase64(String data, String key, String ivVector) {
        byte[] buf;
        if (ivVector == null || ivVector.length() == 0) {
            buf = EncryptUtils.encryptAES(data.getBytes(), key.getBytes(), "AES/EBC/PKCS5Padding", null);
        } else {
            buf = EncryptUtils.encryptAES(data.getBytes(), key.getBytes(), "AES/CBC/PKCS7Padding", ivVector.getBytes(StandardCharsets.UTF_8));
        }
        return Base64.encodeToString(buf, Base64.NO_WRAP);
    }

    public static String[] maoPao(String[] strings) {
        for (int i = strings.length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (strings[j].compareTo(strings[j + 1]) > 0) {
                    String temp = strings[j];
                    strings[j] = strings[j + 1];
                    strings[j + 1] = temp;
                }
            }
        }
        return strings;

    }
}
