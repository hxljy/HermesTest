package com.hxl.hermes.core.request;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class RequestParameter {

    private String parameterClassName;
    private String parameterValue;

    public RequestParameter(){}

    public RequestParameter(String parameterClassName, String parameterValue) {
        this.parameterClassName = parameterClassName;
        this.parameterValue = parameterValue;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName(String parameterClassName) {
        this.parameterClassName = parameterClassName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }



}
