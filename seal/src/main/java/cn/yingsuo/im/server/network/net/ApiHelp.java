package cn.yingsuo.im.server.network.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/17.
 */

public class ApiHelp {
    public static final String MSG_SIGN_KEY = "29067275e60e29544639d4551d953666";
    public static final String BASE_URL = "http://114.115.207.155/api.php/";
    //public static final String BASE_URL = "https://api.qjylw.com/Index.php?p=tv&c=";
    //版本
    public final static String GetQJAppVersion = "https://api.qjylw.com/Index.php?p=app&c=clt&a=GetQJAppVersion";//根据客户端类型获取服务器版本信息
    //登录注册
    public final static String Register = "login/regist";//注册
    public final static String SendMsg = "login/regsend";//根据手机号码发送验证短信
    public final static String CheckSmsAndSignUp = "index&a=CheckSmsAndSignUp";//根据验证的短信和密码注册用户
    public final static String Login = "user&a=Login";//根据用户名和密码进行登录
    public final static String Reset = "user&a=Reset";//重置密码
    public final static String CheckSmsAndSetPassword = "index&a=CheckSmsAndSetPassword";//根据验证的短信和密码重新设定用户登录密码
    //我的资料
    public final static String GetUserInfo = "user&a=GetUserInfo";//根据用户id获取用户资料
    public final static String ModifyUser = "user&a=ModifyUser";//更新资料
    public final static String GetAccount = "user&a=GetAccount";//根据用户id获取用户账户信息
    public final static String GetUserLevelById = "doctor&a=GetUserLevelById";//获取用户等级
    private static ApiHelp apiHelp;
    //首页
    public final static String Index = "video&a=Index";//获取首页轮播图和模块集合

    public final static String GetHotVideo = "video&a=GetHotVideo";//热门视频搜索.
    public final static String FindVideoBy = "video&a=FindVideoBy";//根据关键字搜索视频
    public final static String GetProgramList = "video&a=GetProgramList";//获取节目列表
    public final static String GetLiveReserve = "video&a=GetLiveReserve";//获取预约列表
    public final static String Reserve = "video&a=Reserve";//预约节目

    public final static String UpLoadVideo = "video&a=UpLoadVideo";//更新视频下载量
    public final static String VideoPlay = "video&a=VideoPlay";//同步视频观看时间
    public final static String GetVideoPlayList = "video&a=GetVideoPlayList";// 获取视频观看列表
    public final static String DeletePlay = "video&a=DeletePlay";// 根据主键删除播放记录

    //public final static String VideoListWebView = "http://192.168.55.196/tvplayer/site/tv-list";//视频列表
    //public final static String VideoColectListWebView = "http://192.168.55.196/tvplayer/site/tv-collect";//收藏列表
   /* public final static String VideoListWebView = "https://demo.qjylw.com/tvplayer/site/tv-list";//视频列表
    public final static String VideoColectListWebView = "https://demo.qjylw.com/tvplayer/site/tv-collect";//收藏列表
    public final static String BaoxianWebView = "https://demo.qjylw.com/mobile";//保险*/
    public final static String BaoxianWebView = "https://www.qjylw.com/mobile";//保险
    public final static String VideoListWebView = "https://www.qjylw.com/tvplayer/site/tv-list";//视频列表
    public final static String VideoColectListWebView = "https://www.qjylw.com/tvplayer/site/tv-collect";//收藏列表

    public final static int RESULT_OK = 1;
    public final static int RESULT_FAULT = 2;

    private ApiHelp() {
    }

    public static ApiHelp getInstance() {
        if (null == apiHelp) {
            apiHelp = new ApiHelp();
        }
        return apiHelp;
    }

    public INetWorkCallBack netWorkCallBack = new INetWorkCallBack() {
        @Override
        public void onNetWorkCallBack(String o) {

        }
    };


