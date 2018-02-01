package cn.yingsuo.im.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.model.LiaoTianShiEntity;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.network.async.AsyncTaskManager;
import cn.yingsuo.im.server.network.async.OnDataListener;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.DefaultConversationResponse;
import cn.yingsuo.im.server.response.LiaoListResponse;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.ui.activity.MainActivity;
import cn.yingsuo.im.ui.adapter.LiaoTianShiAdapter;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class DiscoverFragment extends Fragment implements View.OnClickListener, OnDataListener {

    private static final int GETDEFCONVERSATION = 333;
    private AsyncTaskManager atm = AsyncTaskManager.getInstance(getActivity());
    private ArrayList<DefaultConversationResponse.ResultEntity> chatroomList;
    private RecyclerView liaotsRecyclerView;
    private LiaoTianShiAdapter liaoTianShiAdapter;
    private List<LiaoTianShiEntity> liaoTianShiEntityList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        initViews(view);
        atm.request(GETDEFCONVERSATION, this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //initTopBar();
    }

    private void initTopBar() {
        View topBarView = ((MainActivity) getActivity()).getTopBarView();
        TextView titleView = (TextView) topBarView.findViewById(R.id.topbar_title_tv);
        ImageView topRightView = (ImageView) topBarView.findViewById(R.id.seal_more);
        topRightView.setVisibility(View.GONE);
        titleView.setText("社区");
    }

    private void initViews(View view) {
        liaotsRecyclerView = (RecyclerView) view.findViewById(R.id.liaotian_shi_recyclerview);
        //回调时的线程并不是UI线程，不能在回调中直接操作UI
        RongIMClient.getInstance().setChatRoomActionListener(new RongIMClient.ChatRoomActionListener() {
            @Override
            public void onJoining(String chatRoomId) {

            }

            @Override
            public void onJoined(String chatRoomId) {

            }

            @Override
            public void onQuited(String chatRoomId) {

            }

            @Override
            public void onError(String chatRoomId, final RongIMClient.ErrorCode code) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == RongIMClient.ErrorCode.RC_NET_UNAVAILABLE || code == RongIMClient.ErrorCode.RC_NET_CHANNEL_INVALID) {
                            NToast.shortToast(getActivity(), getString(R.string.network_not_available));
                        } else {
                            NToast.shortToast(getActivity(), getString(R.string.fr_chat_room_join_failure));
                        }
                    }
                });
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        liaoTianShiAdapter = new LiaoTianShiAdapter(getActivity());
        liaoTianShiEntityList = new ArrayList<>();
        liaotsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        liaotsRecyclerView.setAdapter(liaoTianShiAdapter);
        liaoTianShiAdapter.refreshList(liaoTianShiEntityList);

    }

    @Override
    public void onClick(View v) {
        if (chatroomList == null || chatroomList.get(0) == null) {
            NToast.shortToast(getActivity(), getString(R.string.join_chat_room_error_toast));
            return;
        }
        switch (v.getId()) {
          /*  case R.id.def_chatroom1:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, chatroomList.get(0).getId(), "聊天室 I");
                break;
            case R.id.def_chatroom2:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, chatroomList.get(1).getId(), "聊天室 II");
                break;
            case R.id.def_chatroom3:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, chatroomList.get(2).getId(), "聊天室 III");
                break;
            case R.id.def_chatroom4:
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, chatroomList.get(3).getId(), "聊天室 IV");
                break;*/
        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return new SealAction(getActivity()).getLiaoList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        LiaoListResponse response = (LiaoListResponse) result;
        if (response.getCode() == 1) {
        /*    ArrayList<LiaoTianShiEntity> resultEntityArrayList = new ArrayList();
            chatroomList = new ArrayList();*/
            liaoTianShiEntityList = response.getRes();
            liaoTianShiAdapter.refreshList(liaoTianShiEntityList);
        /*    if (response.getRes().size() > 0) {
                resultEntityArrayList.clear();
                chatroomList.clear();
                for (LiaoTianShiEntity d : response.getRes()) {
                  *//*  if (d.getType().equals("group")) {
                        resultEntityArrayList.add(d);
                    } else {
                        chatroomList.add(d);
                    }*//*
                }
            }*/
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }


}
