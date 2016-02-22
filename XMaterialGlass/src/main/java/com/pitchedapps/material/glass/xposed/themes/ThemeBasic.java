package com.pitchedapps.material.glass.xposed.themes;

import android.app.Activity;
import android.content.res.XResources;
import android.os.Bundle;

import com.pitchedapps.material.glass.xposed.utilities.Common;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by 7681 on 2016-02-19.
 */
public class ThemeBasic implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    public static String MODULE_PATH = null;
    public XSharedPreferences prefs;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        prefs = new XSharedPreferences(Common.PACKAGE_NAME);
        prefs.makeWorldReadable();

    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        prefs.reload();

        if (!prefs.getBoolean("master_toggle", false)) {
            return;
        }

//        final XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);

        if (prefs.getBoolean("Adaway_layers", false) && resparam.packageName.equals("org.adaway")) {
            XResources.setSystemWideReplacement("android", "color", "primary_material_dark", 0xFFB71C1C);
        }

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String pname = lpparam.packageName;
        prefs.reload();

        if (!prefs.getBoolean("master_toggle", false)) {
            return;
        }

        if (!(prefs.getBoolean("Adaway_layers", false) && lpparam.packageName.equals("org.adaway"))

                ) {
            return;
        }

        Common.xLog("ThemeBasic enabled");

        findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {
                prefs.reload();
                Activity a = (Activity) param.thisObject;
                Common.xLog("ThemeBasic enabled for " + pname);
                a.setTheme(android.R.style.Theme_DeviceDefault);

                if (prefs.getBoolean("Adaway_layers", false)) {
                    a.getWindow().setStatusBarColor(0xFF9C2020);
                }
            }
        });
    }
}