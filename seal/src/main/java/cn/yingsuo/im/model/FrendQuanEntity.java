package cn.yingsuo.im.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

import cn.yingsuo.im.server.SealAction;
import io.rong.common.ParcelUtils;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class FrendQuanEntity implements Serializable{
    public static final int TYPE_TOP = 10;
    public static final int TYPE_ITEM = 0;
    public final static String TYPE_URL = "1";
    public final static String TYPE_IMG = "2";
    public final static String TYPE_VIDEO = "3";
    private int viewType;
    private String id;
    private String uid;
    private String content;
    private String[] img;
    private String is_del;
    private String add_time;
    private String addr;
    private String is_zhuan;
    private String zid;
    private String zan_num;
    private String zhuan_num;
    private String z_uid;
    private String ping_num;
    private String ni_name;
    private String head_img;
    private List<FrendZanEntity> zan;
    private List<FrendZanEntity> pinglun;
    private String zan_cou;

    private String type;//1:链接  2:图片 3:视频
    private boolean isExpand;


    public FrendQuanEntity() {
    }

    public FrendQuanEntity(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getImg() {
        String[] strs = new String[img.length];
        for (int i = 0; i < img.length; i++) {
            strs[i] = SealAction.BASE_URL + img[i];
        }
        return strs;
    }

    public boolean hasFavort() {
        if (zan != null && zan.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasComment() {
        if (pinglun != null && pinglun.size() > 0) {
            return true;
        }
        return false;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getIs_zhuan() {
        return is_zhuan;
    }

    public void setIs_zhuan(String is_zhuan) {
        this.is_zhuan = is_zhuan;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getZan_num() {
        return zan_num;
    }

    public void setZan_num(String zan_num) {
        this.zan_num = zan_num;
    }

    public String getZhuan_num() {
        return zhuan_num;
    }

    public void setZhuan_num(String zhuan_num) {
        this.zhuan_num = zhuan_num;
    }

    public String getZ_uid() {
        return z_uid;
    }

    public void setZ_uid(String z_uid) {
        this.z_uid = z_uid;
    }

    public String getPing_num() {
        return ping_num;
    }

    public void setPing_num(String ping_num) {
        this.ping_num = ping_num;
    }

    public String getNi_name() {
        return ni_name;
    }

    public void setNi_name(String ni_name) {
        this.ni_name = ni_name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public List<FrendZanEntity> getZan() {
        return zan;
    }

    public void setZan(List<FrendZanEntity> zan) {
        this.zan = zan;
    }

    public String getZan_cou() {
        return zan_cou;
    }

    public void setZan_cou(String zan_cou) {
        this.zan_cou = zan_cou;
    }

    public String getType() {
        if (img.length > 0) {
            type = TYPE_IMG;
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public List<FrendZanEntity> getPinglun() {
        return pinglun;
    }

    public void setPinglun(List<FrendZanEntity> pinglun) {
        this.pinglun = pinglun;
    }
}
