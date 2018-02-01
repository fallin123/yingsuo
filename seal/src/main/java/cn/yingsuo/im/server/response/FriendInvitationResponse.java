package cn.yingsuo.im.server.response;


/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class FriendInvitationResponse {

    /**
     * code : 200
     * result : {"action":"Sent"}
     * message : Request sent.
     */

    private int code;
    /**
     * action : Sent
     */
    private String msg;

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
}
