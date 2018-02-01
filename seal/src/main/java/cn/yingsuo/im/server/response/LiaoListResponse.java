package cn.yingsuo.im.server.response;

import java.util.List;

import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.model.LiaoTianShiEntity;

public class LiaoListResponse {

    private int code;

    private String msg;

    private List<LiaoTianShiEntity> res;

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

    public List<LiaoTianShiEntity> getRes() {
        return res;
    }

    public void setRes(List<LiaoTianShiEntity> res) {
        this.res = res;
    }
}
