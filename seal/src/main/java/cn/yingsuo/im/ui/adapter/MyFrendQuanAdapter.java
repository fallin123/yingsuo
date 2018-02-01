package cn.yingsuo.im.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.SealConst;
import cn.yingsuo.im.SealUserInfoManager;
import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.model.LocationAdressEntity;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.widget.CircleImageView;
import cn.yingsuo.im.server.widget.MergePictureView;
import cn.yingsuo.im.ui.activity.FrendQuanActivity;
import cn.yingsuo.im.ui.activity.FrendQuanDetailActivity;
import cn.yingsuo.im.ui.activity.ImagePagerActivity;
import cn.yingsuo.im.ui.activity.MyFrendQuanActivity;
import cn.yingsuo.im.utils.DateUtils;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class MyFrendQuanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FrendQuanEntity> list;
    private Context context;

    public MyFrendQuanAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void notifyList(List<FrendQuanEntity> list) {
        if (null != list) {
            this.list.clear();
            this.list.addAll(list);
            this.list.add(0, new FrendQuanEntity(FrendQuanEntity.TYPE_TOP));
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == FrendQuanEntity.TYPE_TOP) {
            holder = new FrendQuanHeadHolder(LayoutInflater.from(context).inflate(R.layout.adapter_frend_quan_head, parent, false));
        } else {
            holder = new FrendQuanHolder(LayoutInflater.from(context).inflate(R.layout.adapter_my_frend_quan_item, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FrendQuanHeadHolder) {
            FrendQuanHeadHolder frendQuanHeadHolder = (FrendQuanHeadHolder) holder;
            frendQuanHeadHolder.bindView();
        } else {
            FrendQuanHolder frendQuanHolder = (FrendQuanHolder) holder;
            frendQuanHolder.bindView(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FrendQuanEntity.TYPE_TOP;
        } else {
            return FrendQuanEntity.TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class FrendQuanHolder extends RecyclerView.ViewHolder {

        public FrendQuanHolder(View itemView) {
            super(itemView);
        }

        public void bindView(int position) {
            MergePictureView mergePictureView = (MergePictureView) itemView.findViewById(R.id.quan_image);
            TextView dateTv = (TextView) itemView.findViewById(R.id.frend_date_tv);
            TextView monthTv = (TextView) itemView.findViewById(R.id.frend_month_tv);
            final TextView contentTv = (TextView) itemView.findViewById(R.id.content_iv);
            TextView imageCountTv = (TextView) itemView.findViewById(R.id.image_count_tv);
            final FrendQuanEntity frendQuanEntity = list.get(position);
            contentTv.setText(frendQuanEntity.getContent());
            imageCountTv.setText("共" + frendQuanEntity.getImg().length + "张");
            String date = DateUtils.dateToString(new Date(Long.parseLong(frendQuanEntity.getAdd_time()) * 1000), "dd");
            String month = DateUtils.dateToString(new Date(Long.parseLong(frendQuanEntity.getAdd_time()) * 1000), "mm月");
            dateTv.setText(date);
            monthTv.setText(month);
            int[] resourcesIdsFirst = new int[]{R.drawable._102, R.drawable._103, R.drawable._104, R.drawable._105};
            //mergePictureView.setDrawableIds(resourcesIdsFirst);
            mergePictureView.getBitmapFromRul(frendQuanEntity.getImg());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FrendQuanDetailActivity.class);
                    intent.putExtra("myFrendQuanEntity", frendQuanEntity);
                    context.startActivity(intent);
                }
            });
        }
    }

    private class FrendQuanHeadHolder extends RecyclerView.ViewHolder {

        public FrendQuanHeadHolder(View itemView) {
            super(itemView);
        }

        public void bindView() {
            CircleImageView iconView = (CircleImageView) itemView.findViewById(R.id.frend_quan_icon);
            TextView slogon = (TextView) itemView.findViewById(R.id.frend_quan_slogon);
            UserInfoResponse userInfoResponse = App.getUserInfoResponse();

            String portraitUri = userInfoResponse.getImg();
            ImageLoader.getInstance().displayImage(SealAction.BASE_URL + portraitUri, iconView, App.getOptions());
            slogon.setText(userInfoResponse.getSlogon());
        }
    }
}
