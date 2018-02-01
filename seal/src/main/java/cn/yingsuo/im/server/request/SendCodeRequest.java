package cn.yingsuo.im.server.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class SendCodeRequest {

    private String tel;
    private String key;

//    private String phone;

    public SendCodeRequest(String tel, String key) {
        this.tel = tel;
        this.key = key;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
