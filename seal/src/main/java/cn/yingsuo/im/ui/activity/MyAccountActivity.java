package cn.yingsuo.im.ui.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.server.broadcast.BroadcastManager;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.network.net.ApiHelp;
import cn.yingsuo.im.server.response.QiNiuTokenResponse;
import cn.yingsuo.im.server.response.SetPortraitResponse;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.photo.PhotoUtils;
import cn.yingsuo.im.server.widget.BottomMenuDialog;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.server.widget.SelectableRoundedImageView;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class MyAccountActivity extends BaseActivity implements View.OnClickListener {

    private static final int UP_LOAD_PORTRAIT = 8;
    private static final int GET_QI_NIU_TOKEN = 128;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SelectableRoundedImageView mImageView;
    private TextView mName;
    private TextView mYingSuoNum;
    private TextView mSex;
    private TextView mAdress;
    private TextView mSlogon;
    private PhotoUtils photoUtils;
    private BottomMenuDialog dialog;
    private UploadManager uploadManager;
    private String imageUrl;
    private File imageFile;
    private Uri selectUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        setTitle(R.string.de_actionbar_myacc);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        initView();
    }

    private void initView() {
        TextView mPhone = (TextView) findViewById(R.id.tv_my_phone);
        RelativeLayout portraitItem = (RelativeLayout) findViewById(R.id.rl_my_portrait);
        RelativeLayout nameItem = (RelativeLayout) findViewById(R.id.rl_my_username);
        RelativeLayout nameSex = (RelativeLayout) findViewById(R.id.rl_my_sex);
        RelativeLayout nameAdress = (RelativeLayout) findViewById(R.id.rl_my_adress);
        RelativeLayout nameSlogon = (RelativeLayout) findViewById(R.id.rl_my_slogon);
        mImageView = (SelectableRoundedImageView) findViewById(R.id.img_my_portrait);
        mName = (TextView) findViewById(R.id.tv_my_username);
        mYingSuoNum = (TextView) findViewById(R.id.tv_my_yingsuonum);
        mAdress = (TextView) findViewById(R.id.tv_my_adress);
        mSex = (TextView) findViewById(R.id.tv_my_sex);
        mSlogon = (TextView) findViewById(R.id.tv_my_slogon);
        portraitItem.setOnClickListener(this);
        nameItem.setOnClickListener(this);
        nameSex.setOnClickListener(this);
        nameAdress.setOnClickListener(this);
        nameSlogon.setOnClickListener(this);
        String cacheName = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
        String cachePortrait = sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "");
        String cachePhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");

        String yingsuoNum = sp.getString(SealConst.SEALTALK_LOGING_YINGSUONUM, "");
        String adress = sp.getString(SealConst.SEALTALK_LOGING_ADRESS, "");
        String sex = sp.getString(SealConst.SEALTALK_LOGING_SEX, "");
        String slogon = sp.getString(SealConst.SEALTALK_LOGING_SLOGON, "");
        if (!TextUtils.isEmpty(cachePhone)) {
            mPhone.setText("+86 " + cachePhone);
        }
        if (!TextUtils.isEmpty(yingsuoNum)) {
            mYingSuoNum.setText(yingsuoNum);
        }
        if (!TextUtils.isEmpty(adress)) {
            mAdress.setText(adress);
        }
        if (!TextUtils.isEmpty(sex)) {
            if ("1".equals(sex)) {
                mSex.setText("男");
            } else {
                mSex.setText("女");
            }
        }
        if (!TextUtils.isEmpty(slogon)) {
            mSlogon.setText(slogon);
        }
        if (!TextUtils.isEmpty(cacheName)) {
            mName.setText(cacheName);
            String cacheId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "a");
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(new UserInfo(
                    cacheId, cacheName, Uri.parse(cachePortrait)));
            ImageLoader.getInstance().displayImage(portraitUri, mImageView, App.getOptions());
        }
        setPortraitChangeListener();
        BroadcastManager.getInstance(mContext).addAction(SealConst.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mName.setText(sp.getString(SealConst.SEALTALK_LOGIN_NAME, ""));
                mAdress.setText(sp.getString(SealConst.SEALTALK_LOGING_ADRESS, ""));
                String sex = sp.getString(SealConst.SEALTALK_LOGING_SEX, "");
                if (!TextUtils.isEmpty(sex)) {
                    if ("1".equals(sex)) {
                        mSex.setText("男");
                    } else {
                        mSex.setText("女");
                    }
                }
                mSlogon.setText(sp.getString(SealConst.SEALTALK_LOGING_SLOGON, ""));
            }
        });
    }

    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    imageFile = new File(selectUri.getPath());
                    LoadDialog.show(mContext);
                    //request(GET_QI_NIU_TOKEN);
                    try {
                        action.setPortrait(MyAccountActivity.this, new ApiHelp.IApiCallBack() {
                            @Override
                            public void onApiCallBack(Object object, int index) {
                                imageUrl = (String) object;
                                editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, imageUrl);
                                editor.commit();
                                ImageLoader.getInstance().displayImage(imageUrl, mImageView, App.getOptions());
                                if (RongIM.getInstance() != null) {
                                    String userId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "");
                                    String userName = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
                                    Uri headUri = Uri.parse(imageUrl);
                                    UserInfo userInfo = new UserInfo(userId, userName, headUri);
                                    RongIM.getInstance().setCurrentUserInfo(userInfo);
                                }
                                BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);
                                NToast.shortToast(mContext, getString(R.string.portrait_update_success));
                                LoadDialog.dismiss(mContext);
                            }
                        }, imageFile);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_my_portrait:
                showPhotoDialog();
                break;
            case R.id.rl_my_username:
                startActivity(new Intent(this, UpdateNameActivity.class));
                break;
            case R.id.rl_my_sex:
                startActivity(new Intent(this, UpdateSexActivity.class));
                break;
            case R.id.rl_my_adress:
                startActivity(new Intent(this, UpdateAdressActivity.class));
                break;
            case R.id.rl_my_slogon:
                startActivity(new Intent(this, UpdateSlogonActivity.class));
                break;
        }
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case UP_LOAD_PORTRAIT:
                //return action.setPortrait(imageUrl);
                return null;
            case GET_QI_NIU_TOKEN:
                return action.getQiNiuToken();
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case UP_LOAD_PORTRAIT:
                    SetPortraitResponse spRes = (SetPortraitResponse) result;
                    if (spRes.getCode() == 200) {
                        editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, imageUrl);
                        editor.commit();
                        ImageLoader.getInstance().displayImage(imageUrl, mImageView, App.getOptions());
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), sp.getString(SealConst.SEALTALK_LOGIN_NAME, ""), Uri.parse(imageUrl)));
                        }
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);
                        NToast.shortToast(mContext, getString(R.string.portrait_update_success));
                    }
                    LoadDialog.dismiss(mContext);
                    break;
                case GET_QI_NIU_TOKEN:
                    QiNiuTokenResponse response = (QiNiuTokenResponse) result;
                    if (response.getCode() == 200) {
                        uploadImage(response.getResult().getDomain(), response.getResult().getToken(), selectUri);
                    }
                    break;
            }
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_QI_NIU_TOKEN:
            case UP_LOAD_PORTRAIT:
                NToast.shortToast(mContext, "设置头像请求失败");
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;

    /**
     * 弹出底部框
     */
    @TargetApi(23)
    private void showPhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new BottomMenuDialog(mContext);
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("您需要在设置里打开相机权限。")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create().show();
                        }
                        return;
                    }
                }
                photoUtils.takePicture(MyAccountActivity.this);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectPicture(MyAccountActivity.this);
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(MyAccountActivity.this, requestCode, resultCode, data);
                break;
        }
    }


    public void uploadImage(final String domain, String imageToken, Uri imagePath) {
        if (TextUtils.isEmpty(domain) && TextUtils.isEmpty(imageToken) && TextUtils.isEmpty(imagePath.toString())) {
            throw new RuntimeException("upload parameter is null!");
        }
        File imageFile = new File(imagePath.getPath());

        if (this.uploadManager == null) {
            this.uploadManager = new UploadManager();
        }
        this.uploadManager.put(imageFile, null, imageToken, new UpCompletionHandler() {

            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                if (responseInfo.isOK()) {
                    try {
                        String key = (String) jsonObject.get("key");
                        imageUrl = "http://" + domain + "/" + key;
                        Log.e("uploadImage", imageUrl);
                        if (!TextUtils.isEmpty(imageUrl)) {
                            request(UP_LOAD_PORTRAIT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    NToast.shortToast(mContext, getString(R.string.upload_portrait_failed));
                    LoadDialog.dismiss(mContext);
                }
            }
        }, null);
    }
}
