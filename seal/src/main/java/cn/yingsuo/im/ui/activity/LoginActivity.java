package cn.yingsuo.im.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mob.tools.utils.UIHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.GetTokenResponse;
import cn.yingsuo.im.server.response.GetUserInfoByIdResponse;
import cn.yingsuo.im.server.response.LoginResponse;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.utils.AMUtils;
import cn.yingsuo.im.server.utils.CommonUtils;
import cn.yingsuo.im.server.utils.NLog;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.RongGenerate;
import cn.yingsuo.im.server.widget.ClearWriteEditText;
import cn.yingsuo.im.server.widget.LoadDialog;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/15.
 * Company RongCloud
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "LoginActivity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;
    private static final int LOGIN_SHARESDK = 8;
    private static final int LOGIN_SHARESDK_SHOUQUAN = 10;

    private ImageView mImg_Background;
    private View weiboLoginView, weixinLoginView, qqLoginView;
    private ClearWriteEditText mPhoneEdit, mPasswordEdit;
    private String phoneString;
    private String passwordString;
    private String connectResultId;
    private String openId;
    private String loginType;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String loginToken;
    private String yingsuoNum;
    private String jing, wei, last_addr;
    private AMapLocationClient locationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setHeadVisibility(View.GONE);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        AMapLocation location = locationClient.getLastKnownLocation();
        if (location.getErrorCode() == 0) {
            jing = location.getLongitude() + "";
            wei = location.getLatitude() + "";
            last_addr = location.getAddress();
        } else {
            jing = "";
            wei = "";
            last_addr = "";
        }
        initView();
    }

    private void initView() {
        weiboLoginView = findViewById(R.id.weibo_login_view);
        weixinLoginView = findViewById(R.id.weixin_login_view);
        qqLoginView = findViewById(R.id.qq_login_view);
        mPhoneEdit = (ClearWriteEditText) findViewById(R.id.de_login_phone);
        mPasswordEdit = (ClearWriteEditText) findViewById(R.id.de_login_password);
        Button mConfirm = (Button) findViewById(R.id.de_login_sign);
        TextView mRegister = (TextView) findViewById(R.id.de_login_register);
        TextView forgetPassword = (TextView) findViewById(R.id.de_login_forgot);
        forgetPassword.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        weiboLoginView.setOnClickListener(this);
        weixinLoginView.setOnClickListener(this);
        qqLoginView.setOnClickListener(this);
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);*/
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    AMUtils.onInactive(mContext, mPhoneEdit);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
        String oldPassword = sp.getString(SealConst.SEALTALK_LOGING_PASSWORD, "");

        if (!TextUtils.isEmpty(oldPhone) && !TextUtils.isEmpty(oldPassword)) {
            mPhoneEdit.setText(oldPhone);
            mPasswordEdit.setText(oldPassword);
        }

        if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
            final AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();
            dlg.show();
            Window window = dlg.getWindow();
            window.setContentView(R.layout.other_devices);
            TextView text = (TextView) window.findViewById(R.id.ok);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.cancel();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weibo_login_view:
                loginType = "1";
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                loginShareSdk(weibo);
                break;
            case R.id.weixin_login_view:
                loginType = "2";
                Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
                loginShareSdk(weixin);
                break;
            case R.id.qq_login_view:
                loginType = "3";
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                loginShareSdk(qq);
                break;
            case R.id.de_login_sign:
                phoneString = mPhoneEdit.getText().toString().trim();
                passwordString = mPasswordEdit.getText().toString().trim();
                yingsuoNum = sp.getString(SealConst.SEALTALK_LOGING_YINGSUONUM, "");

                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    mPhoneEdit.setShakeAnimation();
                    return;
                }

