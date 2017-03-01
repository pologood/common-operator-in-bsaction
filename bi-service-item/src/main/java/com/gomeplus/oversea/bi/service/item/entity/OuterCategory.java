package com.gomeplus.oversea.bi.service.item.entity;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Objects;

/**
 * 2017/2/13
 * 外站类目
 */
public class OuterCategory extends BaseCategory{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3379430389341840290L;
	
	/**
     * 类目来源
     */
	@Getter @Setter
    private String source;
    
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
     * 类目是否有效
     * @param outerCategory 外部类目
     * @return 
     */
    public static boolean isEnable(OuterCategory outerCategory){
        return Status.from(outerCategory.getStatus()) == Status.ENABLED;
    }
}
