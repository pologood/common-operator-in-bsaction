package com.gomeplus.bs.interfaces.lolengine.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 新好友关系实体 
 * @author mojianli
 * @date 2016年11月22日 上午11:13:50
 */
public class FriendshipRelation implements Serializable{
	
    private static final long serialVersionUID = -2133631508248894550L;
    
    private Long id;
	private Long userId;   //会员id
	private Long newUserId;   //会员id
	private Long friendUserId;   //我的好友的会员id
	private Long newFriendUserId;   //我的好友的会员id
	private Integer status;   //好友状态，0：正常，1：已被对方单向删除
	private Date createTime;   //创建时间
	private Date updateTime;   //修改时间
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getFriendUserId() {
		return friendUserId;
	}
	public void setFriendUserId(Long friendUserId) {
		this.friendUserId = friendUserId;
	}
	
	
	@Override
	public String toString() {
		return "FriendshipRelation [id=" + id + ", userId=" + userId
				+ ", friendUserId=" + friendUserId + ", status=" + status
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
    public Long getNewUserId() {
        return newUserId;
    }
    public void setNewUserId(Long newUserId) {
        this.newUserId = newUserId;
    }
    public Long getNewFriendUserId() {
        return newFriendUserId;
    }
    public void setNewFriendUserId(Long newFriendUserId) {
        this.newFriendUserId = newFriendUserId;
    }
}
