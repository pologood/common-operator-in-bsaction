/*
 * Copyright 2012 The Netty Project
 * Copyright 2016 JiaYanwei
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.gomeplus.bs.thumbnail;

import com.gomeplus.bs.thumbnail.util.CacheUtils;
import com.gomeplus.bs.thumbnail.util.ConfigUtils;
import com.ning.http.client.AsyncHttpClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * An HTTP service that generate thumbnails of original image
 */
public final class HttpImageServer {
    public static void main(String[] args) throws Exception {

        int port = 8080;
        int workerCount = 2*Runtime.getRuntime().availableProcessors()-1;
        int bossCount = 1;

        if (args.length==0 || args[0].equals("")) {
            System.err.println("Please set config path first!");
            return;
        }

        String  configPath = args[0];

        if (args.length > 1)
            port = Integer.parseInt(args[1]);
        if (args.length > 2)
            workerCount = Integer.parseInt(args[2]);
        if (args.length > 3)
            bossCount = Integer.parseInt(args[3]);

        // 初始化配置
        ConfigUtils configUtils = new ConfigUtils(configPath);
        configUtils.init();
        
        CacheUtils cacheUtils = CacheUtils.getInstance();
        Thread timerJob = new Thread(new CrontabBootstrap(cacheUtils));
        timerJob.start();

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossCount);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerCount);
        try {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpImageServerInitializer(null, asyncHttpClient, cacheUtils));

            Channel ch = b.bind(port).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    "http://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
