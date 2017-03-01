package com.gomeplus.modifyjsonp;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.gomeplus.modifyjsonp.config.ConfigUtil;
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


class JsonpRequest {
    ChannelHandlerContext ctx;
    JsonpRequest(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}

@Slf4j
final class HttpModifyjsonpServerHandler extends SimpleChannelInboundHandler<Object>{
	private AsyncHttpClient asyncHttpClient;
	private String request_uri;

	HttpModifyjsonpServerHandler(AsyncHttpClient asyncHttpClient) {
	    this.asyncHttpClient = asyncHttpClient;
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        dealPrimaryUrl(ctx);
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
    
    private void dealPrimaryUrl(ChannelHandlerContext ctx) {
		// TODO 转换url-->请求新的url->改变其中的 内容 (用request_uri)
    	//https://s-pre.meixincdn.com/g/html/prodhtml/productDesc/descHtml/201611/category/prod21500050.html
    	// http://desc.atguat.com.cn/html/prodhtml/productDesc/descHtml/201611/category/prod21500050.html 
    	
    	//https://s-pre.meixincdn.com/g/
    	//http://desc.atguat.com.cn/
    	
        // 从配置获取 meixincdn 域名以及路径"http://desc.atguat.com.cn"
        String originUrlPrefix = ConfigUtil.getProperty("ONLINE_URLPREFIX");
        //取得http://desc.atguat.com.cn/html... (去掉/g)
        String newUrl;
        //判断uri以/g开头
        if(request_uri.startsWith("/g")){
        	newUrl = originUrlPrefix + this.request_uri.substring(2);
        	JsonpRequest request = new JsonpRequest(ctx);
        	log.info("request_uri---" + request_uri);
        	asyncHttpClient.prepareGet(newUrl).execute(new ResponseAsyncCompletionHandler(request));
        }else{
        	doResponse4xx(ctx, HttpResponseStatus.BAD_REQUEST);
        }
    	
	}


    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }
    private void doResponse4xx(ChannelHandlerContext ctx, HttpResponseStatus status) {
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status,
                Unpooled.copiedBuffer(status.reasonPhrase(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        // Write the response.
        ctx.write(response);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}//end of class
