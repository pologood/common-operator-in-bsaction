package com.gomeplus.modifyjsonp;

import com.ning.http.client.AsyncHttpClient;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

final class HttpModifyjsonpServerInitializer extends ChannelInitializer<SocketChannel>{
    private final SslContext sslCtx;
    private final AsyncHttpClient asyncHttpClient;

    public HttpModifyjsonpServerInitializer(SslContext sslCtx, AsyncHttpClient asyncHttpClient) {
        this.sslCtx = sslCtx;
        this.asyncHttpClient = asyncHttpClient;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpModifyjsonpServerHandler(asyncHttpClient));
    }
}
