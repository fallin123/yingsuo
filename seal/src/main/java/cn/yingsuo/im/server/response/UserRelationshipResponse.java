package cn.yingsuo.im.server.response;

import java.util.List;

import cn.yingsuo.im.db.Friend;

/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class UserRelationshipResponse {

    /**
     * code : 200
     * result : [{"displayName":"","message":"手机号:18622222222昵称:的用户请求添加你为好友","status":11,"updatedAt":"2016-01-07T06:22:55.000Z","user":{"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}}]
     */

    private int code;

    private String msg;

    private List<NewFrendResultResponse> res;

    /**
     * displayName :
     * message : 手机号:18622222222昵称:的用户请求添加你为好友
     * status : 11
     * updatedAt : 2016-01-07T06:22:55.000Z
     * user : {"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}
     */
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

    public List<NewFrendResultResponse> getRes() {
        return res;
    }

    public void setRes(List<NewFrendResultResponse> res) {
        this.res = res;
    }
}
