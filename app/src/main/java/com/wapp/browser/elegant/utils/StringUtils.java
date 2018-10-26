package com.wapp.browser.elegant.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guangzhao Cai
 * @className:
 * @classDescription:
 * @createTime: 2018-10-26
 */
public class StringUtils {
    /**
     * 判断一个url是否符合规则
     * @param url
     * @return
     */
    public static boolean isUrlValid(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String regex = "^([\\s\\S]{1,20})://([\\s\\S]*)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
