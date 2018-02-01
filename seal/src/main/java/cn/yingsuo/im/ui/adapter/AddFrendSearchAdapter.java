package cn.yingsuo.im.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.widget.CircleImageView;
import cn.yingsuo.im.server.widget.MergePictureView;
import cn.yingsuo.im.server.widget.SelectableRoundedImageView;
import cn.yingsuo.im.utils.DateUtils;
import io.rong.imageloader.core.ImageLoader;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class AddFrendSearchAdapter extends RecyclerView.Adapter<AddFrendSearchAdapter.ItemHolder> {
    private List<UserInfoResponse> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AddFrendSearchAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void notifyList(List<UserInfoResponse> list) {
        if (null != list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.add_frend_result_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bindView(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }

        private void bindView(final int position) {
            SelectableRoundedImageView headImage = (SelectableRoundedImageView) itemView.findViewById(R.id.search_header);
            TextView nameTv = (TextView) itemView.findViewById(R.id.search_name);
            UserInfoResponse frend = list.get(position);
            nameTv.setText(frend.getNi_name());
            ImageLoader.getInstance().displayImage(frend.getImg(), headImage, App.getOptions());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
