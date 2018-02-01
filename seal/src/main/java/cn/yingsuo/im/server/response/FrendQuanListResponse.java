package cn.yingsuo.im.server.response;

import java.util.List;

import cn.yingsuo.im.model.FrendQuanEntity;

public class FrendQuanListResponse {

    private int code;

    private String msg;

    private List<FrendQuanEntity> res;

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

    public List<FrendQuanEntity> getRes() {
        return res;
    }

    public void setRes(List<FrendQuanEntity> res) {
        this.res = res;
    }
}
