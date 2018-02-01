package cn.yingsuo.im.model;

import java.io.Serializable;

/**
 * Created by zhangfenfen on 2018/1/18.
 */

public class LocationAdressEntity implements Serializable{
    private String title;
    private String detail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
