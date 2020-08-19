package com.zzh.plugins;

import org.apache.http.util.TextUtils;

/**
 * 车主邦
 * ---------------------------
 * <p>
 * Created by zhaozh on 2020/8/19.
 */
class MethodFilter {

    public static boolean isConstructor(String methodName) {
        return !TextUtils.isEmpty(methodName) && methodName.contains("<init>");
    }
}
