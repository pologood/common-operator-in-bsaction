package com.gomeplus.bs.service.lol.event.common;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description id事件抽象类 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:54:27
 * @param <T>
 */
public abstract class AbstractIdEvent<T> implements IdEvent {

    @Getter
    @Setter
    private T data;

    private String id;

    @Override
    public final String id() {
        return id;
    }

    public AbstractIdEvent(String id) {
        this.id = id;
    }
}
