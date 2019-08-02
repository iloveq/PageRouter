package com.woaiqw.pagerouter.bean;

import java.io.Serializable;

/**
 * Created by haoran on 2019-08-02.
 */
public class SerializableBean implements Serializable {

    public String type;

    public SerializableBean(String type){
        this.type = type;
    }

}
