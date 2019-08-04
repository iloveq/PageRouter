package com.sohu.generate;

/**
 Do not modify!
 */
import android.os.Bundle;
import android.content.Intent;
import com.woaiqw.pagerouter.test.Test1Activity;

public final class Test1ActivityBundle {


    public static final class Builder {

        private final Bundle args;
        
        public Builder() {
            this.args = new Bundle();
        }

        public Test1ActivityBundle.Builder setMsg(java.lang.String msg){
            args.putString("msg",msg);
            return this;
        }
        public Test1ActivityBundle.Builder setName(java.lang.Character name){
            args.putChar("name",name);
            return this;
        }
        public Test1ActivityBundle.Builder setMoney(double money){
            args.putDouble("money",money);
            return this;
        }
        public Test1ActivityBundle.Builder setBeanS(com.woaiqw.pagerouter.bean.SerializableBean beanS){
            args.putSerializable("beanS",beanS);
            return this;
        }
        public Test1ActivityBundle.Builder setBeanP(com.woaiqw.pagerouter.bean.ParcelableBean beanP){
            args.putParcelable("beanP",beanP);
            return this;
        }
        public Test1ActivityBundle.Builder setBm(android.graphics.Bitmap bm){
            args.putParcelable("bm",bm);
            return this;
        }
        public Test1ActivityBundle.Builder setStringArrayList(java.util.ArrayList<java.lang.String> stringArrayList){
            args.putStringArrayList("stringArrayList",stringArrayList);
            return this;
        }
        public Test1ActivityBundle.Builder setIntegerArrayList(java.util.ArrayList<java.lang.Integer> integerArrayList){
            args.putIntegerArrayList("integerArrayList",integerArrayList);
            return this;
        }
        public Bundle bundle() {
            return args;
        }

    }

    public static void bind(Test1Activity target) {
        Intent intent = target.getIntent();
        if (intent==null)return;
        Bundle source = intent.getExtras();
        if (source==null)return;
        if (source.containsKey("msg")) {
            target.msg = (java.lang.String) source.getString("msg");
        } else {
            throw new IllegalStateException("msg is required, but not found in the bundle.");
        }
        if (source.containsKey("name")) {
            target.name = (java.lang.Character) source.getChar("name");
        } else {
            throw new IllegalStateException("name is required, but not found in the bundle.");
        }
        if (source.containsKey("money")) {
            target.money = (double) source.getDouble("money");
        } else {
            throw new IllegalStateException("money is required, but not found in the bundle.");
        }
        if (source.containsKey("beanS")) {
            target.beanS = (com.woaiqw.pagerouter.bean.SerializableBean) source.getSerializable("beanS");
        } else {
            throw new IllegalStateException("beanS is required, but not found in the bundle.");
        }
        if (source.containsKey("beanP")) {
            target.beanP = (com.woaiqw.pagerouter.bean.ParcelableBean) source.getParcelable("beanP");
        } else {
            throw new IllegalStateException("beanP is required, but not found in the bundle.");
        }
        if (source.containsKey("bm")) {
            target.bm = (android.graphics.Bitmap) source.getParcelable("bm");
        } else {
            throw new IllegalStateException("bm is required, but not found in the bundle.");
        }
        if (source.containsKey("stringArrayList")) {
            target.stringArrayList = (java.util.ArrayList<java.lang.String>) source.getStringArrayList("stringArrayList");
        } else {
            throw new IllegalStateException("stringArrayList is required, but not found in the bundle.");
        }
        if (source.containsKey("integerArrayList")) {
            target.integerArrayList = (java.util.ArrayList<java.lang.Integer>) source.getIntegerArrayList("integerArrayList");
        } else {
            throw new IllegalStateException("integerArrayList is required, but not found in the bundle.");
        }

    }
}