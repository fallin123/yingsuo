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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.donkingliang.imageselector.constant.Constants;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.model.LocationAdressEntity;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.network.net.ApiHelp;
import cn.yingsuo.im.server.response.FrendQuanSendResponse;
import cn.yingsuo.im.server.utils.NToast;
import cn.yingsuo.im.server.utils.photo.PhotoUtils;
import cn.yingsuo.im.server.widget.BottomMenuDialog;
import cn.yingsuo.im.server.widget.DialogWithYesOrNoUtils;
import cn.yingsuo.im.server.widget.LoadDialog;
import cn.yingsuo.im.ui.adapter.FrendQuanSendPicAdapter;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class FrendQuanSendActivity extends BaseActivity {
    private final static String TAG = "FrendQuanSendActivity";
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private static final int SEND_FREND_QUAN = 1;
    private static final int REQUEST_ADRESS = 2;
    private static final int REQUEST_DELETE_PIC = 10;
    private View backView;
    private View rightView;
    private View locationView;
    private TextView locationTv;
    private String content;
    private String address = "";
    private EditText sendContentEdit;
    private RecyclerView imageSelectRyv;
    private FrendQuanSendPicAdapter frendQuanSendPicAdapter;
    private ArrayList<String> imageList;
    private BottomMenuDialog dialog;
    private PhotoUtils photoUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frend_quan_send);
        initView();
    }

    private void initView() {
        mHeadLayout.setVisibility(View.GONE);
        backView = findViewById(R.id.frend_send_btn_left);
        rightView = findViewById(R.id.frend_send_btn_right);
        locationView = findViewById(R.id.location_layout);
        locationTv = (TextView) findViewById(R.id.location_tv);
        sendContentEdit = (EditText) findViewById(R.id.frend_send_content_edit);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackDialog();
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = sendContentEdit.getText().toString().trim();
                requestSendFrendQaun();
            }
        });
        locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(FrendQuanSendActivity.this, LocationAdressListActivity.class), REQUEST_ADRESS);
            }
        });
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                imageList.add(uri.getPath());
                frendQuanSendPicAdapter.notifyList(imageList);
            }

            @Override
            public void onPhotoCancel() {

            }
        });
        photoUtils.setCameraPicPath("/yingsuo/" + "crop_frend_file" + "12" + ".jpg");
        initPicSelectView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            showBackDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initPicSelectView() {
        imageList = new ArrayList<>();
        imageSelectRyv = (RecyclerView) findViewById(R.id.frend_add_pic_gridview);
        frendQuanSendPicAdapter = new FrendQuanSendPicAdapter(this);
        imageSelectRyv.setAdapter(frendQuanSendPicAdapter);
        imageSelectRyv.setLayoutManager(new GridLayoutManager(this, 4));
        Intent intent = getIntent();
        if (null != intent) {
            List<String> list = intent.getStringArrayListExtra("images");
            if (null != list) {
                imageList.addAll(list);
            }
        }
        frendQuanSendPicAdapter.notifyList(imageList);
    }

    private void requestSendFrendQaun() {
        LoadDialog.show(mContext);
        List<File> files = new ArrayList<>();
        for (String path : imageList) {
            files.add(new File(path));
        }
        try {
            action.sendFrendQuan(this, content, address, files, new ApiHelp.IApiCallBack() {
                @Override
                public void onApiCallBack(Object object, int index) {
                    LoadDialog.dismiss(mContext);
                }
            });
        } catch (HttpException e) {
            e.printStackTrace();
            LoadDialog.dismiss(mContext);
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case SEND_FREND_QUAN:
                break;

        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case SEND_FREND_QUAN:
                FrendQuanSendResponse frendQuanSendResponse = (FrendQuanSendResponse) result;
                if (frendQuanSendResponse.getCode() == 1) {
                    startActivity(new Intent(FrendQuanSendActivity.this, FrendQuanActivity.class));
                    finish();
                } else {
                    NToast.longToast(FrendQuanSendActivity.this, frendQuanSendResponse.getMsg());
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case SEND_FREND_QUAN:
                break;
        }
    }

    private void showBackDialog() {
        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否退出编辑?", new DialogWithYesOrNoUtils.DialogCallBack() {
            @Override
            public void executeEvent() {
                finish();
            }

            @Override
            public void executeEditEvent(String editText) {

            }

            @Override
            public void updatePassword(String oldPassword, String newPassword) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_SELECT://选图成功返回
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                    imageList.addAll(images);
                    frendQuanSendPicAdapter.notifyList(imageList);
                }
                break;
            case REQUEST_ADRESS:
                if (resultCode == RESULT_OK && data != null) {
                    LocationAdressEntity adress = (LocationAdressEntity) data.getSerializableExtra("address");
                    locationTv.setText(adress.getTitle());
                }
                break;
            case Constants.RESULT_CODE:
                frendQuanSendPicAdapter.notifyList(imageList);
                break;
            case PhotoUtils.INTENT_TAKE://拍照成功返回
                photoUtils.onActivityResultForCamera(FrendQuanSendActivity.this, requestCode);
                break;
        }

    }

    /**
     * 弹出底部框
     */
    @TargetApi(23)
    public void showPhotoDialog(final int picNum) {
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
                photoUtils.takePicture(FrendQuanSendActivity.this);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectAllPicture(FrendQuanSendActivity.this, picNum);
            }
        });
        dialog.show();
    }
}
