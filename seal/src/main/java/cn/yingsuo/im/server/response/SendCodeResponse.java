package cn.yingsuo.im.server.response;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class SendCodeResponse {

    private int code;
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
