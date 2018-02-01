package cn.yingsuo.im.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.yingsuo.im.App;
import cn.yingsuo.im.R;
import cn.yingsuo.im.model.ActionItem;
import cn.yingsuo.im.model.CommentConfig;
import cn.yingsuo.im.model.FrendQuanEntity;
import cn.yingsuo.im.model.FrendZanEntity;
import cn.yingsuo.im.model.PhotoInfo;
import cn.yingsuo.im.server.SealAction;
import cn.yingsuo.im.server.response.UserInfoResponse;
import cn.yingsuo.im.server.widget.CircleImageView;
import cn.yingsuo.im.ui.activity.FrendQuanActivity;
import cn.yingsuo.im.ui.activity.ImagePagerActivity;
import cn.yingsuo.im.ui.activity.MyFrendQuanActivity;
import cn.yingsuo.im.ui.adapter.viewholder.CircleViewHolder;
import cn.yingsuo.im.ui.adapter.viewholder.ImageViewHolder;
import cn.yingsuo.im.ui.adapter.viewholder.URLViewHolder;
import cn.yingsuo.im.ui.widget.ExpandTextView;
import cn.yingsuo.im.ui.widget.MultiImageView;
import cn.yingsuo.im.ui.widget.PraiseListView;
import cn.yingsuo.im.ui.widget.SnsPopupWindow;
import cn.yingsuo.im.utils.DisplayUtils;
import cn.yingsuo.im.utils.UrlUtils;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.utils.TimeUtils;

/**
 * Created by zhangfenfen on 2018/1/16.
 */

