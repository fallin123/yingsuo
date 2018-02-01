package cn.yingsuo.im.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrmf360.rylib.JrmfClient;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.broadcast.BroadcastManager;
import cn.yingsuo.im.server.network.async.AsyncTaskManager;
import cn.yingsuo.im.server.network.async.OnDataListener;
import cn.yingsuo.im.server.network.http.HttpException;
import cn.yingsuo.im.server.response.VersionResponse;
import cn.yingsuo.im.server.widget.SelectableRoundedImageView;
import cn.yingsuo.im.ui.activity.AboutRongCloudActivity;
import cn.yingsuo.im.ui.activity.AccountSettingActivity;
import cn.yingsuo.im.ui.activity.FrendQuanActivity;
import cn.yingsuo.im.ui.activity.MainActivity;
import cn.yingsuo.im.ui.activity.MyAccountActivity;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/6/21.
 * Company RongCloud
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static final int COMPARE_VERSION = 54;
    public static final String SHOW_RED = "SHOW_RED";
    private SharedPreferences sp;
    private SelectableRoundedImageView imageView;
    private TextView mName;
    private ImageView mNewVersionView;
    private boolean isHasNewVersion;
    private String url;
    private boolean isDebug;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.seal_mine_fragment, container, false);
        isDebug = getContext().getSharedPreferences("config", getContext().MODE_PRIVATE).getBoolean("isDebug", false);
        initViews(mView);
        initData();
        BroadcastManager.getInstance(getActivity()).addAction(SealConst.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserInfo();
            }
        });
        compareVersion();
        return mView;
    }

    private void compareVersion() {
        AsyncTaskManager.getInstance(getActivity()).request(COMPARE_VERSION, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getSealTalkVersion();
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    VersionResponse response = (VersionResponse) result;
                    String[] s = response.getAndroid().getVersion().split("\\.");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < s.length; i++) {
                        sb.append(s[i]);
                    }

                    String[] s2 = getVersionInfo()[1].split("\\.");
                    StringBuilder sb2 = new StringBuilder();
                    for (int i = 0; i < s2.length; i++) {
                        sb2.append(s2[i]);
                    }
                    if (Integer.parseInt(sb.toString()) > Integer.parseInt(sb2.toString())) {
                        mNewVersionView.setVisibility(View.GONE);
                        url = response.getAndroid().getUrl();
                        isHasNewVersion = true;
                        BroadcastManager.getInstance(getActivity()).sendBroadcast(SHOW_RED);
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
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
        titleView.setText("我");
    }

    private void initData() {
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        updateUserInfo();
    }

    private void initViews(View mView) {
        mNewVersionView = (ImageView) mView.findViewById(R.id.new_version_icon);
        imageView = (SelectableRoundedImageView) mView.findViewById(R.id.mine_header);
        mName = (TextView) mView.findViewById(R.id.mine_name);
        LinearLayout mUserProfile = (LinearLayout) mView.findViewById(R.id.start_user_profile);
        LinearLayout mMineFrends = (LinearLayout) mView.findViewById(R.id.mine_frending);
        LinearLayout mMinePhotos = (LinearLayout) mView.findViewById(R.id.mine_photos);
        LinearLayout mMineNears = (LinearLayout) mView.findViewById(R.id.mine_nears);
        LinearLayout mMineScan = (LinearLayout) mView.findViewById(R.id.mine_scan);
        LinearLayout mMineWallet = (LinearLayout) mView.findViewById(R.id.my_wallet);
        LinearLayout mMineStore = (LinearLayout) mView.findViewById(R.id.mine_store);
        LinearLayout mMineMemberCenter = (LinearLayout) mView.findViewById(R.id.mine_member_center);
        LinearLayout mMineShareing = (LinearLayout) mView.findViewById(R.id.mine_shareing);
        LinearLayout mMineContract = (LinearLayout) mView.findViewById(R.id.mine_contract);
        LinearLayout mMineSetting = (LinearLayout) mView.findViewById(R.id.mine_setting);
     /*   if (isDebug) {
            mMineXN.setVisibility(View.VISIBLE);
        } else {
            mMineXN.setVisibility(View.GONE);
        }*/
        mUserProfile.setOnClickListener(this);
        mMineFrends.setOnClickListener(this);
        mMinePhotos.setOnClickListener(this);
        mMineScan.setOnClickListener(this);
        mMineWallet.setOnClickListener(this);
        mMineStore.setOnClickListener(this);
        mMineNears.setOnClickListener(this);
        mMineMemberCenter.setOnClickListener(this);
        mMineShareing.setOnClickListener(this);
        mMineContract.setOnClickListener(this);
        mMineSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_user_profile:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;
            case R.id.mine_setting:
                startActivity(new Intent(getActivity(), AccountSettingActivity.class));
                break;
            case R.id.mine_frending:
                startActivity(new Intent(getActivity(), FrendQuanActivity.class));
                break;
            case R.id.mine_contract:
                CSCustomServiceInfo.Builder builder = new CSCustomServiceInfo.Builder();
                builder.province("北京");
                builder.city("北京");
                RongIM.getInstance().startCustomerServiceChat(getActivity(), "KEFU146001495753714", "在线客服", builder.build());
                // KEFU146001495753714 正式  KEFU145930951497220 测试  小能: zf_1000_1481459114694   zf_1000_1480591492399
                break;
            case R.id.mine_member_center:
                CSCustomServiceInfo.Builder builder1 = new CSCustomServiceInfo.Builder();
                builder1.province("北京");
                builder1.city("北京");
                RongIM.getInstance().startCustomerServiceChat(getActivity(), "zf_1000_1481459114694", "在线客服", builder1.build());
                break;
           /* case R.id.mine_about:
                mNewVersionView.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), AboutRongCloudActivity.class);
                intent.putExtra("isHasNewVersion", isHasNewVersion);
                if (!TextUtils.isEmpty(url)) {
                    intent.putExtra("url", url);
                }
                startActivity(intent);
                break;*/
            case R.id.mine_photos:
                break;
            case R.id.mine_nears:
                break;
            case R.id.mine_scan:
                break;
            case R.id.mine_shareing:
                showShare();
                break;
            case R.id.mine_store:
                break;
            case R.id.my_wallet:
                JrmfClient.intentWallet(getActivity());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUserInfo() {
        String userId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "");
        String username = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
        String userPortrait = sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "");
        mName.setText(username);
        if (!TextUtils.isEmpty(userId)) {
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri
                    (new UserInfo(userId, username, Uri.parse(userPortrait)));
            ImageLoader.getInstance().displayImage(portraitUri, imageView, App.getOptions());
        }
    }

    private String[] getVersionInfo() {
        String[] version = new String[2];

        PackageManager packageManager = getActivity().getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            version[0] = String.valueOf(packageInfo.versionCode);
            version[1] = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不     调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(getActivity());
    }
}
