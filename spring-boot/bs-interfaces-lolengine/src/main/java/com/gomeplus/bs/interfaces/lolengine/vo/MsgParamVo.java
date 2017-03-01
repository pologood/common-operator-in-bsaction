package com.gomeplus.bs.interfaces.lolengine.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @Description 发送消息提醒提供的vo类型
 * @author mojianli
 * @date 2016年12月19日 下午2:35:02
 */
@Data
public class MsgParamVo implements Serializable {

    private static final long serialVersionUID = 8774493441565926461L;

    // 消息类型。0：文本消息；1：透传消息
    private int msgType = 0;
    // 发送人id
    private String sendUserId;
    // 发送人id
    private String sendUserName = "default";
    // 接收用户id或者接收群组id
    private String receiveUserId;
    // 接收用户角色接收用户角色0：个人；1：群组
    private int receiveUserRole;
    // 接收用户或者圈子名称
    private String receiveUserName;
    // 发送人是否接收消息，一对一单聊必传参数，默认为false
    private boolean senderReceiveMsg = false;
    // 发送人是否为系统帐号，默认为true
    private boolean sendBySysAccount = true;
    // 非必填（该消息客户端是否不计数 1：不计数 0：计数（默认0，计数））
    private int isNotCount;
    // 非必填（该消息客户端是否隐藏 1：隐藏 0：显示 （默认0，显示））
    private int isHide;
    // 非必填（当isHide参数为1时，即想要隐藏该消息。但某些用户需要显示该消息，需要显示该消息的用户id列表）
    private List<Long> receiveUids;
    // 消息的内容
    private String sendMessage;
    // 扩展信息，存相应的业务信息，与手机端商定
    private Map<String, Object> extInfoMap;

}
