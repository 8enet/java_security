package com.javademo.ws;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zl on 16/4/7.
 */
public class OrderModel {

    public static final class Module{
        public static final String TOOLS="tools";
        public static final String USER="user";
    }

    public static final class Action{
        public static final String ADD="add";
        public static final String DELETE="delete";
        public static final String UPDATE="update";
        public static final String GET="get";
    }

    private int version;
    private String module;
    private String action;
    private Map<String,String> params;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParams(String key,String value){
        if(this.params == null){
            this.params=new HashMap<>(5);
        }
        this.params.put(key,value);
    }

    public void clearParams(){
        if(this.params != null){
            this.params.clear();
        }
    }

    public void reset(){
        action=null;
        module=null;
        clearParams();
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "version=" + version +
                ", module='" + module + '\'' +
                ", action='" + action + '\'' +
                ", params=" + params +
                '}';
    }
}
