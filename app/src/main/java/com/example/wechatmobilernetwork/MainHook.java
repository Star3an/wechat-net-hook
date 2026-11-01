package com.example.wechatmobilernetwork;

import android.net.NetworkCapabilities;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 仅针对微信生效
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
                    // TRANSPORT_WIFI 的常量值是 1，当微信检测 Wi-Fi 时强制返回 false
                    if (transportType == NetworkCapabilities.TRANSPORT_WIFI) {
                        param.setResult(false);
                    }
                }
            }
        );
    }
}
