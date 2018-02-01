package cn.yingsuo.im.model;

import java.util.List;

import cn.yingsuo.im.server.SealAction;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class LiaoTianShiEntity {
    private String id;
    private String img;
    private String name;
    private String province;
    private String city;
    private String country;
    private String content;
    private String aid;
    private String add_time;
    private String sort;
    private String is_tj;
    private String is_del;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIs_tj() {
        return is_tj;
    }

    public void setIs_tj(String is_tj) {
        this.is_tj = is_tj;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }
}
