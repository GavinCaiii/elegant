/*
 *   文件名:  ResponseConstruction.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-09-06
 */

/*
 *   文件名:  ResponseData.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-28
 */

package com.wapp.browser.elegant.browser.paramters;

import com.wapp.browser.elegant.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guangzhao Cai
 * @className: ResponseData
 * @classDescription: 返回参数构造器
 * @createTime: 2018-08-28
 */
public class ResponseData {

    private Map<String, String> params = new HashMap<>();
    private int code;
    private String message;

    private static ResponseData sInstance;

    public static ResponseData getInstance() {
        if (sInstance == null) {
            synchronized (ResponseData.class) {
                if (sInstance == null) {
                    sInstance = new ResponseData();
                }
            }
        }
        return sInstance;
    }

    private ResponseData() {}

    public String getResponse(int code, String[] keys, String... value) {
        this.code = code;
        if (code != 200) {
            this.message = "成功";
        } else {
            this.message = "失败";
        }
        for (int i = 0; i < keys.length; i++) {
            params.put(keys[i], value[i]);
        }
        return JsonUtil.toJson(this, ResponseData.class);
    }

    public String getSuccessfulResponse(String[] keys, String... value) {
        this.code = 200;
        this.message = "成功";
        for (int i = 0; i < keys.length; i++) {
            if (value[i] == null) {
                params.put(keys[i], "");
            } else {
                params.put(keys[i], value[i]);
            }
        }

        return JsonUtil.toJson(this, ResponseData.class);
    }
}
