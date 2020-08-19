package com.zzh.plugins;

public class PluginConfig {

    //一些默认无需插桩的类
    private static String[] UN_NEED_TRACE_CLASS = {"R.class", "R$", "Manifest", "BuildConfig"};


    public static boolean isNeedTraceClass(String fileName) {
        boolean isNeed = true;
        if (fileName.endsWith(".class")) {
            for (String data : UN_NEED_TRACE_CLASS) {
                if (fileName.contains(data)) {
                    isNeed = false;
                    break;
                }
            }
        } else {
            isNeed = false;
        }
        return isNeed;
    }
}
