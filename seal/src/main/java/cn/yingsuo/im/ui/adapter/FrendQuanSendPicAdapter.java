package cn.yingsuo.im.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.entry.Image;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.R;
import cn.yingsuo.im.server.widget.ShowPicSelectDialogUtil;
import cn.yingsuo.im.ui.activity.FrendQuanSendActivity;

/**
 * Created by zhangfenfen on 2018/1/17.
 */

public class FrendQuanSendPicAdapter extends RecyclerView.Adapter<FrendQuanSendPicAdapter.ImageHolder> {
    private Activity mContext;
    private ArrayList<String> mImages;
    private ArrayList<String> mImagesTemp;
    private LayoutInflater mInflater;

    public FrendQuanSendPicAdapter(Activity context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mImages = new ArrayList<>();
        this.mImagesTemp = new ArrayList<>();
    }

    public void notifyList(ArrayList<String> list) {
        if (list != null) {
            this.mImages.clear();
            this.mImages.addAll(list);
            this.mImagesTemp = list;
            if (list.size() < 9) {
                this.mImages.add("add");
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.frend_quan_adapter_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        final String image = mImages.get(position);
        if (mImages.get(position).equals("add")) {
            holder.ivImage.setImageResource(R.drawable.add_card);
        } else {
            Glide.with(mContext).load(new File(image)).into(holder.ivImage);
        }
        holder.bindView(position);


    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;

        public ImageHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
        }

        private void bindView(final int position) {
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImages.get(position).equals("add")) {
                        int pciNum = 10 - mImages.size();
                        ((FrendQuanSendActivity) mContext).showPhotoDialog(pciNum);
                    } else {
                        ImageSelectorUtils.openPhotoDelete(mContext, mImagesTemp, position);
                    }
                }
            });
        }
    }
}
