package cn.yingsuo.im.ui.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.yingsuo.im.R;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.server.broadcast.BroadcastManager;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.SetNameResponse;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.widget.ClearWriteEditText;
import cn.yingsuo.im.server.widget.LoadDialog;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/23.
 * Company RongCloud
 */
public class UpdateSexActivity extends BaseActivity implements View.OnClickListener {

    private static final int UPDATE_SEX = 7;
    private String newName;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private RadioGroup radioGroup;
    private String mSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sex);
        setTitle(getString(R.string.update_sex));
        Button rightButton = getHeadRightButton();
        rightButton.setVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(getString(R.string.confirm));
        mHeadRightText.setOnClickListener(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        String sex = sp.getString(SealConst.SEALTALK_LOGING_SEX, "");
        if ("1".equals(sex)) {
            radioGroup.check(R.id.nan);
        } else {
            radioGroup.check(R.id.nv);
        }
        editor = sp.edit();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.nan:
                        mSex = "1";
                        break;
                    case R.id.nv:
                        mSex = "2";
                        break;
                }
            }
        });
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return action.setSex(mSex);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        SetNameResponse sRes = (SetNameResponse) result;
        if (sRes.getCode() == 1) {
            editor.putString(SealConst.SEALTALK_LOGING_SEX, mSex);
            editor.commit();

            BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);

            //RongIM.getInstance().refreshUserInfoCache(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));
            //RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));

            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, "性别更改成功");
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        LoadDialog.show(mContext);
        request(UPDATE_SEX);
    }
}