public class FrendQuanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FrendQuanEntity> list;
    private Context context;
    private OnMorePopuItemClickListener onMorePopuItemClickListener;

    public void setOnMorePopuItemClickListener(OnMorePopuItemClickListener onMorePopuItemClickListener) {
        this.onMorePopuItemClickListener = onMorePopuItemClickListener;
    }

    public FrendQuanAdapter(Context context) {
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
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == FrendQuanEntity.TYPE_TOP) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_frend_quan_head, parent, false);
            viewHolder = new FrendQuanHeadHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_frend_quan_item, parent, false);

            if (viewType == CircleViewHolder.TYPE_URL) {
                viewHolder = new URLViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_IMAGE) {
                viewHolder = new ImageViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_VIDEO) {
                //viewHolder = new VideoViewHolder(view);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FrendQuanHeadHolder) {
            FrendQuanHeadHolder frendQuanHeadHolder = (FrendQuanHeadHolder) holder;
            frendQuanHeadHolder.bindView();
        } else {
            final CircleViewHolder viewHolder = (CircleViewHolder) holder;
            final FrendQuanEntity frendQuanEntity = list.get(position);
            //头像
            ImageLoader.getInstance().displayImage(SealAction.BASE_URL + frendQuanEntity.getHead_img(), viewHolder.headIv, App.getOptions());
            //昵称
            viewHolder.nameTv.setText(frendQuanEntity.getNi_name());
            //时间
            viewHolder.timeTv.setText(TimeUtils.formatTime(Long.parseLong(frendQuanEntity.getAdd_time()) * 1000));
            //内容
            String content = frendQuanEntity.getContent();
            if (!TextUtils.isEmpty(content)) {
                viewHolder.contentTv.setExpand(frendQuanEntity.isExpand());
                viewHolder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                    @Override
                    public void statusChange(boolean isExpand) {
                        frendQuanEntity.setExpand(isExpand);
                    }
                });

                viewHolder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            viewHolder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
            //点赞及评论弹出框
            final SnsPopupWindow snsPopupWindow = new SnsPopupWindow(context);
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new FrendSnsPopuOnItemClickLinstener(frendQuanEntity, position));
            viewHolder.snsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snsPopupWindow.showPopupWindow(v);
                }
            });
            //点赞及评论
            final List<FrendZanEntity> zanList = frendQuanEntity.getZan();
            boolean hasFavort = frendQuanEntity.hasFavort();
            boolean hasComment = frendQuanEntity.hasComment();
            viewHolder.digLine.setVisibility(hasComment && hasFavort ? View.VISIBLE : View.GONE);
            if (hasComment || hasFavort) {
                if (hasFavort) {//处理点赞列表
                    viewHolder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            String userName = zanList.get(position).getName();
                            String userId = zanList.get(position).getId();
                        }
                    });
                    viewHolder.praiseListView.setDatas(zanList);
                    viewHolder.praiseListView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.praiseListView.setVisibility(View.GONE);
                }
                viewHolder.digCommentBody.setVisibility(View.VISIBLE);
            } else {
                viewHolder.digCommentBody.setVisibility(View.GONE);
            }
            viewHolder.urlTipTv.setVisibility(View.GONE);
            switch (viewHolder.viewType) {
                case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if (holder instanceof URLViewHolder) {
                      /*  String linkImg = frendQuanEntity.getLinkImg();
                        String linkTitle = frendQuanEntity.getLinkTitle();
                        Glide.with(context).load(linkImg).into(((URLViewHolder)holder).urlImageIv);
                        ((URLViewHolder)holder).urlContentTv.setText(linkTitle);
                        ((URLViewHolder)holder).urlBody.setVisibility(View.VISIBLE);
                        ((URLViewHolder)holder).urlTipTv.setVisibility(View.VISIBLE);*/
                    }

                    break;
                case CircleViewHolder.TYPE_IMAGE:// 处理图片
                    if (holder instanceof ImageViewHolder) {
                        final List<PhotoInfo> photos = new ArrayList<>();
                        for (String str : frendQuanEntity.getImg()) {
                            photos.add(new PhotoInfo(SealAction.BASE_URL + str, 0, 0));
                        }
                        if (photos != null && photos.size() > 0) {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).multiImageView.setList(photos);
                            ((ImageViewHolder) holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //imagesize是作为loading时的图片size
                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                    List<String> photoUrls = new ArrayList<String>();
                                    for (PhotoInfo photoInfo : photos) {
                                        photoUrls.add(photoInfo.url);
                                    }
                                    ImagePagerActivity.startImagePagerActivity(((FrendQuanActivity) context), photoUrls, position, imageSize);


                                }
                            });
                        } else {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.GONE);
                        }
                    }

                    break;
                case CircleViewHolder.TYPE_VIDEO:
                   /* if(holder instanceof VideoViewHolder){
                        ((VideoViewHolder)holder).videoView.setVideoUrl(circleItem.getVideoUrl());
                        ((VideoViewHolder)holder).videoView.setVideoImgUrl(circleItem.getVideoImgUrl());//视频封面图片
                        ((VideoViewHolder)holder).videoView.setPostion(position);
                        ((VideoViewHolder)holder).videoView.setOnPlayClickListener(new CircleVideoView.OnPlayClickListener() {
                            @Override
                            public void onPlayClick(int pos) {
                                curPlayIndex = pos;
                            }
                        });
                    }*/

                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FrendQuanEntity.TYPE_TOP;
        } else {
            int itemType = 0;
            FrendQuanEntity item = (FrendQuanEntity) list.get(position);
            if (FrendQuanEntity.TYPE_URL.equals(item.getType())) {
                itemType = CircleViewHolder.TYPE_URL;
            } else if (FrendQuanEntity.TYPE_IMG.equals(item.getType())) {
                itemType = CircleViewHolder.TYPE_IMAGE;
            } else if (FrendQuanEntity.TYPE_VIDEO.equals(item.getType())) {
                itemType = CircleViewHolder.TYPE_VIDEO;
            }
            return itemType;
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private class FrendQuanHeadHolder extends RecyclerView.ViewHolder {

        public FrendQuanHeadHolder(View itemView) {
            super(itemView);
        }

        public void bindView() {
            CircleImageView iconView = (CircleImageView) itemView.findViewById(R.id.frend_quan_icon);
            iconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, MyFrendQuanActivity.class));
                }
            });
            TextView slogon = (TextView) itemView.findViewById(R.id.frend_quan_slogon);
            UserInfoResponse userInfoResponse = App.getUserInfoResponse();
            String portraitUri = userInfoResponse.getImg();
            ImageLoader.getInstance().displayImage(SealAction.BASE_URL + portraitUri, iconView, App.getOptions());
            slogon.setText(userInfoResponse.getSlogon());
        }
    }

    /**
     * 弹出点赞和评论框
     */
    private class FrendSnsPopuOnItemClickLinstener implements SnsPopupWindow.OnItemClickListener {
        private FrendQuanEntity entity;
        private int frendPosition;

        public FrendSnsPopuOnItemClickLinstener(FrendQuanEntity entity, int frendPosition) {
            this.entity = entity;
            this.frendPosition = frendPosition;
        }

        @Override
        public void onItemClick(ActionItem item, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (null != onMorePopuItemClickListener) {
                        onMorePopuItemClickListener.onClickZan(entity);
                    }
                    break;
                case 1://发布评论
                    CommentConfig config = new CommentConfig();
                    config.circlePosition = frendPosition - 1;
                    config.commentType = CommentConfig.Type.PUBLIC;
                    if (null != onMorePopuItemClickListener) {
                        onMorePopuItemClickListener.onCickPingLun(entity, "", frendPosition, config);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void notifyItemZan(FrendQuanEntity frendQuanEntity, FrendZanEntity zanEntity) {
        frendQuanEntity.getZan().add(zanEntity);
        int position = list.indexOf(frendQuanEntity);
        if (position > 0 && position < list.size()) {
            notifyItemChanged(position);
        }
    }


    public interface OnMorePopuItemClickListener {
        void onClickZan(FrendQuanEntity entity);

        void onCickPingLun(FrendQuanEntity entity, String hId, int position, CommentConfig commentConfig);
    }
}
