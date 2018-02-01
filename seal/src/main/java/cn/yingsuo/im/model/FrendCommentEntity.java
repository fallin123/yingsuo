package cn.yingsuo.im.model;

import cn.yingsuo.im.server.response.UserInfoResponse;

/**
 * Created by zhangfenfen on 2018/1/26.
 */

public class FrendCommentEntity {
    private String id;
    private UserInfoResponse user;
    private UserInfoResponse toReplyUser;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }

    public UserInfoResponse getToReplyUser() {
        return toReplyUser;
    }

    public void setToReplyUser(UserInfoResponse toReplyUser) {
        this.toReplyUser = toReplyUser;
    }
}
