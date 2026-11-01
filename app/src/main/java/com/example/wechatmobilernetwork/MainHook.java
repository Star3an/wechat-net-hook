package com.example.wechatmobilernetwork;

import android.net.NetworkCapabilities;
import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(WECHAT_PACKAGE_NAME)) {
            return;
        }
        XposedBridge.log("LSPosed: 成功加载微信网络状态修改模块");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                XposedHelpers.findAndHookMethod(
                    NetworkCapabilities.class,
                    "hasTransport",
                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            int transportType = (int) param.args[0];
                            if (transportType == NetworkCapabilities.TRANSPORT_CELLULAR) {
                                param.setResult(true);
                            } else if (transportType == NetworkCapabilities.TRANSPORT_WIFI) {
                                param.setResult(false);
                            }
                        }
                    }
                );
            } catch (Throwable e) {
                XposedBridge.log("LSPosed Error hook hasTransport: " + e.getMessage());
            }
        }
    }
}
