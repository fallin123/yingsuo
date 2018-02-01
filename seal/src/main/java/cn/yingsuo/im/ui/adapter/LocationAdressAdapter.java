package cn.yingsuo.im.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.model.LocationAdressEntity;
import cn.yingsuo.im.ui.activity.BaseActivity;

/**
 * Created by zhangfenfen on 2018/1/18.
 */

public class LocationAdressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int ITEM_TOP = 1;
    private final static int ITEM_ITEM = 0;
    private BaseActivity context;
    private List<LocationAdressEntity> adressList;
    private LayoutInflater layoutInflater;

    public LocationAdressAdapter(BaseActivity context) {
        this.context = context;
        this.adressList = new ArrayList<>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void notifyList(List<LocationAdressEntity> adressList) {
        this.adressList.clear();
        this.adressList.addAll(adressList);
        this.adressList.add(0, new LocationAdressEntity());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_ITEM) {
            return new ItemHolder(layoutInflater.inflate(R.layout.location_adress_item, parent, false));
        } else {
            return new HeadHolder(layoutInflater.inflate(R.layout.location_adress_head, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.bindView(position);
        } else {
            HeadHolder headHolder = (HeadHolder) holder;
            headHolder.bindView();
        }
    }

    @Override
    public int getItemCount() {
        return adressList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TOP;
        } else {
            return ITEM_ITEM;
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder {

        public HeadHolder(View itemView) {
            super(itemView);
        }

        private void bindView() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    LocationAdressEntity entity = new LocationAdressEntity();
                    entity.setTitle("所在位置");
                    intent.putExtra("address", entity);
                    context.setResult(Activity.RESULT_OK, intent);
                    context.finish();
                }
            });
        }

    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView detailTv;

        public ItemHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.location_adress_title);
            detailTv = (TextView) itemView.findViewById(R.id.location_adress_detail);
        }

        private void bindView(final int position) {
            titleTv.setText(adressList.get(position).getTitle());
            detailTv.setText(adressList.get(position).getDetail());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("address", adressList.get(position));
                    context.setResult(Activity.RESULT_OK, intent);
                    context.finish();
                }
            });
        }
    }
}
