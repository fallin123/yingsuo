package cn.yingsuo.im.server.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.yingsuo.im.R;
import cn.yingsuo.im.ui.activity.FrendQuanActivity;

import static io.rong.imkit.plugin.image.PictureSelectorActivity.REQUEST_CAMERA;

/**
 * Created by zhangfenfen on 2018/1/15.
 */

public class CameraDialog extends Dialog {

    public CameraDialog(Activity context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(final Activity context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_camera, null);
        TextView cameraView = (TextView) view.findViewById(R.id.camera);
        TextView photoView = (TextView) view.findViewById(R.id.photos);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spikCamera(context);
                dismiss();
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spikPictures(context);
                dismiss();
            }
        });
        setContentView(view);
    }

    //　　MediaStore.ACTION_IMAGE_CAPTURE 拍照；
//  　　MediaStore.ACTION_VIDEO_CAPTURE录像。
    private void spikPictures(final Activity context) {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivityForResult(intent, 2);
        } catch (Exception e) {
           
        }
    }

    private void spikCamera(final Activity context) {
        try {
            String imagePath = Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + "/yingsuo/save_img";
            String imageName = imagePath + "/"
                    + System.currentTimeMillis() + ".jpg";
            String filePath = imageName;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(filePath)));
            context.startActivityForResult(intent,
                    REQUEST_CAMERA);
        } catch (Exception e) {

        }
    }
}
