package cn.yingsuo.im.server.response;

import cn.yingsuo.im.server.SealAction;

/**
 * Created by zhangfenfen on 2018/1/13.
 */

public class NewFrendResultResponse {
    private String id;
    private String uid;
    private String fid;
    private String add_time;
    private String is_sure;
    private String type;
    private String role;
    private String ni_name;
    private String head_img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getIs_sure() {
        return is_sure;
    }

    public void setIs_sure(String is_sure) {
        this.is_sure = is_sure;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNi_name() {
        return ni_name;
    }

    public void setNi_name(String ni_name) {
        this.ni_name = ni_name;
    }

    public String getHead_img() {
        return SealAction.BASE_URL + head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
}
