package cn.yingsuo.im.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.model.LiaoTianShiEntity;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.widget.SelectableRoundedImageView;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by zhangfenfen on 2018/1/22.
 */

public class LiaoTianShiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<LiaoTianShiEntity> list;

    public LiaoTianShiAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void refreshList(List<LiaoTianShiEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(inflater.inflate(R.layout.fragment_chatroom_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }

        private void bindView(int position) {
            final LiaoTianShiEntity liaoTianShiEntity = list.get(position);
            SelectableRoundedImageView liaotianIcon = (SelectableRoundedImageView) itemView.findViewById(R.id.liaotian_icon);
            TextView liaotianTitle = (TextView) itemView.findViewById(R.id.liaotian_title);
            ImageLoader.getInstance().displayImage(SealAction.BASE_URL + liaoTianShiEntity.getImg(), liaotianIcon, App.getOptions());
            liaotianTitle.setText(liaoTianShiEntity.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RongIM.getInstance().startConversation(context, Conversation.ConversationType.CHATROOM, liaoTianShiEntity.getId(), liaoTianShiEntity.getName());
                }
            });
        }

    }
}
