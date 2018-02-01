package cn.yingsuo.im.server.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class RegisterRequest {


    private String ni_name;

    private String password;

    private String code;

    private String tel;

    private String tui_tel;

    public RegisterRequest(String ni_name, String password, String code, String tel, String tui_tel) {
        this.ni_name = ni_name;
        this.password = password;
        this.code = code;
        this.tel = tel;
        this.tui_tel = tui_tel;
    }

    public String getNi_name() {
        return ni_name;
    }

    public void setNi_name(String ni_name) {
        this.ni_name = ni_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTui_tel() {
        return tui_tel;
    }

    public void setTui_tel(String tui_tel) {
        this.tui_tel = tui_tel;
    }
}
