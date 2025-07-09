package com.vecentek.licenseGenerate.generator.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author zyl
 */
public class AjaxResult<T> {
    /**
     * 响应代码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    private boolean success;

    /**
     * 响应结果
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public AjaxResult() {
    }

    /*public AjaxResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }*/

    public AjaxResult(int code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public AjaxResult(int code, String msg, T data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 成功
     *
     * @return
     */
    public static <T> AjaxResult<T> success() {
        return new AjaxResult(200, "操作成功！", true);
    }

    public static <T> AjaxResult<T> success(String msg) {
        return new AjaxResult(200, msg, true);
    }


    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static <T> AjaxResult success(T data) {

        return new AjaxResult(200, "操作成功！", data, true);
    }

    public static <T> AjaxResult success(String msg,T data) {

        return new AjaxResult(200, msg, data, true);
    }

    /**
     * 失败
     */
    public static AjaxResult error(int code, String msg) {
        AjaxResult rb = new AjaxResult();
        rb.setCode(code);
        rb.setMsg(msg);
        rb.setSuccess(false);
        return rb;
    }

    /**
     * 失败
     */
    public static AjaxResult error() {

        return new AjaxResult(200, "操作失败！", false);
    }

    /**
     * 失败
     */
    public static AjaxResult error(String msg) {

        return new AjaxResult(200, msg, false);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
