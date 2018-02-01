package cn.yingsuo.im.ui.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

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
public class UpdateSlogonActivity extends BaseActivity implements View.OnClickListener {

    private static final int UPDATE_SLOGON = 7;
    private ClearWriteEditText mNameEditText;
    private String newSlogon;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);
        setTitle(getString(R.string.update_slogon));
        Button rightButton = getHeadRightButton();
        rightButton.setVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(getString(R.string.confirm));
        mHeadRightText.setOnClickListener(this);
        mNameEditText = (ClearWriteEditText) findViewById(R.id.update_name);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        mNameEditText.setText(sp.getString(SealConst.SEALTALK_LOGING_SLOGON, ""));
        mNameEditText.setSelection(sp.getString(SealConst.SEALTALK_LOGING_SLOGON, "").length());
        editor = sp.edit();

    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return action.setSlogon(newSlogon);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        SetNameResponse sRes = (SetNameResponse) result;
        if (sRes.getCode() == 1) {
            editor.putString(SealConst.SEALTALK_LOGING_SLOGON, newSlogon);
            editor.commit();

            BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.CHANGEINFO);

            //RongIM.getInstance().refreshUserInfoCache(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));
            //RongIM.getInstance().setCurrentUserInfo(new UserInfo(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""), newName, Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, ""))));

            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, "个性签名更改成功");
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        LoadDialog.show(mContext);
        newSlogon = mNameEditText.getText().toString().trim();
        request(UPDATE_SLOGON, true);
    }
}
