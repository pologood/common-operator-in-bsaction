package com.gomeplus.oversea.bi.service.spider.mq;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import com.google.common.base.Objects;

/**
 * 2017/2/13
 * 外站类目
 */
@Data
public class OuterCategory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1194575144451725072L;
	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 父级id
	 */
	private String pid;
	/**
	 * 名称
	 */
    private String name;
    /**
     * 类目来源
     */
    private String source;
    /**
     * 级别
     */
    private Integer level;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 是否有子类目
     */
    private Integer hasChildren;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 修改人
     */
    private String updater;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
    
    public static enum Status {
        ENABLED(1, "生效"),
        DISABLED(-1, "失效");

        private final int value;
        private final String desc;

        private Status(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public int value() {
            return value;
        }

        public static Status from(Integer number) {
            for (Status status : Status.values()) {
                if (Objects.equal(number, status.value())) {
                    return status;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return desc;
        }
    }

    /**
     * 类目是否启用
     * @param category 后台类目
     * @return 若为启用状态返回true, 反之返回false
     */
    public static Boolean isEnable(OuterCategory category){
        return Status.from(category.getStatus()) == Status.ENABLED;
    }
}
