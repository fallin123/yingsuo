package cn.yingsuo.im.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.SealAppContext;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.db.Friend;
import cn.yingsuo.im.server.network.async.AsyncTaskManager;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.FriendInvitationResponse;
import cn.yingsuo.im.server.response.GetUserInfoByPhoneResponse;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.utils.CommonUtils;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.widget.DialogWithYesOrNoUtils;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.server.widget.SelectableRoundedImageView;
import cn.yingsuo.im.ui.adapter.AddFrendSearchAdapter;

public class SearchFriendActivity extends BaseActivity {

    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    private static final int SEARCH_PHONE = 10;
    private static final int ADD_FRIEND = 11;
    private EditText mEtSearch;
    private LinearLayout searchItem;
    private RecyclerView frendResultRv;
    private Button searchBtn;
    private TextView searchName;
    private SelectableRoundedImageView searchImage;
    private String mPhone;
    private String addFriendMessage;
    private String mFriendId;
    private String mUid;
    private AddFrendSearchAdapter addFrendSearchAdapter;
    private List<UserInfoResponse> searchFrendList;

    private Friend mFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle((R.string.search_friend));

        searchBtn = (Button) findViewById(R.id.add_frend_search_btn);
        mEtSearch = (EditText) findViewById(R.id.search_edit);
        searchItem = (LinearLayout) findViewById(R.id.search_result);
        searchName = (TextView) findViewById(R.id.search_name);
        searchImage = (SelectableRoundedImageView) findViewById(R.id.search_header);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhone = mEtSearch.getText().toString().trim();
                if ("".equals(mPhone)) {
                    NToast.longToast(SearchFriendActivity.this, "搜索内容不能为空");
                    return;
                }
                hintKbTwo();
                LoadDialog.show(mContext);
                request(SEARCH_PHONE, true);
            }
        });
        initResultRecyclerView();
    }

    private void initResultRecyclerView() {
        frendResultRv = (RecyclerView) findViewById(R.id.add_frend_result_rv);
        addFrendSearchAdapter = new AddFrendSearchAdapter(this);
        searchFrendList = new ArrayList<>();
        frendResultRv.setAdapter(addFrendSearchAdapter);
        frendResultRv.setLayoutManager(new LinearLayoutManager(this));
        addFrendSearchAdapter.notifyList(searchFrendList);
        addFrendSearchAdapter.setOnItemClickListener(new AddFrendSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mFriendId = searchFrendList.get(position).getId();
                if (isFriendOrSelf(mFriendId)) {
                    Intent intent = new Intent(SearchFriendActivity.this, UserDetailActivity.class);
                    intent.putExtra("friend", mFriend);
                    intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                    startActivity(intent);
                    SealAppContext.getInstance().pushActivity(SearchFriendActivity.this);
                    return;
                }
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, getString(R.string.add_text), getString(R.string.add_friend), new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void executeEvent() {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }

                    @Override
                    public void executeEditEvent(String editText) {
                        if (!CommonUtils.isNetworkConnected(mContext)) {
                            NToast.shortToast(mContext, R.string.network_not_available);
                            return;
                        }
                        addFriendMessage = editText;
                        if (TextUtils.isEmpty(editText)) {
                            addFriendMessage = "我是" + getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_NAME, "");
                        }
                        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
                        if (null != userInfoResponse) {
                            mUid = userInfoResponse.getId();
                        }
                        if (!TextUtils.isEmpty(mFriendId)) {
                            LoadDialog.show(mContext);
                            request(ADD_FRIEND);
                        } else {
                            NToast.shortToast(mContext, "id is null");
                        }
                    }
                });
            }
        });
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case SEARCH_PHONE:
                return action.getUserInfoFromPhone(mPhone);
            case ADD_FRIEND:
                return action.sendFriendInvitation(mUid, mFriendId);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case SEARCH_PHONE:
                    final GetUserInfoByPhoneResponse userInfoByPhoneResponse = (GetUserInfoByPhoneResponse) result;
                    if (userInfoByPhoneResponse.getCode() == 1) {
                        LoadDialog.dismiss(mContext);
                        searchFrendList = userInfoByPhoneResponse.getRes();
                        if (null != searchFrendList) {
                            addFrendSearchAdapter.notifyList(searchFrendList);
                        }
                    } else if (userInfoByPhoneResponse.getCode() == 120) {
                        LoadDialog.dismiss(mContext);
                        searchFrendList.clear();
                        if (null != searchFrendList) {
                            addFrendSearchAdapter.notifyList(searchFrendList);
                        }
                        NToast.shortToast(mContext, userInfoByPhoneResponse.getMsg());
                    } else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, userInfoByPhoneResponse.getMsg());
                    }
                    break;
                case ADD_FRIEND:
                    FriendInvitationResponse fres = (FriendInvitationResponse) result;
                    if (fres.getCode() == 1) {
                        NToast.shortToast(mContext, getString(R.string.request_success));
                        LoadDialog.dismiss(mContext);
                    } else {
                        NToast.shortToast(mContext, fres.getMsg());
                        LoadDialog.dismiss(mContext);
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case ADD_FRIEND:
                NToast.shortToast(mContext, "你们已经是好友");
                LoadDialog.dismiss(mContext);
                break;
            case SEARCH_PHONE:
                if (state == AsyncTaskManager.HTTP_ERROR_CODE || state == AsyncTaskManager.HTTP_NULL_CODE) {
                    super.onFailure(requestCode, state, result);
                } else {
                    NToast.shortToast(mContext, "用户不存在");
                }
                LoadDialog.dismiss(mContext);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hintKbTwo();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private boolean isFriendOrSelf(String id) {
        String inputPhoneNumber = mEtSearch.getText().toString().trim();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String selfPhoneNumber = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
        if (inputPhoneNumber != null) {
            if (inputPhoneNumber.equals(selfPhoneNumber)) {
                mFriend = new Friend(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""),
                        sp.getString(SealConst.SEALTALK_LOGIN_NAME, ""),
                        Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "")));
                return true;
            } else {
                mFriend = SealUserInfoManager.getInstance().getFriendByID(id);
                if (mFriend != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
