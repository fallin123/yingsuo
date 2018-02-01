package cn.yingsuo.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;

import org.json.JSONException;

import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.network.net.ApiHelp;
import cn.yingsuo.im.server.response.BindYsIdResponse;
import cn.yingsuo.im.server.response.CheckPhoneResponse;
import cn.yingsuo.im.server.response.RegisterResponse;
import cn.yingsuo.im.server.response.SendCodeResponse;
import cn.yingsuo.im.server.response.VerifyCodeResponse;
import cn.yingsuo.im.server.utils.AMUtils;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.downtime.DownTimer;
import cn.yingsuo.im.server.utils.downtime.DownTimerListener;
import cn.yingsuo.im.server.widget.ClearWriteEditText;
import cn.yingsuo.im.server.widget.LoadDialog;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
@SuppressWarnings("deprecation")
public class RegisterActivity extends BaseActivity implements View.OnClickListener, DownTimerListener {

    private static final int CHECK_PHONE = 1;
    private static final int SEND_CODE = 2;
    private static final int VERIFY_CODE = 3;
    private static final int REGISTER = 4;
    private static final int BIND_YSID = 5;
    private static final int REGISTER_BACK = 1001;
    private ImageView mImgBackground;
    private ClearWriteEditText mPhoneEdit, mCodeEdit, mNickEdit, mPasswordEdit, mPhoneEditTuiJian;
    private Button mGetCode, mConfirm;
    private String mPhone, mCode, mNickName, mPassword, mCodeToken, mUid, mYsId, mPhoneTuiJian;
    private boolean isRequestCode = false;
    private AMapLocationClient locationClient = null;
    private String jing, wei, last_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setHeadVisibility(View.GONE);
        locationClient = new AMapLocationClient(this.getApplicationContext());
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
        mPhoneEdit = (ClearWriteEditText) findViewById(R.id.reg_phone);
        mCodeEdit = (ClearWriteEditText) findViewById(R.id.reg_code);
        mNickEdit = (ClearWriteEditText) findViewById(R.id.reg_username);
        mPasswordEdit = (ClearWriteEditText) findViewById(R.id.reg_password);
        mPhoneEditTuiJian = (ClearWriteEditText) findViewById(R.id.reg_phone_tuijian);
        mGetCode = (Button) findViewById(R.id.reg_getcode);
        mConfirm = (Button) findViewById(R.id.reg_button);

