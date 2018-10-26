/*
 * 文件名:  JsonUtil.java
 * 版   权:  广州亚美信息科技有限公司
 * 创建人:  liguoliang
 * 创建时间:2018-01-15
 */

package com.wapp.browser.elegant.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @className: JsonUtil
 * @classDescription:
 * @author: liguoliang
 * @createTime: 2018/1/15
 */
public class JsonUtil {
    public static String getString(String json, String name) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.optString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getLong(String json, String name) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.optLong(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json) || classOfT == null) {
            return null;
        }
        T t = null;
        try {
            t = new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T fromJson(String json, Type type) {
        if (TextUtils.isEmpty(json) || type == null) {
            return null;
        }
        T t = null;
        try {
            t = new Gson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String toJson(Object object, Type typeOfSrc) {
        String ret = "";
        if (object == null || typeOfSrc == null) {
            return "";
        }
        try {
            ret = new Gson().toJson(object, typeOfSrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Map<String, String> jsonToMap(String strJson) {
        if (TextUtils.isEmpty(strJson)) {
            return null;
        }

        try {
            Map<String, String> map = new Gson().fromJson(strJson, new TypeToken<HashMap<String, String>>(){}.getType());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
