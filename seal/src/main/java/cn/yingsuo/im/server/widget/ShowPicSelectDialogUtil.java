package cn.yingsuo.im.server.widget;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import cn.yingsuo.im.server.utils.photo.PhotoUtils;
import cn.yingsuo.im.server.widget.BottomMenuDialog;
import cn.yingsuo.im.ui.activity.FrendQuanActivity;

/**
 * Created by zhangfenfen on 2018/1/17.
 */

public class ShowPicSelectDialogUtil {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Activity mContext;
    private BottomMenuDialog dialog;
    private PhotoUtils photoUtils;

    public ShowPicSelectDialogUtil(Activity mContext) {
        this.mContext = mContext;
        setPortraitChangeListener();
    }

    /**
     * 弹出底部框
     */
    @TargetApi(23)
    public void showPhotoDialog(final int requestCode, final int picNum) {
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
                    int checkPermission = mContext.checkSelfPermission(Manifest.permission.CAMERA);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        if (mContext.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            mContext.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setMessage("您需要在设置里打开相机权限。")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mContext.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create().show();
                        }
                        return;
                    }
                }
                photoUtils.takePicture(mContext);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                //photoUtils.selectAllPicture(mContext, requestCode, picNum);
            }
        });
        dialog.show();
    }

    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }
}