        mGetCode.setOnClickListener(this);
        mGetCode.setClickable(false);
        mConfirm.setOnClickListener(this);
        mImgBackground = (ImageView) findViewById(R.id.rg_img_backgroud);
        addEditTextListener();

    }

    private void addEditTextListener() {
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* if (s.length() == 11 && isBright) {
                    if (AMUtils.isMobile(s.toString().trim())) {
                        mPhone = s.toString().trim();
                        request(CHECK_PHONE, true);
                        AMUtils.onInactive(mContext, mPhoneEdit);
                    } else {
                        Toast.makeText(mContext, R.string.Illegal_phone_number, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mGetCode.setClickable(false);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }*/

                if (s.length() == 11) {
                    mPhone = s.toString().trim();
                    mGetCode.setClickable(true);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    mGetCode.setClickable(false);
                    mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    AMUtils.onInactive(mContext, mCodeEdit);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    mConfirm.setClickable(true);
                    mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                } else {
                    mConfirm.setClickable(false);
                    mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case CHECK_PHONE:
                return action.checkPhoneAvailable("86", mPhone);
            case SEND_CODE:
                return action.regsend(mPhone);
            case VERIFY_CODE:
                return action.verifyCode("86", mPhone, mCode);
            case REGISTER:
                return action.register(mNickName, mPassword, mPhone, mPhoneTuiJian, mCode,last_addr,jing,wei);
            case BIND_YSID:
                return action.bindYsid(mUid, mYsId);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case CHECK_PHONE:
                    CheckPhoneResponse cprres = (CheckPhoneResponse) result;
                    if (cprres.getCode() == 200) {
                        if (cprres.isResult()) {
                            mGetCode.setClickable(true);
                            mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
                            Toast.makeText(mContext, R.string.phone_number_available, Toast.LENGTH_SHORT).show();
                        } else {
                            mGetCode.setClickable(false);
                            mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
                            Toast.makeText(mContext, R.string.phone_number_has_been_registered, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case SEND_CODE:
                    SendCodeResponse scrres = (SendCodeResponse) result;
                    NToast.shortToast(mContext, scrres.getMsg());
                    /*if (scrres.getCode() == 200) {
                        NToast.shortToast(mContext, R.string.messge_send);
                    } else if (scrres.getCode() == 5000) {
                        NToast.shortToast(mContext, R.string.message_frequency);
                    }*/
                    break;

                case VERIFY_CODE:
                    VerifyCodeResponse vcres = (VerifyCodeResponse) result;
                    switch (vcres.getCode()) {
                        case 200:
                            mCodeToken = vcres.getResult().getVerification_token();
                            if (!TextUtils.isEmpty(mCodeToken)) {
                                request(REGISTER);
                            } else {
                                NToast.shortToast(mContext, "code token is null");
                                LoadDialog.dismiss(mContext);
                            }
                            break;
                        case 1000:
                            //验证码错误
                            NToast.shortToast(mContext, R.string.verification_code_error);
                            LoadDialog.dismiss(mContext);
                            break;
                        case 2000:
                            //验证码过期
                            NToast.shortToast(mContext, R.string.captcha_overdue);
                            LoadDialog.dismiss(mContext);
                            break;
                    }
                    break;

                case REGISTER:
                    RegisterResponse rres = (RegisterResponse) result;
                    switch (rres.getCode()) {
                        case 1:
                            //NToast.shortToast(mContext, R.string.register_success);
                            mUid = rres.getRes().getuId();
                            String[] ysIdList = rres.getRes().getYs_id();
                            if (null != ysIdList && ysIdList.length > 0) {
                                mYsId = ysIdList[0];
                            }
                            request(BIND_YSID);
                           /* Intent data = new Intent();
                            data.putExtra("phone", mPhone);
                            data.putExtra("password", mPassword);
                            data.putExtra("nickname", mNickName);
                            //data.putExtra("id", rres.getResult().getId());
                            setResult(REGISTER_BACK, data);
                            this.finish();*/
                            break;
                        default:
                            NToast.shortToast(mContext, rres.getMsg());
                            break;
                    }
                    break;
                case BIND_YSID:
                    BindYsIdResponse byir = (BindYsIdResponse) result;
                    LoadDialog.dismiss(mContext);

                    switch (byir.getCode()) {
                        case 1:
                            NToast.shortToast(mContext, R.string.register_success);
                            Intent data = new Intent();
                            data.putExtra("phone", mPhone);
                            data.putExtra("password", mPassword);
                            data.putExtra("nickname", mNickName);
                            data.putExtra("id", mUid);
                            setResult(REGISTER_BACK, data);
                            this.finish();
                            break;
                        case 109:
                            NToast.shortToast(mContext, byir.getMsg());
                            break;
                        default:
                            NToast.shortToast(mContext, byir.getMsg());
                            break;
                    }
                    break;

            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case CHECK_PHONE:
                Toast.makeText(mContext, "手机号可用请求失败", Toast.LENGTH_SHORT).show();
                break;
            case SEND_CODE:
                NToast.shortToast(mContext, "获取验证码请求失败");
                break;
            case VERIFY_CODE:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, "验证码是否可用请求失败");
                break;
            case REGISTER:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, "注册请求失败");
                break;
        }
    }

    @Override
    public android.support.v4.app.FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg_getcode:
                if (TextUtils.isEmpty(mPhoneEdit.getText().toString().trim())) {
                    NToast.longToast(mContext, R.string.phone_number_is_null);
                } else {
                    isRequestCode = true;
                    DownTimer downTimer = new DownTimer();
                    downTimer.setListener(this);
                    downTimer.startDown(60 * 1000);
                    request(SEND_CODE);
                }
                break;
            case R.id.reg_button:
                mPhone = mPhoneEdit.getText().toString().trim();
                mCode = mCodeEdit.getText().toString().trim();
                mNickName = mNickEdit.getText().toString().trim();
                mPassword = mPasswordEdit.getText().toString().trim();
                mPhoneTuiJian = mPhoneEditTuiJian.getText().toString().trim();


                if (TextUtils.isEmpty(mNickName)) {
                    NToast.shortToast(mContext, getString(R.string.name_is_null));
                    mNickEdit.setShakeAnimation();
                    return;
                }
                if (mNickName.contains(" ")) {
                    NToast.shortToast(mContext, getString(R.string.name_contain_spaces));
                    mNickEdit.setShakeAnimation();
                    return;
                }

                if (TextUtils.isEmpty(mPhone)) {
                    NToast.shortToast(mContext, getString(R.string.phone_number_is_null));
                    mPhoneEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mCode)) {
                    NToast.shortToast(mContext, getString(R.string.code_is_null));
                    mCodeEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    NToast.shortToast(mContext, getString(R.string.password_is_null));
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (mPassword.contains(" ")) {
                    NToast.shortToast(mContext, getString(R.string.password_cannot_contain_spaces));
                    mPasswordEdit.setShakeAnimation();
                    return;
                }

                if (!isRequestCode) {
                    NToast.shortToast(mContext, getString(R.string.not_send_code));
                    return;
                }

                LoadDialog.show(mContext);
                //request(VERIFY_CODE, true);
                request(REGISTER);
                break;
        }
    }

    boolean isBright = true;

    @Override
    public void onTick(long millisUntilFinished) {
        mGetCode.setText(String.valueOf(millisUntilFinished / 1000) + "s");
        mGetCode.setClickable(false);
        mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_gray));
        isBright = false;
    }

    @Override
    public void onFinish() {
        mGetCode.setText(R.string.get_code);
        mGetCode.setClickable(true);
        mGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.rs_select_btn_blue));
        isBright = true;
    }

}