//                if (!AMUtils.isMobile(phoneString)) {
//                    NToast.shortToast(mContext, R.string.Illegal_phone_number);
//                    mPhoneEdit.setShakeAnimation();
//                    return;
//                }

                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(mContext, R.string.password_is_null);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                AMapLocation location = locationClient.getLastKnownLocation();
                if (location.getErrorCode() == 0) {
                    jing = location.getLongitude() + "";
                    wei = location.getLatitude() + "";
                    last_addr = location.getAddress();
                } else {
                    jing = "";
                    wei = "";
                    last_addr = "";
                }
                LoadDialog.show(mContext);
                editor.putBoolean("exit", false);
                editor.apply();
                String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
                request(LOGIN, true);
                break;
            case R.id.de_login_register:
                startActivityForResult(new Intent(this, RegisterActivity.class), 1);
                break;
            case R.id.de_login_forgot:
                startActivityForResult(new Intent(this, ForgetPasswordActivity.class), 2);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            mPhoneEdit.setText(phone);
            mPasswordEdit.setText(password);
        } else if (data != null && requestCode == 1) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            String id = data.getStringExtra("id");
            String nickname = data.getStringExtra("nickname");
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(nickname)) {
                mPhoneEdit.setText(phone);
                mPasswordEdit.setText(password);
                editor.putString(SealConst.SEALTALK_LOGING_PHONE, phone);
                editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, id);
                editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickname);
                editor.apply();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case LOGIN:
                return action.login(phoneString, passwordString, yingsuoNum, last_addr, jing, wei);
            case GET_TOKEN:
                return action.getToken();
            case SYNC_USER_INFO:
                return action.getUserInfoById(connectResultId);
            case LOGIN_SHARESDK:
                return action.loginShareSdk(openId, loginType, last_addr, jing, wei);
            case LOGIN_SHARESDK_SHOUQUAN:
                //return action.loginShareSdkShouQuan();
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    LoginResponse loginResponse = (LoginResponse) result;
                    final UserInfoResponse userInfoResponse = loginResponse.getRes();
                    //goToMain();
                    //用token登录融云
                    if (loginResponse.getCode() == 1) {
                        loginToken = loginResponse.getRes().getToken();
                        if (!TextUtils.isEmpty(loginToken)) {
                            RongIM.connect(loginToken, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    LoadDialog.dismiss(mContext);
                                    NLog.e("connect", "onTokenIncorrect");
                                    //reGetToken();
                                    NToast.shortToast(mContext, "登录融云失败");
                                }

                                @Override
                                public void onSuccess(String s) {
                                    LoadDialog.dismiss(mContext);
                                    connectResultId = s;
                                    NLog.e("connect", "onSuccess userid:" + s);
                                    App.setUserInfoResponse(userInfoResponse);
                                    if (TextUtils.isEmpty(userInfoResponse.getImg())) {
                                        userInfoResponse.setImg(RongGenerate.generateDefaultAvatar(userInfoResponse.getNi_name(), userInfoResponse.getId()));
                                    }
                                    String userId = userInfoResponse.getId();
                                    String nickName = userInfoResponse.getNi_name();
                                    String portraitUri = userInfoResponse.getImg();

                                    String yingsuoNum = userInfoResponse.getYs_id();
                                    String sex = userInfoResponse.getSex();
                                    String adress = userInfoResponse.getAddr();
                                    String slogon = userInfoResponse.getSlogon();

                                    editor.putString(SealConst.SEALTALK_LOGIN_ID, userId);
                                    editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickName);
                                    editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, SealAction.BASE_URL + portraitUri);

                                    editor.putString(SealConst.SEALTALK_LOGING_YINGSUONUM, yingsuoNum);
                                    editor.putString(SealConst.SEALTALK_LOGING_ADRESS, adress);
                                    editor.putString(SealConst.SEALTALK_LOGING_SEX, sex);
                                    editor.putString(SealConst.SEALTALK_LOGING_SLOGON, slogon);
                                    editor.apply();
                                    SealUserInfoManager.getInstance().openDB();
                                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(userId, nickName, Uri.parse(portraitUri)));
                                    //不继续在login界面同步好友,群组,群组成员信息
                                    SealUserInfoManager.getInstance().getAllUserInfo();
                                    //request(SYNC_USER_INFO, true);
                                    goToMain();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    NLog.e("connect", "onError errorcode:" + errorCode.getValue());
                                    LoadDialog.dismiss(mContext);
                                    NToast.shortToast(mContext, "登录融云失败");
                                }
                            });
                        }
                    } else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, loginResponse.getMsg());
                    }
                    break;
                case SYNC_USER_INFO:
                    /*GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (userInfoByIdResponse.getCode() == 200) {
                        if (TextUtils.isEmpty(userInfoByIdResponse.getResult().getPortraitUri())) {
                            userInfoByIdResponse.getResult().setPortraitUri(RongGenerate.generateDefaultAvatar(userInfoByIdResponse.getResult().getNickname(), userInfoByIdResponse.getResult().getId()));
                        }
                        String nickName = userInfoByIdResponse.getResult().getNickname();
                        String portraitUri = userInfoByIdResponse.getResult().getPortraitUri();
                        editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickName);
                        editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, portraitUri);
                        editor.apply();
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(connectResultId, nickName, Uri.parse(portraitUri)));
                    }
                    //不继续在login界面同步好友,群组,群组成员信息
                    SealUserInfoManager.getInstance().getAllUserInfo();
                    goToMain();*/
                    break;
                case GET_TOKEN:
                    GetTokenResponse tokenResponse = (GetTokenResponse) result;
                    if (tokenResponse.getCode() == 200) {
                        String token = tokenResponse.getResult().getToken();
                        if (!TextUtils.isEmpty(token)) {
                            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                                @Override
                                public void onTokenIncorrect() {
                                    Log.e(TAG, "reToken Incorrect");
                                }

                                @Override
                                public void onSuccess(String s) {
                                    connectResultId = s;
                                    NLog.e("connect", "onSuccess userid:" + s);
                                    editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                                    editor.apply();
                                    SealUserInfoManager.getInstance().openDB();
                                    request(SYNC_USER_INFO, true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode e) {

                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    private void reGetToken() {
        request(GET_TOKEN);
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (!CommonUtils.isNetworkConnected(mContext)) {
            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, getString(R.string.network_not_available));
            return;
        }
        switch (requestCode) {
            case LOGIN:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.login_api_fail);
                break;
            case SYNC_USER_INFO:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.sync_userinfo_api_fail);
                break;
            case GET_TOKEN:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.get_token_api_fail);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToMain() {
        editor.putString("loginToken", loginToken);
        editor.putString(SealConst.SEALTALK_LOGING_PHONE, phoneString);
        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, passwordString);
        editor.apply();
        LoadDialog.dismiss(mContext);
        // NToast.shortToast(mContext, R.string.login_success);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void loginShareSdk(Platform plat) {
        if (plat.isAuthValid()) {
            plat.removeAccount(true); //移除授权
        }
        plat.SSOSetting(false);  //设置false表示使用SSO授权方式
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        plat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                arg2.printStackTrace();
            }

            @Override
            public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
                // TODO Auto-generated method stub
                //输出所有授权信息
                arg0.getDb().exportData();
                openId = arg0.getDb().getUserId();
                request(LOGIN_SHARESDK);
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });
        //authorize与showUser单独调用一个即可
        //weibo.authorize();//单独授权,OnComplete返回的hashmap是空的
        plat.showUser(null);//授权并获取用户信息
        //移除授权
        //plat.removeAccount(true);
    }

}
