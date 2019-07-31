package com.sohu.hy.api;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Method;

/**
 * Created by haoran on 2019/7/31.
 */
public class PageRouter {

    public static Builder of(Context ctx) {
        return new Builder(ctx);
    }

    public static final class Builder {

        private Context ctx;
        private String path;
        private Bundle params;
        private Uri uri;

        Builder(Context ctx) {
            this.ctx = ctx;
        }

        public Builder path(String path){
            this.path = path;
            return this;
        }

        public Builder setParams(Bundle params){
            this.params = params;
            return this;
        }

        public Builder setUri(Uri uri){
            this.uri = uri;
            return this;
        }

        public void navigation() {
            to(ctx,path,params,uri);
        }

        private void to(Context ctx,String path,Bundle bundle,Uri uri){

            try{
                String dispatcherClassPath = "com.sohu.generate.RouterDispatcher";
                Class<?> c = Class.forName(dispatcherClassPath);
                Method bind = c.getMethod("go", Context.class,String.class,Bundle.class,Uri.class);
                bind.invoke(null, ctx,path,bundle,uri);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }







}
