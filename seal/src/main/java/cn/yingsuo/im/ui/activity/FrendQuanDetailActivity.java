package cn.yingsuo.im.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.model.CommentConfig;
import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.model.FrendZanEntity;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.FrendQuanListResponse;
import cn.yingsuo.im.server.response.PingQuanResponse;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.response.ZanQuanResponse;
import cn.yingsuo.im.server.utils.CommonUtils;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.photo.PhotoUtils;
import cn.yingsuo.im.server.widget.BottomMenuDialog;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.ui.adapter.FrendQuanAdapter;
import cn.yingsuo.im.ui.widget.CommentListView;
import cn.yingsuo.im.ui.widget.DivItemDecoration;
import cn.yingsuo.im.utils.DisplayUtils;

/**
 * Created by zhangfenfen on 2018/1/13.
 */

public class FrendQuanDetailActivity extends BaseActivity {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final static String TAG = "FrendQuanActivity";
    private static final int REQUEST_CLICK_ZAN = 2;
    private static final int REQUEST_CLICK_PINGLUN = 3;
    private View titleBar;
    private View backView;
    //private XRefreshView xRefreshView;
    private View frendCommentView;
    private RelativeLayout bodyLayout;
    private EditText frendCommentEdit;
    private ImageView frendCommentBtn;
    private SuperRecyclerView frendQuanListView;
    private FrendQuanAdapter frendQuanAdapter;
    private LinearLayoutManager layoutManager;
    private List<FrendQuanEntity> frendQuanEntities;
    private FrendQuanEntity zanFrendQuanEntity;
    private FrendZanEntity frendZanEntity;
    private String uid, zanId, pingId, hId, pingContent;

    /*评论框弹出*/
    private CommentConfig commentConfig;
    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frend_quan_detail);
        initView();
        //LoadDialog.show(mContext);
        //request(GET_FREND_LIST);
    }

    private void initView() {
        mHeadLayout.setVisibility(View.GONE);
        titleBar = findViewById(R.id.layout_head);
        backView = findViewById(R.id.frend_btn_left);
        frendCommentView = findViewById(R.id.frend_comment_view);
        frendCommentEdit = (EditText) findViewById(R.id.frend_comment_edit);
        frendCommentBtn = (ImageView) findViewById(R.id.frend_comment_send_btn);
        frendQuanListView = (SuperRecyclerView) findViewById(R.id.recycler);
        //xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        UserInfoResponse userInfoResponse = App.getUserInfoResponse();
        uid = userInfoResponse.getId();
        initListView();
        setViewTreeObserver();
    }

    private void initListView() {
        frendQuanAdapter = new FrendQuanAdapter(this);
        frendQuanListView.setAdapter(frendQuanAdapter);
        layoutManager = new LinearLayoutManager(this);
        frendQuanListView.setLayoutManager(layoutManager);
        frendQuanEntities = new ArrayList<>();
        FrendQuanEntity entity = (FrendQuanEntity) getIntent().getSerializableExtra("myFrendQuanEntity");
        frendQuanEntities.add(entity);
        frendQuanAdapter.notifyList(frendQuanEntities);
        frendQuanListView.addItemDecoration(new DivItemDecoration(2, true));
        frendQuanListView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        //实现自动下拉刷新功能
        frendQuanListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(FrendQuanDetailActivity.this).resumeRequests();
                } else {
                    Glide.with(FrendQuanDetailActivity.this).pauseRequests();
                }

            }
        });
        frendQuanAdapter.setOnMorePopuItemClickListener(new FrendQuanAdapter.OnMorePopuItemClickListener() {
            @Override
            public void onClickZan(FrendQuanEntity entity) {
                zanFrendQuanEntity = entity;
                frendZanEntity = new FrendZanEntity();
                UserInfoResponse userInfoResponse = App.getUserInfoResponse();
                frendZanEntity.setName(userInfoResponse.getNi_name());
                zanId = entity.getId();
                LoadDialog.show(mContext);
                request(REQUEST_CLICK_ZAN);
            }

            @Override
            public void onCickPingLun(FrendQuanEntity entity, String mHid, int position, CommentConfig commentConfig) {
                updateEditTextBodyVisible(View.VISIBLE, commentConfig);
                pingId = entity.getId();
                hId = mHid;
            }
        });
        frendQuanListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (frendCommentView.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });
        frendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pingContent = frendCommentEdit.getText().toString().trim();
                if (null == pingContent || "".equals(pingContent)) {
                    NToast.longToast(FrendQuanDetailActivity.this, "内容不能为空");
                    return;
                }
                LoadDialog.show(mContext);
                request(REQUEST_CLICK_PINGLUN);
            }
        });

    }

    /**
     * 点击评论弹出评论框
     */
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        frendCommentView.setVisibility(visibility);

        measureCircleItemHighAndCommentItemOffset(commentConfig);

        if (View.VISIBLE == visibility) {
            frendCommentEdit.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(frendCommentEdit.getContext(), frendCommentEdit);

        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(frendCommentEdit.getContext(), frendCommentEdit);
        }
    }

    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + 1 - firstPosition);

        if (selectCircleItem != null) {
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if (commentLv != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    private void setViewTreeObserver() {
        bodyLayout = (RelativeLayout) findViewById(R.id.frend_quan_body_layout);
        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                Log.d(TAG, "screenH＝ " + screenH + " &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = frendCommentView.getHeight();

                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if (layoutManager != null && commentConfig != null) {
                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + 1, getListviewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 测量偏移量
     *
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if (commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int titleBarH = DisplayUtils.dip2px(this, 48);
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight - titleBarH;
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        Log.i(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case REQUEST_CLICK_ZAN:
                return action.zanQuan(uid, zanId);
            case REQUEST_CLICK_PINGLUN:
                return action.pingQuan(uid, pingId, hId, pingContent);
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (null != result) {
            switch (requestCode) {
                case REQUEST_CLICK_ZAN:
                    ZanQuanResponse zanQuanResponse = (ZanQuanResponse) result;
                    if (zanQuanResponse.getCode() == 1) {
                        LoadDialog.dismiss(mContext);
                        frendQuanAdapter.notifyItemZan(zanFrendQuanEntity, frendZanEntity);
                    } else {
                        LoadDialog.dismiss(mContext);
                        NToast.longToast(mContext, zanQuanResponse.getMsg());
                    }
                    break;
                case REQUEST_CLICK_PINGLUN:
                    PingQuanResponse pingQuanResponse = (PingQuanResponse) result;
                    if (pingQuanResponse.getCode() == 1) {
                        LoadDialog.dismiss(mContext);
                    } else {
                        LoadDialog.dismiss(mContext);
                        NToast.longToast(mContext, pingQuanResponse.getMsg());
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (!CommonUtils.isNetworkConnected(mContext)) {
            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, getString(R.string.network_not_available));
            return;
        }
        switch (requestCode) {
            case REQUEST_CLICK_ZAN:
                LoadDialog.dismiss(mContext);
                break;
            case REQUEST_CLICK_PINGLUN:
                LoadDialog.dismiss(mContext);
                break;
        }
    }


    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
