package cn.yingsuo.im.server.response;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class LoginResponse {

    /**
     * code : 200
     * result : {"id":"t1hWCOGvX","token":"B0DA/kKanJviD5xxUzhwsEFIJad0/86YwGxBwz1417WFQi/Vr2OJay26s5IFDffGZaUYRMAkvN0ikvOcTl7RN9JilKZlosfQ"}
     */

    private int code;
    private String msg;
    /**
     * id : t1hWCOGvX
     * token : B0DA/kKanJviD5xxUzhwsEFIJad0/86YwGxBwz1417WFQi/Vr2OJay26s5IFDffGZaUYRMAkvN0ikvOcTl7RN9JilKZlosfQ
     */

    private UserInfoResponse res;

    public void setCode(int code) {
        this.code = code;
    }



    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserInfoResponse getRes() {
        return res;
    }

    public void setRes(UserInfoResponse res) {
        this.res = res;
    }

    /* public static class ResultEntity {
        private String id;
        private String token;

        public void setId(String id) {
            this.id = id;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getId() {
            return id;
        }

        public String getToken() {
            return token;
        }
    }*/
}
