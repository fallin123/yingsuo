package cn.yingsuo.im.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.yingsuo.im.R;
import cn.yingsuo.im.SealAppContext;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.db.Friend;
import cn.yingsuo.im.server.broadcast.BroadcastManager;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.pinyin.CharacterParser;
import cn.yingsuo.im.server.response.AgreeFriendsResponse;
import cn.yingsuo.im.server.response.NewFrendResultResponse;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.response.UserRelationshipResponse;
import cn.yingsuo.im.server.utils.CommonUtils;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.ui.adapter.NewFriendListAdapter;


public class NewFriendListActivity extends BaseActivity implements NewFriendListAdapter.OnItemButtonClick, View.OnClickListener {

    private static final int GET_ALL = 11;
    private static final int AGREE_FRIENDS = 12;
    public static final int FRIEND_LIST_REQUEST_CODE = 1001;
    private ListView shipListView;
    private NewFriendListAdapter adapter;
    private String friendId, uid;
    private TextView isData;
    private UserRelationshipResponse userRelationshipResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friendlist);
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }
        LoadDialog.show(mContext);
        request(GET_ALL);
        adapter = new NewFriendListAdapter(mContext);
        shipListView.setAdapter(adapter);
    }

    protected void initView() {
        setTitle(R.string.new_friends);
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
        Button rightButton = getHeadRightButton();
        rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.de_address_new_friend));
        rightButton.setOnClickListener(this);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_ALL:
                return action.getAllUserRelationship();
            case AGREE_FRIENDS:
                return action.agreeFriends(friendId,uid);
        }
        return super.doInBackground(requestCode, id);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_ALL:
                    userRelationshipResponse = (UserRelationshipResponse) result;

                    if (userRelationshipResponse.getRes().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }

                    Collections.sort(userRelationshipResponse.getRes(), new Comparator<NewFrendResultResponse>() {

                        @Override
                        public int compare(NewFrendResultResponse lhs, NewFrendResultResponse rhs) {
                            Date date1 = stringToDate(lhs);
                            Date date2 = stringToDate(rhs);
                            if (date1.before(date2)) {
                                return 1;
                            }
                            return -1;
                        }
                    });

                    adapter.removeAll();
                    adapter.addData(userRelationshipResponse.getRes());

                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                    break;
                case AGREE_FRIENDS:
                    AgreeFriendsResponse afres = (AgreeFriendsResponse) result;
                    if (afres.getCode() == 200) {
                        NewFrendResultResponse bean = userRelationshipResponse.getRes().get(index);
                        SealUserInfoManager.getInstance().addFriend(new Friend(bean.getId(),
                                bean.getNi_name(),
                                Uri.parse(bean.getHead_img()),
                                bean.getNi_name(),
                                String.valueOf(bean.getType()),
                                null,
                                null,
                                null,
                                CharacterParser.getInstance().getSpelling(bean.getNi_name()),
                                CharacterParser.getInstance().getSpelling(bean.getNi_name())));
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                        request(GET_ALL); //刷新 UI 按钮
                    }

            }
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_ALL:
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }

    private int index;

    @Override
    public boolean onButtonClick(int position, View view, int status) {
        index = position;
        switch (status) {
            case 2: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                LoadDialog.show(mContext);
//                friendId = null;
                friendId = userRelationshipResponse.getRes().get(position).getId();
                uid = userRelationshipResponse.getRes().get(position).getUid();
                request(AGREE_FRIENDS);
                break;
            case 10: // 发出了好友邀请
                break;
            case 21: // 忽略好友邀请
                break;
            case 20: // 已是好友
                break;
            case 30: // 删除了好友关系
                break;
        }
        return false;
    }

    private Date stringToDate(NewFrendResultResponse resultEntity) {
        Long updatedAt = Long.parseLong(resultEntity.getAdd_time());
        //String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        java.util.Date dt = new Date(updatedAt * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        String updateAtDateStr = null;
        try {
            updateAtDateStr = simpleDateFormat.format(dt);
            updateAtDate = simpleDateFormat.parse(updateAtDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, FRIEND_LIST_REQUEST_CODE);
    }
}
