package com.gomeplus.oversea.bi.service.item.entity;

import com.google.common.base.Objects;

/**
 * 2017/2/13
 * 后台类目
 */
public class Category extends BaseCategory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8578323430861017060L;
    
    public static enum Status {
        ENABLED(1, "启用"),
        DISABLED(-1, "禁用");

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
    
    public static enum LevelData {
		// 一级类目
		LEVELONE(0),
		// 二级类目
		LEVELTWO(1),
		// 三级类目
		LEVELTHREE(2),
		// 四级类目
		LEVELFOUR(3),
		//固定空值
		LEVELNULL(4);
		
		private Integer flag;
		private LevelData(Integer flag) {
			this.flag = flag;
		}
		public Integer getFlag() {
			return flag;
		}
	    public static LevelData from(Integer number) {
            for (LevelData levelData : LevelData.values()) {
                if (levelData.flag.equals(number)) {
                    return levelData;
                }
                if(number == 4) {
                	return LevelData.LEVELNULL;
                }
            }
            return null;
        }
	}

    /**
     * 类目是否启用
     * @param category 后台类目
     * @return 若为启用状态返回true, 反之返回false
     */
    public static boolean isEnable(Category category){
        return Status.from(category.getStatus()) == Status.ENABLED;
    }
}
