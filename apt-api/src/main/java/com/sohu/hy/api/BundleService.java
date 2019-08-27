package com.sohu.hy.api;

import android.app.Activity;

import java.lang.reflect.Method;
import java.util.Objects;

public class BundleService {

    public static void bind(Activity target) {
        bind(target, false);
    }

    public static void bind(Activity target, boolean isBindParent) {
        try {
            String bundlePath;
            if (isBindParent) {
                bundlePath = "com.sohu.generate." + Objects.requireNonNull(target.getClass().getSuperclass()).getSimpleName() + "Bundle";
            } else {
                bundlePath = "com.sohu.generate." + target.getClass().getSimpleName() + "Bundle";
            }
            if (bundlePath.isEmpty()) return;
            Class<?> c = Class.forName(bundlePath);
            Method bind;
            if (isBindParent) {
                bind = c.getMethod("bind", target.getClass().getSuperclass());
            } else {
                bind = c.getMethod("bind", target.getClass());
            }
            bind.invoke(null, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
