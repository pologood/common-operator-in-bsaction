package com.gomeplus.oversea.bs.service.gateway.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by neowyp on 2016/7/9.
 * Author   : wangyunpeng
 * Date     : 2016/7/9
 * Time     : 9:08
 * Version  : V1.0
 * Desc     : 事件分发器，对Guava的EventBus进行封装
 */
@Component
@Slf4j
public class EventDispatcher {
    @Autowired
    private ApplicationContext ctx;
    protected final EventBus eventBus;
    public EventDispatcher() {
        this(Integer.valueOf(Runtime.getRuntime().availableProcessors() + 1));
    }

    public EventDispatcher(Integer threadCount) {
        this.eventBus = new AsyncEventBus(Executors.newFixedThreadPool(threadCount.intValue()));
    }

    @PostConstruct
    public void registerListeners() {
        Map listeners = ctx.getBeansOfType(EventListener.class);
        Iterator i$ = listeners.values().iterator();

        while(i$.hasNext()) {
            EventListener eventListener = (EventListener)i$.next();
            this.eventBus.register(eventListener);
        }

    }

    /**
     * 发布事件
     * @param event
     */
    public void publish(BaseEvent event) {
        log.info("publish an event({})", event);
        this.eventBus.post(event);
    }
}
