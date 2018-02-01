package cn.yingsuo.im.server;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.yingsuo.im.App;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.network.net.ApiHelp;
import cn.yingsuo.im.server.network.net.INetWorkCallBack;
import cn.yingsuo.im.server.network.net.OkHttpHelp;
import cn.yingsuo.im.server.request.AddGroupMemberRequest;
import cn.yingsuo.im.server.request.AddToBlackListRequest;
import cn.yingsuo.im.server.request.ChangePasswordRequest;
import cn.yingsuo.im.server.request.CheckPhoneRequest;
import cn.yingsuo.im.server.request.CreateGroupRequest;
import cn.yingsuo.im.server.request.DeleteFriendRequest;
import cn.yingsuo.im.server.request.DeleteGroupMemberRequest;
import cn.yingsuo.im.server.request.DismissGroupRequest;
import cn.yingsuo.im.server.request.JoinGroupRequest;
import cn.yingsuo.im.server.request.QuitGroupRequest;
import cn.yingsuo.im.server.request.RemoveFromBlacklistRequest;
import cn.yingsuo.im.server.request.SetFriendDisplayNameRequest;
import cn.yingsuo.im.server.request.SetGroupDisplayNameRequest;
import cn.yingsuo.im.server.request.SetGroupNameRequest;
import cn.yingsuo.im.server.request.SetGroupPortraitRequest;
import cn.yingsuo.im.server.request.VerifyCodeRequest;
import cn.yingsuo.im.server.response.AddGroupMemberResponse;
import cn.yingsuo.im.server.response.AddToBlackListResponse;
import cn.yingsuo.im.server.response.AgreeFriendsResponse;
import cn.yingsuo.im.server.response.BindYsIdResponse;
import cn.yingsuo.im.server.response.ChangePasswordResponse;
import cn.yingsuo.im.server.response.CheckPhoneResponse;
import cn.yingsuo.im.server.response.CreateGroupResponse;
import cn.yingsuo.im.server.response.DefaultConversationResponse;
import cn.yingsuo.im.server.response.DeleteFriendResponse;
import cn.yingsuo.im.server.response.DeleteGroupMemberResponse;
import cn.yingsuo.im.server.response.DismissGroupResponse;
import cn.yingsuo.im.server.response.FrendQuanListResponse;
import cn.yingsuo.im.server.response.FriendInvitationResponse;
import cn.yingsuo.im.server.response.GetBlackListResponse;
import cn.yingsuo.im.server.response.GetFriendInfoByIDResponse;
import cn.yingsuo.im.server.response.GetGroupInfoResponse;
import cn.yingsuo.im.server.response.GetGroupMemberResponse;
import cn.yingsuo.im.server.response.GetGroupResponse;
import cn.yingsuo.im.server.response.GetTokenResponse;
import cn.yingsuo.im.server.response.GetUserInfoByIdResponse;
import cn.yingsuo.im.server.response.GetUserInfoByPhoneResponse;
import cn.yingsuo.im.server.response.GetUserInfosResponse;
import cn.yingsuo.im.server.response.JoinGroupResponse;
import cn.yingsuo.im.server.response.LiaoListResponse;
import cn.yingsuo.im.server.response.LoginResponse;
import cn.yingsuo.im.server.response.PingQuanResponse;
import cn.yingsuo.im.server.response.QiNiuTokenResponse;
import cn.yingsuo.im.server.response.QuitGroupResponse;
import cn.yingsuo.im.server.response.RegisterResponse;
import cn.yingsuo.im.server.response.RemoveFromBlackListResponse;
import cn.yingsuo.im.server.response.RestPasswordResponse;
import cn.yingsuo.im.server.response.SendCodeResponse;
import cn.yingsuo.im.server.response.SetFriendDisplayNameResponse;
import cn.yingsuo.im.server.response.SetGroupDisplayNameResponse;
import cn.yingsuo.im.server.response.SetGroupNameResponse;
import cn.yingsuo.im.server.response.SetGroupPortraitResponse;
import cn.yingsuo.im.server.response.SetNameResponse;
import cn.yingsuo.im.server.response.SyncTotalDataResponse;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.response.UserRelationshipResponse;
import cn.yingsuo.im.server.response.VerifyCodeResponse;
import cn.yingsuo.im.server.response.VersionResponse;
import cn.yingsuo.im.server.response.ZanQuanResponse;
import cn.yingsuo.im.server.utils.NLog;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.json.JsonMananger;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class SealAction extends BaseAction {
    public static final String BASE_URL = "http://114.115.207.155";
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String ENCODING = "utf-8";
    private final static String KEY = "29067275e60e29544639d4551d953666";

    private final String regist = "login/regist";//注册
    private final String bind_ysid = "login/bind_ysid";//绑定应索号
    private final String find_pwd = "login/find_pwd";//找回密码
    private final String login = "login/login";//登录
    private final String other_login = "login/other_login";//第三方登录
    private final String other_shouquan = "login/other_shouquan";//第三方登录授权
    private final String regsend = "login/regsend";//注册短信验证码
    private final String cmfsend = "login/cmfsend";//找回密码短信验证码
    private final String friend = "friend/friend";//获取好友列表
    private final String quan = "quan/quan";//好友圈
    private final String my_quan = "quan/my_quan";//我的好友圈
    private final String add_quan = "quan/add_quan";//发布好友圈
    private final String edit_img = "user/edit_img";//修改用户头像
    private final String edit_name = "user/edit_name";//修改昵称接口
    private final String edit_sex = "user/edit_sex";//修改性别
    private final String edit_addr = "user/edit_addr";//修改地区
    private final String edit_slogon = "user/edit_slogon";//修改个性签名
    private final String ser_friend = "friend/ser_friend";//搜索好友
    private final String add_friend = "friend/add_friend";//添加好友
    private final String que_friend = "user/que_friend";//确认好友
    private final String liao_lists = "liao/lists";//获取所有聊天室
    private final String zan_quan = "quan/zan_quan";//点赞好友圈
    private final String ping_quan = "quan/ping_quan";//评论好友圈


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public SealAction(Context context) {
        super(context);
    }


    /**
     * 检查手机是否被注册
     *
     * @param region 国家码
     * @param phone  手机号
     * @throws HttpException
     */
    public CheckPhoneResponse checkPhoneAvailable(String region, String phone) throws HttpException {
        String url = getURL("user/check_phone_available");
        String json = JsonMananger.beanToJson(new CheckPhoneRequest(phone, region));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        CheckPhoneResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CheckPhoneResponse.class);
        }
        return response;
    }


    /**
     * 注册短信验证码
     *
     * @param tel 手机号
     * @throws HttpException
     */
    public SendCodeResponse regsend(String tel) throws HttpException {
        String url = getURL(regsend);
        //String json = JsonMananger.beanToJson(new SendCodeRequest(phone,KEY));
        Map<String, String> map = new HashMap<>();
        map.put("tel", tel);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SendCodeResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
        }
        return response;
    }

    /**
     * 找回密码短信验证码
     *
     * @param tel 手机号
     * @throws HttpException
     */
    public SendCodeResponse cmfsend(String tel) throws HttpException {
        String url = getURL(cmfsend);
        //String json = JsonMananger.beanToJson(new SendCodeRequest(phone,KEY));
        Map<String, String> map = new HashMap<>();
        map.put("tel", tel);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SendCodeResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = JsonMananger.jsonToBean(result, SendCodeResponse.class);
        }
        return response;
    }

    /*
    * 200: 验证成功
    1000: 验证码错误
    2000: 验证码过期
    异常返回，返回的 HTTP Status Code 如下：

    400: 错误的请求
    500: 应用服务器内部错误
    * */

    /**
     * 验证验证码是否正确(必选先用手机号码调sendcode)
     *
     * @param region 国家码
     * @param phone  手机号
     * @throws HttpException
     */
    public VerifyCodeResponse verifyCode(String region, String phone, String code) throws HttpException {
        String url = getURL("user/verify_code");
        String json = JsonMananger.beanToJson(new VerifyCodeRequest(region, phone, code));
        VerifyCodeResponse response = null;
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            Log.e("VerifyCodeResponse", result);
            response = jsonToBean(result, VerifyCodeResponse.class);
        }
        return response;
    }

    /**
     * 注册
     *
     * @param nickname 昵称
     * @param password 密码
     * @param code     验证码
     * @throws HttpException
     */
    public RegisterResponse register(String nickname, String password, String tel, String tui_tel, String code, String last_addr, String jing, String wei) throws HttpException {
        String url = getURL(regist);
        Map<String, String> map = new HashMap<>();
        map.put("ni_name", nickname);
        map.put("tel", tel);
        map.put("password", password);
        map.put("tui_tel", tui_tel);
        map.put("code", code);
        map.put("last_addr", last_addr);
        map.put("jing", jing);
        map.put("wei", wei);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RegisterResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RegisterResponse", result);
            response = jsonToBean(result, RegisterResponse.class);
        }
        return response;
    }

    /**
     * 绑定应索号
     *
     * @param uid   id
     * @param ys_id 注册返回的应索号
     * @throws HttpException
     */
    public BindYsIdResponse bindYsid(String uid, String ys_id) throws HttpException {
        String url = getURL(bind_ysid);
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("ys_id", ys_id);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        BindYsIdResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RegisterResponse", result);
            response = jsonToBean(result, BindYsIdResponse.class);
        }
        return response;
    }

    /**
     * 登录: 登录成功后，会设置 Cookie，后续接口调用需要登录的权限都依赖于 Cookie。
     *
     * @param ys_id    应索号
     * @param tel      手机号
     * @param password 密码
     * @throws HttpException
     */
    public LoginResponse login(String tel, String password, String ys_id, String last_addr, String jing, String wei) throws HttpException {
        String uri = getURL(login);
        Map<String, String> map = new HashMap<>();
        map.put("tel", tel);
        map.put("password", password);
        map.put("ys_id", ys_id);
        map.put("last_addr", last_addr);
        map.put("jing", jing);
        map.put("wei", wei);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
        LoginResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("LoginResponse", result);
            response = JsonMananger.jsonToBean(result, LoginResponse.class);
        }
        return response;
    }

    /**
     * 第三方 登录: 登录成功后，会设置 Cookie，后续接口调用需要登录的权限都依赖于 Cookie。
     *
     * @param openid 第三方授权id
     * @param type   1微博2微信 3QQ
     * @throws HttpException
     */
    public LoginResponse loginShareSdk(String openid, String type, String last_addr, String jing, String wei) throws HttpException {
        String uri = getURL(other_login);
        Map<String, String> map = new HashMap<>();
        map.put("openid", openid);
        map.put("type", type);
        map.put("last_addr", last_addr);
        map.put("jing", jing);
        map.put("wei", wei);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
        LoginResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("LoginResponse", result);
            response = JsonMananger.jsonToBean(result, LoginResponse.class);
        }
        return response;
    }

    /**
     * 第三方 登录:授权
     *
     * @param uid  第三方授权id
     * @param type 1微博2微信 3QQ
     * @throws HttpException
     */
    public LoginResponse loginShareSdkShouQuan(String uid, String tel, String openid, String type) throws HttpException {
        String uri = getURL(other_shouquan);
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("tel", tel);
        map.put("openid", openid);
        map.put("type", type);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
        LoginResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("LoginResponse", result);
            response = JsonMananger.jsonToBean(result, LoginResponse.class);
        }
        return response;
    }


    /**
     * 获取 token 前置条件需要登录   502 坏的网关 测试环境用户已达上限
     *
     * @throws HttpException
     */
    public GetTokenResponse getToken() throws HttpException {
        String url = getURL("user/get_token");
        String result = httpManager.get(url);
        GetTokenResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("GetTokenResponse", result);
            response = jsonToBean(result, GetTokenResponse.class);
        }
        return response;
    }

    /**
     * 设置自己的昵称
     *
     * @param nickname 昵称
     * @throws HttpException
     */
    public SetNameResponse setName(String nickname) throws HttpException {
        String url = getURL(edit_name);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("name", nickname);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SetNameResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetNameResponse.class);
        }
        return response;
    }

    /**
     * 设置自己的昵称
     *
     * @param sex 1男 2女
     * @throws HttpException
     */
    public SetNameResponse setSex(String sex) throws HttpException {
        String url = getURL(edit_sex);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("sex", sex);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SetNameResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetNameResponse.class);
        }
        return response;
    }

    /**
     * 设置自己的昵称
     *
     * @param prov    省
     * @param city    市
     * @param country 县区
     * @throws HttpException
     */
    public SetNameResponse setAdress(String prov, String city, String country) throws HttpException {
        String url = getURL(edit_addr);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("prov", prov);
        map.put("city", city);
        map.put("country", country);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SetNameResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetNameResponse.class);
        }
        return response;
    }

    /**
     * 设置自己的个性签名
     *
     * @param slogon 签名
     * @throws HttpException
     */
    public SetNameResponse setSlogon(String slogon) throws HttpException {
        String url = getURL(edit_slogon);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("slogon", slogon);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SetNameResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetNameResponse.class);
        }
        return response;
    }


    /**
     * 设置用户头像
     *
     * @param
     * @throws HttpException
     */
    public void setPortrait(final Context context, final ApiHelp.IApiCallBack callBack, File file) throws HttpException {
        String url = getURL(edit_img);
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("key", KEY);
        http.postAsynFile(url, file, map, new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        String res = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            String imageUrl = "";
                            if ("1".equals(returnCode)) {
                                if (jo.has("res")) {
                                    imageUrl = jo.getString("res");
                                }
                                callBack.onApiCallBack(BASE_URL + imageUrl, 1);
                            } else {
                                NToast.longToast(context, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    /**
     * 当前登录用户通过旧密码设置新密码  前置条件需要登录才能访问
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws HttpException
     */
    public ChangePasswordResponse changePassword(String oldPassword, String newPassword) throws HttpException {
        String url = getURL("user/change_password");
        String json = JsonMananger.beanToJson(new ChangePasswordRequest(oldPassword, newPassword));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        ChangePasswordResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("ChangePasswordResponse", result);
            response = jsonToBean(result, ChangePasswordResponse.class);
        }
        return response;
    }


    /**
     * 通过手机验证码重置密码
     *
     * @param password 密码，6 到 20 个字节，不能包含空格
     * @param tel      会员电话
     * @param code     验证码
     */
    public RestPasswordResponse restPassword(String tel, String password, String code) throws HttpException {
        String uri = getURL(find_pwd);
        Map<String, String> map = new HashMap<>();
        map.put("tel", tel);
        map.put("password", password);
        map.put("code", code);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, uri, entity, CONTENT_TYPE);
        RestPasswordResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            NLog.e("RestPasswordResponse", result);
            response = jsonToBean(result, RestPasswordResponse.class);
        }
        return response;
    }

    /**
     * 根据 id 去服务端查询用户信息
     *
     * @param userid 用户ID
     * @throws HttpException
     */
    public GetUserInfoByIdResponse getUserInfoById(String userid) throws HttpException {
        String url = getURL("user/" + userid);
        String result = httpManager.get(url);
        GetUserInfoByIdResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetUserInfoByIdResponse.class);
        }
        return response;
    }


    /**
     * 通过国家码和手机号查询用户信息
     *
     * @param key_id 手机号|应索号
     * @throws HttpException
     */
    public GetUserInfoByPhoneResponse getUserInfoFromPhone(String key_id) throws HttpException {
        String url = getURL(ser_friend);
        Map<String, String> map = new HashMap<>();
        map.put("key_id", key_id);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        GetUserInfoByPhoneResponse response = null;
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetUserInfoByPhoneResponse.class);
        }
        return response;
    }


    /**
     * 发送好友邀请
     *
     * @param uid 会员id
     * @param fid 好友id
     * @throws HttpException
     */
    public FriendInvitationResponse sendFriendInvitation(String uid, String fid) throws HttpException {
        String url = getURL(add_friend);
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("fid", fid);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        FriendInvitationResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, FriendInvitationResponse.class);
        }
        return response;
    }


    /**
     * 获取发生过用户关系的列表
     *
     * @throws HttpException
     */
    public UserRelationshipResponse getAllUserRelationship() throws HttpException {
        String url = getURL(friend);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        UserRelationshipResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, UserRelationshipResponse.class);
        }
        return response;
    }

    /**
     * 获取好友圈列表
     *
     * @throws HttpException
     */
    public FrendQuanListResponse getFrendQuanList() throws HttpException {
        String url = getURL(quan);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        uid = "10013";
        map.put("uid", uid);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        FrendQuanListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, FrendQuanListResponse.class);
        }
        return response;
    }

    /**
     * 获取我的好友圈列表
     *
     * @throws HttpException
     */
    public FrendQuanListResponse getMyFrendQuanList() throws HttpException {
        String url = getURL(my_quan);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", "10014");
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        FrendQuanListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, FrendQuanListResponse.class);
        }
        return response;
    }

    /**
     * 发布好友圈
     *
     * @throws HttpException
     */
    public void sendFrendQuan(final Context context, String content, String addr, List<File> files, final ApiHelp.IApiCallBack callBack) throws HttpException {
        String url = getURL(add_quan);
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        Map<String, String> map = new HashMap<>();
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        String uid = "";
        if (userInfoResponse != null) {
            uid = userInfoResponse.getId();
        }
        map.put("uid", uid);
        map.put("content", content);
        map.put("addr", addr);
        map.put("key", KEY);
        http.postAsynFileList(url, files, map, new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        String res = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            String imageUrl = "";
                            if ("1".equals(returnCode)) {
                              /*  if (jo.has("res")) {
                                    imageUrl = jo.getString("res");
                                }*/
                                callBack.onApiCallBack(BASE_URL + imageUrl, 1);
                            } else {
                                NToast.longToast(context, msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取聊天室列表
     *
     * @throws HttpException
     */
    public LiaoListResponse getLiaoList() throws HttpException {
        String url = getURL(liao_lists);
        Map<String, String> map = new HashMap<>();
        map.put("p", "1");
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        LiaoListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, LiaoListResponse.class);
        }
        return response;
    }


    /**
     * 根据userId去服务器查询好友信息
     *
     * @throws HttpException
     */
    public GetFriendInfoByIDResponse getFriendInfoByID(String userid) throws HttpException {
        String url = getURL("friendship/" + userid + "/profile");
        String result = httpManager.get(url);
        GetFriendInfoByIDResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetFriendInfoByIDResponse.class);
        }
        return response;
    }

    /**
     * 同意对方好友邀请
     *
     * @param id 好友记录id(好友申请列表中获取)
     * @throws HttpException
     */
    public AgreeFriendsResponse agreeFriends(String id, String uid) throws HttpException {
        String url = getURL(que_friend);
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("uid", uid);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        AgreeFriendsResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AgreeFriendsResponse.class);
        }
        return response;
    }

    /**
     * 点赞好友圈
     *
     * @param zid 要点赞的好友圈id
     * @throws HttpException
     */
    public ZanQuanResponse zanQuan(String uid, String zid) throws HttpException {
        String url = getURL(zan_quan);
        Map<String, String> map = new HashMap<>();
        map.put("zid", zid);
        map.put("uid", uid);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        ZanQuanResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, ZanQuanResponse.class);
        }
        return response;
    }

    /**
     * 评论好友圈
     *
     * @param uid 要点赞的好友圈id
     * @param pid 要评论的好友圈id
     * @param hid 回复的评论记录id
     * @param content 评论内容
     * @throws HttpException
     */
    public PingQuanResponse pingQuan(String uid, String pid, String hid, String content) throws HttpException {
        String url = getURL(ping_quan);
        Map<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("pid", pid);
        map.put("hid", hid);
        map.put("content", content);
        String parmStr = getRequestData(map, "utf-8");
        StringEntity entity = null;
        try {
            entity = new StringEntity(parmStr, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        PingQuanResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, PingQuanResponse.class);
        }
        return response;
    }

    /**
     * 创建群组
     *
     * @param name      群组名
     * @param memberIds 群组成员id
     * @throws HttpException
     */
    public CreateGroupResponse createGroup(String name, List<String> memberIds) throws HttpException {
        String url = getURL("group/create");
        String json = JsonMananger.beanToJson(new CreateGroupRequest(name, memberIds));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        CreateGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, CreateGroupResponse.class);
        }
        return response;
    }

    /**
     * 创建者设置群组头像
     *
     * @param groupId     群组Id
     * @param portraitUri 群组头像
     * @throws HttpException
     */
    public SetGroupPortraitResponse setGroupPortrait(String groupId, String portraitUri) throws HttpException {
        String url = getURL("group/set_portrait_uri");
        String json = JsonMananger.beanToJson(new SetGroupPortraitRequest(groupId, portraitUri));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        SetGroupPortraitResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetGroupPortraitResponse.class);
        }
        return response;
    }

    /**
     * 获取当前用户所属群组列表
     *
     * @throws HttpException
     */
    public GetGroupResponse getGroups() throws HttpException {
        String url = getURL("user/groups");
        String result = httpManager.get(mContext, url);
        GetGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetGroupResponse.class);
        }
        return response;
    }

    /**
     * 根据 群组id 查询该群组信息   403 群组成员才能看
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public GetGroupInfoResponse getGroupInfo(String groupId) throws HttpException {
        String url = getURL("group/" + groupId);
        String result = httpManager.get(mContext, url);
        GetGroupInfoResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetGroupInfoResponse.class);
        }
        return response;
    }

    /**
     * 根据群id获取群组成员
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public GetGroupMemberResponse getGroupMember(String groupId) throws HttpException {
        String url = getURL("group/" + groupId + "/members");
        String result = httpManager.get(mContext, url);
        GetGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 当前用户添加群组成员
     *
     * @param groupId   群组Id
     * @param memberIds 成员集合
     * @throws HttpException
     */
    public AddGroupMemberResponse addGroupMember(String groupId, List<String> memberIds) throws HttpException {
        String url = getURL("group/add");
        String json = JsonMananger.beanToJson(new AddGroupMemberRequest(groupId, memberIds));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        AddGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AddGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 创建者将群组成员提出群组
     *
     * @param groupId   群组Id
     * @param memberIds 成员集合
     * @throws HttpException
     */
    public DeleteGroupMemberResponse deleGroupMember(String groupId, List<String> memberIds) throws HttpException {
        String url = getURL("group/kick");
        String json = JsonMananger.beanToJson(new DeleteGroupMemberRequest(groupId, memberIds));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        DeleteGroupMemberResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DeleteGroupMemberResponse.class);
        }
        return response;
    }

    /**
     * 创建者更改群组昵称
     *
     * @param groupId 群组Id
     * @param name    群昵称
     * @throws HttpException
     */
    public SetGroupNameResponse setGroupName(String groupId, String name) throws HttpException {
        String url = getURL("group/rename");
        String json = JsonMananger.beanToJson(new SetGroupNameRequest(groupId, name));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        SetGroupNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetGroupNameResponse.class);
        }
        return response;
    }

    /**
     * 用户自行退出群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public QuitGroupResponse quitGroup(String groupId) throws HttpException {
        String url = getURL("group/quit");
        String json = JsonMananger.beanToJson(new QuitGroupRequest(groupId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        QuitGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, QuitGroupResponse.class);
        }
        return response;
    }

    /**
     * 创建者解散群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public DismissGroupResponse dissmissGroup(String groupId) throws HttpException {
        String url = getURL("group/dismiss");
        String json = JsonMananger.beanToJson(new DismissGroupRequest(groupId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        DismissGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DismissGroupResponse.class);
        }
        return response;
    }


    /**
     * 修改自己的当前的群昵称
     *
     * @param groupId     群组Id
     * @param displayName 群名片
     * @throws HttpException
     */
    public SetGroupDisplayNameResponse setGroupDisplayName(String groupId, String displayName) throws HttpException {
        String url = getURL("group/set_display_name");
        String json = JsonMananger.beanToJson(new SetGroupDisplayNameRequest(groupId, displayName));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        SetGroupDisplayNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetGroupDisplayNameResponse.class);
        }
        return response;
    }

    /**
     * 删除好友
     *
     * @param friendId 好友Id
     * @throws HttpException
     */
    public DeleteFriendResponse deleteFriend(String friendId) throws HttpException {
        String url = getURL("friendship/delete");
        String json = JsonMananger.beanToJson(new DeleteFriendRequest(friendId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        DeleteFriendResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DeleteFriendResponse.class);
        }
        return response;
    }

    /**
     * 设置好友的备注名称
     *
     * @param friendId    好友Id
     * @param displayName 备注名
     * @throws HttpException
     */
    public SetFriendDisplayNameResponse setFriendDisplayName(String friendId, String displayName) throws HttpException {
        String url = getURL("friendship/set_display_name");
        String json = JsonMananger.beanToJson(new SetFriendDisplayNameRequest(friendId, displayName));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        SetFriendDisplayNameResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SetFriendDisplayNameResponse.class);
        }
        return response;
    }

    /**
     * 获取黑名单
     *
     * @throws HttpException
     */
    public GetBlackListResponse getBlackList() throws HttpException {
        String url = getURL("user/blacklist");
        String result = httpManager.get(mContext, url);
        GetBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetBlackListResponse.class);
        }
        return response;
    }

    /**
     * 加入黑名单
     *
     * @param friendId 群组Id
     * @throws HttpException
     */
    public AddToBlackListResponse addToBlackList(String friendId) throws HttpException {
        String url = getURL("user/add_to_blacklist");
        String json = JsonMananger.beanToJson(new AddToBlackListRequest(friendId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        AddToBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, AddToBlackListResponse.class);
        }
        return response;
    }

    /**
     * 移除黑名单
     *
     * @param friendId 好友Id
     * @throws HttpException
     */
    public RemoveFromBlackListResponse removeFromBlackList(String friendId) throws HttpException {
        String url = getURL("user/remove_from_blacklist");
        String json = JsonMananger.beanToJson(new RemoveFromBlacklistRequest(friendId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        RemoveFromBlackListResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, RemoveFromBlackListResponse.class);
        }
        return response;
    }

    public QiNiuTokenResponse getQiNiuToken() throws HttpException {
        String url = getURL("user/get_image_token");
        String result = httpManager.get(mContext, url);
        QiNiuTokenResponse q = null;
        if (!TextUtils.isEmpty(result)) {
            q = jsonToBean(result, QiNiuTokenResponse.class);
        }
        return q;
    }


    /**
     * 当前用户加入某群组
     *
     * @param groupId 群组Id
     * @throws HttpException
     */
    public JoinGroupResponse JoinGroup(String groupId) throws HttpException {
        String url = getURL("group/join");
        String json = JsonMananger.beanToJson(new JoinGroupRequest(groupId));
        StringEntity entity = null;
        try {
            entity = new StringEntity(json, ENCODING);
            entity.setContentType(CONTENT_TYPE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = httpManager.post(mContext, url, entity, CONTENT_TYPE);
        JoinGroupResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, JoinGroupResponse.class);
        }
        return response;
    }


    /**
     * 获取默认群组 和 聊天室
     *
     * @throws HttpException
     */
    public DefaultConversationResponse getDefaultConversation() throws HttpException {
        String url = getURL("misc/demo_square");
        String result = httpManager.get(mContext, url);
        DefaultConversationResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, DefaultConversationResponse.class);
        }
        return response;
    }

    /**
     * 根据一组ids 获取 一组用户信息
     *
     * @param ids 用户 id 集合
     * @throws HttpException
     */
    public GetUserInfosResponse getUserInfos(List<String> ids) throws HttpException {
        String url = getURL("user/batch?");
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        String newUrl = url + stringRequest;
        String result = httpManager.get(mContext, newUrl);
        GetUserInfosResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, GetUserInfosResponse.class);
        }
        return response;
    }

    /**
     * 获取版本信息
     *
     * @throws HttpException
     */
    public VersionResponse getSealTalkVersion() throws HttpException {
        String url = getURL("misc/client_version");
        String result = httpManager.get(mContext, url.trim());
        VersionResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, VersionResponse.class);
        }
        return response;
    }

    public SyncTotalDataResponse syncTotalData(String version) throws HttpException {
        String url = getURL("user/sync/" + version);
        String result = httpManager.get(mContext, url);
        SyncTotalDataResponse response = null;
        if (!TextUtils.isEmpty(result)) {
            response = jsonToBean(result, SyncTotalDataResponse.class);
        }
        return response;
    }

//    /**
//     * 根据userId去服务器查询好友信息
//     *
//     * @throws HttpException
//     */
//    public GetFriendInfoByIDResponse getFriendInfoByID(String userid) throws HttpException {
//        String url = getURL("friendship/" + userid + "/profile");
//        String result = httpManager.get(url);
//        GetFriendInfoByIDResponse response = null;
//        if (!TextUtils.isEmpty(result)) {
//            response = jsonToBean(result, GetFriendInfoByIDResponse.class);
//        }
//        return response;
//    }

    /**
     * //     * 根据userId去服务器查询好友信息
     * //     *
     * //     * @throws HttpException
     * //
     */

     /*
      * Function  :   封装请求体信息
      * Param     :   params请求体内容，encode编码格式
      * Author    :   博客园-依旧淡然
      */
    public static String getRequestData(Map<String, String> params, String encode) {
        params.put("key", KEY);
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        NLog.e("SealAction", "提交参数 : " + stringBuffer.toString());
        return stringBuffer.toString();
    }

}