    public void getQJAppVersion(final Context context, final IApiCallBack callBack, String version, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("app_version", version);
        json.put("phone_type", "android");
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(GetQJAppVersion, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        callBack.onApiCallBack(o, index);
                    }
                });
            }
        });
    }

    public void register(final Context context, final IApiCallBack callBack, String ni_name, String tel, String password, String tui_tel, String code) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        Map<String, String> map = new HashMap<>();
        map.put("ni_name", ni_name);
        map.put("tel", tel);
        map.put("password", password);
        map.put("tui_tel", tui_tel);
        map.put("code", code);
        map.put("key", MSG_SIGN_KEY);
        String parmStr = getRequestData(map, "utf-8");
        http.okHttp_postFromParameters(BASE_URL + Register, parmStr, new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
                                JSONObject data = jo.getJSONObject("data");
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //"has_check":"是否需要验证用户存在(0:不需要,1需要)",
    public void sendMsg(final Context context, final IApiCallBack callBack, String tel) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        Map<String, String> map = new HashMap<>();
        map.put("key", MSG_SIGN_KEY);
        map.put("tel", tel);
        String parmStr = getRequestData(map, "utf-8");
        http.okHttp_postFromParameters(BASE_URL + SendMsg, parmStr, new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            Log.e("---------message", msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /*
      * Function  :   封装请求体信息
      * Param     :   params请求体内容，encode编码格式
      * Author    :   博客园-依旧淡然
      */
    public static String getRequestData(Map<String, String> params, String encode) {
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
        return stringBuffer.toString();
    }

    //拼接+MD5加密+转成大写获取到短信签名
    private String getSignStr(String paramstr) {
        String stringA = paramstr;
        String stringSignTemp = stringA + "&key=" + MSG_SIGN_KEY;
        String sign = getMd5Str(stringSignTemp).toUpperCase();
        Log.e("sign", sign);
        return sign;
    }

    //拼接+MD5加密+转成大写获取到短信签名
    public String getSignStr(String paramstr, String key) {
        String stringA = paramstr;
        String stringSignTemp = stringA + "&key=" + key;
        String sign = getMd5Str(stringSignTemp).toUpperCase();
        Log.e("sign", sign);
        return sign;
    }

    //json转成map
    public Map<String, String> getMapForJson(String jsonStr) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIter = jsonObject.keys();
            String key;
            String value;
            Map<String, String> valueMap = new HashMap<String, String>();
            while (keyIter.hasNext()) {
                key = keyIter.next();
                value = jsonObject.get(key) + "";
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }


    //map按照参数名ASCII字典序排序转换成字符串
    public String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
//                if (!StringUtils.isBlank(item.getKey())) {
//                    String key = item.getKey();
//                    String val = item.getValue();
//                    if (urlEncode) {
//                        val = URLEncoder.encode(val, "utf-8");
//                    }
//                    if (keyToLower) {
//                        buf.append(key.toLowerCase() + "=" + val);
//                    } else {
//                        buf.append(key + "=" + val);
//                    }
//                    buf.append("&");
//                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


    //获取指定位数的随机字符串(包含小写字母、大写字母、数字,0<length)
    public String getRandomString(int length) {
        //随机字符串的随机字符库
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    //md5加密
    public String getMd5Str(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void checkSmsAndSignUp(final Context context, final IApiCallBack callBack, String mobile, String smsCode, String password, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        Log.e("sms_code", smsCode);
        json.put("mobile", mobile);
        json.put("sms_code", smsCode);
        json.put("password", getMd5Str(password));
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + CheckSmsAndSignUp, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            Log.e("---------注册", o);
                            Log.e("转码", URLDecoder.decode(s, "utf8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("0".equals(returnCode)) {
                            callBack.onApiCallBack(1, index);
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }

    public void loginIn(final Context context, final IApiCallBack callBack, String mobile, String password, String deviceToken) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("login_name", mobile);
        json.put("password", getMd5Str(password));
        json.put("device_token", deviceToken);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + Login, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
                                JSONObject data = jo.getJSONObject("data");
//                                QJUserEntity user = new QJUserEntity(data);
//                                callBack.onApiCallBack(user, 0);
//                                PreferenceManager.getInstance().saveUser(user);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void getHotVideo(final Context context, final IApiCallBack callBack) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        final JSONObject json = new JSONObject();
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + GetHotVideo, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
                                JSONArray jsonArray = jo.getJSONArray("data");
//                                List<VideoBean> list = new ArrayList<VideoBean>();
//                                int size = jsonArray.length();
//                                VideoBean videoBean = null;
//                                for (int i = 0; i < size; i++) {
//                                    videoBean = new VideoBean(jsonArray.getJSONObject(i));
//                                    list.add(videoBean);
//                                }
//                                callBack.onApiCallBack(list, 0);
                            } else {
                                //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void getProgramList(final Context context, boolean isShowDialog, final IApiCallBack callBack, String dateTime, String userId) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        http.setShowDialog(isShowDialog);
        final JSONObject json = new JSONObject();
        json.put("data_time", dateTime);
        json.put("user_id", userId);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + GetProgramList, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
//                            List<VideoBean> list = new ArrayList<VideoBean>();
//                            if ("0".equals(returnCode)) {
//                                JSONArray jsonArray = jo.getJSONArray("data");
//                                int size = jsonArray.length();
//                                VideoBean videoBean = null;
//                                for (int i = 0; i < size; i++) {
//                                    videoBean = new VideoBean(jsonArray.getJSONObject(i));
//                                    list.add(videoBean);
//                                }
//                            } else {
//                                //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//                            }
                            // callBack.onApiCallBack(list, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    Handler handler = new Handler();

    public void getLiveReserve(final Context context, boolean isShowDialog, final IApiCallBack callBack, String userId) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        http.setShowDialog(isShowDialog);
        final JSONObject json = new JSONObject();
        json.put("user_id", userId);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + GetLiveReserve, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
//                            List<VideoBean> list = new ArrayList<VideoBean>();
//                            if ("0".equals(returnCode)) {
//                                JSONArray jsonArray = jo.getJSONArray("data");
//                                int size = jsonArray.length();
//                                VideoBean videoBean = null;
//                                for (int i = 0; i < size; i++) {
//                                    videoBean = new VideoBean(jsonArray.getJSONObject(i));
//                                    list.add(videoBean);
//                                }
//                                //callBack.onApiCallBack(list, 0);
//                            } else {
//                                //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//                            }
//                            QjServices.myYuYueTvList = list;
//                            callBack.onApiCallBack(list, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                /*((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            List<VideoBean> list = new ArrayList<VideoBean>();
                            if ("0".equals(returnCode)) {
                                JSONArray jsonArray = jo.getJSONArray("data");
                                int size = jsonArray.length();
                                VideoBean videoBean = null;
                                for (int i = 0; i < size; i++) {
                                    videoBean = new VideoBean(jsonArray.getJSONObject(i));
                                    list.add(videoBean);
                                }
                                //callBack.onApiCallBack(list, 0);
                            } else {
                                //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                            callBack.onApiCallBack(list, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });*/
            }
        });
    }

    public void reserve(final Context context, final IApiCallBack callBack, String userId, String videoId, String videoName, String startTime) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        final JSONObject json = new JSONObject();
        json.put("user_id", userId);
        json.put("video_id", videoId);
        json.put("video_name", videoName);
        json.put("start_time", startTime);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + Reserve, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
                                JSONArray jsonArray = jo.getJSONArray("data");
//                                List<VideoBean> list = new ArrayList<VideoBean>();
//                                int size = jsonArray.length();
//                                VideoBean videoBean = null;
//                                for (int i = 0; i < size; i++) {
//                                    videoBean = new VideoBean(jsonArray.getJSONObject(i));
//                                    list.add(videoBean);
//                                }
//                                callBack.onApiCallBack(RESULT_OK, 0);
                            } else {
                                callBack.onApiCallBack(RESULT_FAULT, 0);
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void findVideoBy(final Context context, final IApiCallBack callBack, String keyWords) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        final JSONObject json = new JSONObject();
        json.put("key_words", keyWords);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + FindVideoBy, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
//                                JSONObject data = jo.getJSONObject("data");
//                                List<VideoBean> resultList = new ArrayList<VideoBean>();
//                                List<VideoBean> loveList = new ArrayList<VideoBean>();
//                                JSONArray resultArray = data.getJSONArray("video_list");
//                                int advLength = resultArray.length();
//                                JSONObject videoObject = null;
//                                VideoBean videoBean = null;
//                                for (int i = 0; i < advLength; i++) {
//                                    videoObject = resultArray.getJSONObject(i);
//                                    videoBean = new VideoBean(videoObject);
//                                    resultList.add(videoBean);
//                                }
//
//                                JSONArray loveArray = data.getJSONArray("maybe_love");
//                                int itemLength = loveArray.length();
//                                JSONObject loveObject = null;
//                                VideoBean loveBean = null;
//                                for (int i = 0; i < itemLength; i++) {
//                                    loveObject = loveArray.getJSONObject(i);
//                                    loveBean = new VideoBean(loveObject);
//                                    loveList.add(loveBean);
//                                }
                                //List[] tvSearchList = {resultList, loveList};
                                //callBack.onApiCallBack(tvSearchList, 0);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void reSetPwd(final Context context, final IApiCallBack callBack, String mobile, String password, String smsCode) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("mobile", mobile);
        json.put("sms_code", smsCode);
        json.put("password", getMd5Str(password));
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + Reset, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            if ("0".equals(returnCode)) {
                               /* JSONObject data = jo.getJSONObject("data");
                                QJUserEntity user = new QJUserEntity(data);*/
                                callBack.onApiCallBack(1, 0);
                                //PreferenceManager.getInstance().saveUser(user);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void upLoadVideo(final Context context, final IApiCallBack callBack, String userId, String videoId) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        http.setShowDialog(false);
        final JSONObject json = new JSONObject();
        json.put("video_id", videoId);
        json.put("user_id", userId);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + UpLoadVideo, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            // List<VideoBean> list = new ArrayList<VideoBean>();
                            if ("0".equals(returnCode)) {
                                callBack.onApiCallBack(0, 0);
                            } else {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void getUserInfo(final Context context, final IApiCallBack callBack, String userId, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + GetUserInfo, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                    }
                });
            }
        });
    }

    public void modifyUser(final Context context, final IApiCallBack callBack, String userId, final String realName,
                           final String identity, final String sex, final String mobile, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("id", userId);
        json.put("real_name", realName);
        json.put("identity_num", identity);
        json.put("sex", sex);
        json.put("mobile", mobile);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + ModifyUser, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);

                    }
                });
            }
        });
    }


    public void getAccount(final Context context, final IApiCallBack callBack, String userId, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + GetAccount, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);

                    }
                });
            }
        });
    }


    public void checkSmsAndSetPassword(final Context context, final IApiCallBack callBack, String mobile, String sms_code, String password, final int index) throws JSONException {
        OkHttpHelp http = OkHttpHelp.getInstance(context);
        JSONObject json = new JSONObject();
        json.put("mobile", mobile);
        json.put("sms_code", sms_code);
        json.put("password", getMd5Str(password));
        addDeviceMsg(json, context);
        Map<String, String> paramMap = getMapForJson(json.toString());
        String paramStr = formatUrlMap(paramMap, false, false);
        json.put("sign", getSignStr(paramStr));
        http.okHttp_postFromParameters(BASE_URL + CheckSmsAndSetPassword, json.toString(), new INetWorkCallBack() {
            @Override
            public void onNetWorkCallBack(final String o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("---------message", o);
                        JSONObject jo = null;
                        String returnCode = "";
                        String msg = "";
                        try {
                            jo = new JSONObject(o);
                            returnCode = jo.getString("code");
                            String s = jo.getString("msg");
                            msg = URLDecoder.decode(s, "utf8");
                            Log.e("---------注册", o);
                            Log.e("转码", URLDecoder.decode(s, "utf8"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if ("0".equals(returnCode)) {
                            callBack.onApiCallBack(1, index);
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }


    public void addDeviceMsg(JSONObject jo, Context context) {
        String model = Build.MODEL;
        String sdk = "Android";
        PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = "";
        if (null != info) {
            version = info.versionName;
        }
        String netWork = GetNetworkType(context);
        try {
            jo.put("device_xh", model);
            jo.put("device_bb", sdk);
            jo.put("appversion", version);
            jo.put("network", netWork);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String GetNetworkType(Context contenxt) {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager) contenxt.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();


                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }


        return strNetworkType;
    }

    public interface IApiCallBack {
        public void onApiCallBack(Object object, int index);
    }
}
