package cn.yingsuo.im.model;

import java.io.Serializable;

import cn.yingsuo.im.server.response.UserInfoResponse;

/**
 * 
* @ClassName: FavortItem 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author yiw
* @date 2015-12-28 下午3:44:56 
*
 */
public class FavortItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private FrendZanEntity user;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FrendZanEntity getUser() {
		return user;
	}
	public void setUser(FrendZanEntity user) {
		this.user = user;
	}
	
}
