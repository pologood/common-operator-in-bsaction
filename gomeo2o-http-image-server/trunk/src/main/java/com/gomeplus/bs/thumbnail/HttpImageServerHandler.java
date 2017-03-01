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

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gomeplus.bs.thumbnail.config.Config;
import com.gomeplus.bs.thumbnail.util.CacheUtils;
import com.ning.http.client.AsyncHttpClient;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

class ImageRequest {
    ChannelHandlerContext ctx;
    String params;
    String suffix;
    ImageRequest(ChannelHandlerContext ctx, String params, String suffix) {
        this.ctx = ctx;
        this.params = params;
        this.suffix = suffix;
    }
}

class RightSplitter {
    private String str;

    public RightSplitter(String str) {
        this.str = str;
    }

    public String[] splitOnce(char ch) {
        int lastIndexOfCh = str.lastIndexOf(ch);
        return new String[]{str.substring(0, lastIndexOfCh), str.substring(lastIndexOfCh+1)};
    }
}

@Slf4j
final class HttpImageServerHandler extends SimpleChannelInboundHandler<Object> {
    private AsyncHttpClient asyncHttpClient;

    private CacheUtils cacheUtils;

    HttpImageServerHandler(AsyncHttpClient asyncHttpClient, CacheUtils cacheUtils) {
        this.asyncHttpClient = asyncHttpClient;
        this.cacheUtils = cacheUtils;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        dealRequest(ctx);
    }

    static ConcurrentMap<String, CopyOnWriteArrayList<ImageRequest> > pending_requests =
            new ConcurrentHashMap<>();

    private String request_uri;

    private void dealRequest(ChannelHandlerContext ctx) {
        String path;
        ImageRequest request;
        try {
            String[] tmp = new RightSplitter(request_uri).splitOnce('.');
            String suffix = tmp[1];

            tmp = new RightSplitter(tmp[0]).splitOnce('.');
            path = tmp[0];
            String params = tmp[1];

            if (params == null || params.equals("")) {
                doResponse4xx(ctx, HttpResponseStatus.BAD_REQUEST);
                log.info("params is empty ---" + params);
                return;
            }

            // 如果参数不在白名单里，则返回401错误
            if (!cacheUtils.getCache(params)) {
            	doResponse4xx(ctx, HttpResponseStatus.UNAUTHORIZED);
            	log.info("params not in the whitelist---" + params + HttpResponseStatus.UNAUTHORIZED);
                return;
            }
            log.info("request_uri---" + request_uri);
            request = new ImageRequest(ctx, params, suffix);
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            log.warn("bad request, reason ", e);
            doResponse4xx(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        String url = Config.URL_BASE + path;
        CopyOnWriteArrayList<ImageRequest> requests = new CopyOnWriteArrayList<>();
        requests.add(request);

        if (pending_requests.putIfAbsent(path, requests) == null) {
            asyncHttpClient.prepareGet(url).execute(new ResponseAsyncCompletionHandler(path, null));
            return;
        }

        try {
            pending_requests.get(path).add(request);
        }
        catch (NullPointerException e) {
            asyncHttpClient.prepareGet(url).execute(new ResponseAsyncCompletionHandler(null, requests));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            request_uri = request.getUri();

            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
        }

        if (msg instanceof LastHttpContent) {
            LastHttpContent trailer = (LastHttpContent) msg;

            if (!trailer.getDecoderResult().isSuccess()) {
                doResponse4xx(ctx, HttpResponseStatus.BAD_REQUEST);
            }
        }
    }

    private void doResponse4xx(ChannelHandlerContext ctx, HttpResponseStatus status) {
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status,
                Unpooled.copiedBuffer(status.reasonPhrase(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Write the response.
        ctx.write(response);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
