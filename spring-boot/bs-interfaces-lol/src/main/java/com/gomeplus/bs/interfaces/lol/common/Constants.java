package com.gomeplus.bs.interfaces.lol.common;

/**
 * @Description 常量
 * @author yanyuyu
 * @date 2016年12月16日 上午10:11:02
 */
public class Constants {

    /**
     * @Description mongo collection name constants
     * @author yanyuyu
     * @date 2016年12月16日 上午10:38:25
     */
    public static class CollectionName {
        /**
         * 发布信息
         */
        public static final String PUBLISH_ENTRY = "publishEntry";

        /**
         * 评论
         */
        public static final String PUBLISH_REPLY = "publishReply";

        /**
         * 时间线
         */
        public static final String TIME_LINE = "timeLine";

        /**
         * 点赞
         */
        public static final String PRAISE = "praise";

        /**
         * 动态审核日志
         */
        public static final String AUDIT_CONTENT_LOG = "auditContentLog";

        /**
         * 评论审核日志
         */
        public static final String AUDIT_REPLY_LOG = "auditReplyLog";
    }

    /**
     * @Description rabbitmq ruting key
     * @author yanyuyu
     * @date 2016年12月16日 下午6:12:44
     */
    public static class RoutingKey {
        
        /**
         * 动态
         */
        public static final String PUBLISH_ENTRY = "lol.entry";

        /**
         * 评论
         */
        public static final String PUBLISH_REPLY = "lol.reply";

        /**
         * 点赞
         */
        public static final String PRAISE = "lol.praise";
        
        /**
         * 好友动态红点提醒im消息
         */
        public static final String IM_REDTIP_MESSAGE = "lolRedtip.message";
        
        /**
         * 好友动态回复（点赞、评论）im消息
         */
        public static final String IM_REPLY_MESSAGE = "lolReply.message";
    }

    /**
     * @Description 动作定义
     * @author mojianli
     * @date 2016年12月22日 下午2:55:37
     */
    public static class Action {
        
        /**
         * 新增
         */
        public static final String ACTION_CREATE = "CREATE";

        /**
         * 更新
         */
        public static final String ACTION_UPDATE = "UPDATE";

        /**
         * 删除
         */
        public static final String ACTION_DELETE = "DELETE";
    }
    
    /**
     * @Description im系统帐号定义
     * @author mojianli
     * @date 2016年12月22日 下午2:55:37
     */
    public static class ImSysAccount {
        
        /**
         * 好友动态红点提示im系统帐号
         */
        public static final String LOL_REDTIP_NOTIFICATION = "lolRedtipNotification";
        
        /**
         * 好友动态回复提示im系统帐号（点赞、评论）
         */
        public static final String LOL_REPLY_NOTIFICATION = "lolReplyNotification";
        
        /**
         * 好友动态审核系统通知帐号
         */
        public static final String LOL_SYSTEM_NOTIFICATION = "systemNotification";

    }

    public static class RedisKey {

        public static final String KEY_PROJECT = "lol";

        public static final String KEY_BUSINESS_CONTENT = "content";

        public static final String KEY_BUSINESS_FRIENDSHIP = "friendship";

        public static final String KEY_EVENT_REPLYNUM = "replynum";

        public static final String KEY_EVENT_PRAISENUM = "praisenum";

        // 点赞列表
        public static final String KEY_EVENT_PRAISESET = "praises";

        public static final String KEY_EVENT_FRIENDS = "friends";

        /**
         * 动态
         */
        public static String contentReplyNum(String entryId) {
            return KEY_PROJECT + ":" + KEY_BUSINESS_CONTENT + ":" + entryId + ":" + KEY_EVENT_REPLYNUM;
        }

        /**
         * 点赞
         */
        public static String contentPraiseNum(String entryId) {
            return KEY_PROJECT + ":" + KEY_BUSINESS_CONTENT + ":" + entryId + ":" + KEY_EVENT_PRAISENUM;
        }

        /**
         * 好友
         */
        public static String friends(Long userId) {
            return KEY_PROJECT + ":" + KEY_BUSINESS_FRIENDSHIP + ":" + userId;
        }

        /**
         * 动态下的点赞列表
         */
        public static String praiseSet(String entryId) {
            return KEY_PROJECT + ":" + KEY_BUSINESS_CONTENT + ":" + entryId + ":" + KEY_EVENT_PRAISESET;
        }
    }

    /**
     * @Description 社交审核顺序配置的 KEY true为先发后审，false为先审后发
     * @author yanyuyu
     * @date 2016年12月16日 上午10:38:25
     */
    public static final String SOCIAL_AUDIT_PUBFIRST = "social.audit.pubFirst";

}
