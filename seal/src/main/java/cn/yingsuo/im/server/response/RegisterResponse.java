package cn.yingsuo.im.server.response;


import java.util.List;

/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class RegisterResponse {


    private int code;

    private String msg;

    private ResultEntity res;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResultEntity getRes() {
        return res;
    }

    public void setRes(ResultEntity res) {
        this.res = res;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class ResultEntity {
        private String uId;
        private String[] ys_id;

        public String getuId() {
            return uId;
        }

        public void setuId(String uId) {
            this.uId = uId;
        }

        public String[] getYs_id() {
            return ys_id;
        }

        public void setYs_id(String[] ys_id) {
            this.ys_id = ys_id;
        }
    }


}
