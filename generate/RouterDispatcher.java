package com.sohu.generate;

/**
 Do not modify!
 */
import android.os.Bundle;
import android.net.Uri;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.woaiqw.pagerouter.test.Test1Activity;
import com.woaiqw.pagerouter.test.Test2Activity;

public final class RouterDispatcher {

    public static void go(Context ctx,String path,Bundle extras, Uri uri){ 
        if(ctx==null)return;
        Intent intent = null;
        switch (path){
            case "Test2Activity":
               intent = new Intent(ctx, Test2Activity.class);
            break;
            case "Test1Activity":
               intent = new Intent(ctx, Test1Activity.class);
            break;

        }
        if (intent == null) return;
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (uri != null){
            intent.setData(uri);
        }
        ctx.startActivity(intent);

    }

}