package com.sohu.hy.api;

import android.app.Activity;

import java.lang.reflect.Method;

public class BundleService {

    public static void bind(Activity target) {
        try {
            boolean isContinueToBind;
            Class currentClass = target.getClass();
            do {
                isContinueToBind = isContinueToBind(target, currentClass);
                if (isContinueToBind) {
                    assert currentClass != null;
                    currentClass = currentClass.getSuperclass();
                }
            } while (isContinueToBind);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isContinueToBind(Activity target, Class<? extends Activity> targetClass) {
        try {
            String className = targetClass.getSimpleName();
            if (className.isEmpty()) return false;
            if (className.equals("AppCompatActivity")) return false;
            String bundlePath = "com.sohu.generate." + className + "Launcher";
            Class<?> c;
            try {
                c = Class.forName(bundlePath);
            }catch (Exception e){
                c = null;
            }
            if(c!=null){
                Method bind;
                try {
                    bind = c.getMethod("bind", targetClass);
                }catch(Exception e){
                    bind = null;
                }
                if (bind!=null){
                    bind.invoke(null, target);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
