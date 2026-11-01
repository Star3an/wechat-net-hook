package com.example.wechatmobilernetwork;

import android.net.NetworkCapabilities;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 可以先不限制包名，或者指定微信包名 "com.tencent.mm"
        if (!lpparam.packageName.equals("com.tencent.mm")) {
            return;
        }

        XposedBridge.log("WeChatMobileNetwork: Successfully hooked WeChat!");

        // Hook NetworkCapabilities 的 hasTransport 方法
        XposedHelpers.findAndHookMethod(
            "android.net.NetworkCapabilities",
            lpparam.classLoader,
            "hasTransport",
            int.class,
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int transportType = (int) param.args[0];
                    // TRANSPORT_WIFI 的常量值是 1
                    if (transportType == NetworkCapabilities.TRANSPORT_WIFI) {
                        // 强制将 Wi-Fi 识别结果修改为 false
                        param.setResult(false);
                    }
                    // TRANSPORT_CELLULAR 的常量值是 0，如果需要也可以强制返回 true
                    else if (transportType == NetworkCapabilities.TRANSPORT_CELLULAR) {
                        // param.setResult(true);
                    }
                }
            }
        );
    }
}
