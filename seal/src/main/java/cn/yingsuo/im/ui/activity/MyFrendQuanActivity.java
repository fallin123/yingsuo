package cn.yingsuo.im.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.FrendQuanListResponse;
import cn.yingsuo.im.server.utils.CommonUtils;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.photo.PhotoUtils;
import cn.yingsuo.im.server.widget.BottomMenuDialog;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.ui.adapter.FrendQuanAdapter;
import cn.yingsuo.im.ui.adapter.MyFrendQuanAdapter;

/**
 * Created by zhangfenfen on 2018/1/13.
 */

public class MyFrendQuanActivity extends BaseActivity {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private final static String TAG = "MyFrendQuanActivity";
    private static final int GET_FREND_LIST = 1;
    private BottomMenuDialog dialog;
    private PhotoUtils photoUtils;
    private View backView;
    private View rightView;
    //private XRefreshView xRefreshView;
    private RecyclerView myFrendQuanListView;
    private MyFrendQuanAdapter myFrendQuanAdapter;
    private List<FrendQuanEntity> frendQuanEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_frend_quan);
        initView();
        LoadDialog.show(mContext);
        request(GET_FREND_LIST);
    }

    private void initView() {
        mHeadLayout.setVisibility(View.GONE);
        backView = findViewById(R.id.frend_btn_left);
        rightView = findViewById(R.id.frend_btn_right);
        myFrendQuanListView = (RecyclerView) findViewById(R.id.recycler);
        //xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CameraDialog cameraDialog = new CameraDialog(FrendQuanActivity.this,R.style.jrmf_commondialog);
                // cameraDialog.show();
                showPhotoDialog();

            }
        });
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                ArrayList<String> images = new ArrayList<>();
                images.add(uri.getPath());
                Intent intent = new Intent(MyFrendQuanActivity.this, FrendQuanSendActivity.class);
                intent.putExtra("images", images);
                startActivity(intent);
            }

            @Override
            public void onPhotoCancel() {

            }
        });
        photoUtils.setCameraPicPath("/yingsuo/"+"crop_frend_file0"+".jpg");
        initListView();
    }

    private void initListView() {
        myFrendQuanAdapter = new MyFrendQuanAdapter(mContext);
        myFrendQuanListView.setAdapter(myFrendQuanAdapter);
        myFrendQuanListView.setLayoutManager(new LinearLayoutManager(this));
        frendQuanEntities = new ArrayList<>();
        myFrendQuanAdapter.notifyList(frendQuanEntities);

    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_FREND_LIST:
                return action.getMyFrendQuanList();
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (null != result) {
            switch (requestCode) {
                case GET_FREND_LIST:
                    LoadDialog.dismiss(MyFrendQuanActivity.this);
                    FrendQuanListResponse frendQuanListResponse = (FrendQuanListResponse) result;
                    if (frendQuanListResponse.getCode() == 1) {
                        frendQuanEntities = frendQuanListResponse.getRes();
                        myFrendQuanAdapter.notifyList(frendQuanEntities);
                    } else {
                        NToast.longToast(MyFrendQuanActivity.this, frendQuanListResponse.getMsg());
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
            case GET_FREND_LIST:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, R.string.login_api_fail);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoUtils.INTENT_SELECT://选图成功返回
                if (resultCode == RESULT_OK) { // 如果返回数据
                    if (data != null) {
                        ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                        Intent intent = new Intent(MyFrendQuanActivity.this, FrendQuanSendActivity.class);
                        intent.putExtra("images", images);
                        startActivity(intent);
                    }
                }
                break;
            case PhotoUtils.INTENT_TAKE://拍照成功返回
                photoUtils.onActivityResultForCamera(MyFrendQuanActivity.this,requestCode);
                break;
        }

    }

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
                photoUtils.takePicture(MyFrendQuanActivity.this);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectAllPicture(MyFrendQuanActivity.this, 9);
            }
        });
        dialog.show();
    }
}
