package cn.yingsuo.im.server.response;

import java.util.List;

import cn.yingsuo.im.model.FrendQuanEntity;

/**
 * Created by AMing on 16/1/4.
 * Company RongCloud
 */
public class GetUserInfoByPhoneResponse {


    /**
     * code : 1
     * result : {"id":"10YVscJI3","nickname":"阿明","portraitUri":""}
     */

    private int code;

    private String msg;

    private List<UserInfoResponse> res;

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

    public List<UserInfoResponse> getRes() {
        return res;
    }

    public void setRes(List<UserInfoResponse> res) {
        this.res = res;
    }
}
