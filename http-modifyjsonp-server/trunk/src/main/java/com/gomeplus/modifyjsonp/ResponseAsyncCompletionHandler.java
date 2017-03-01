package com.gomeplus.modifyjsonp;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gomeplus.modifyjsonp.config.Config;
import com.gomeplus.modifyjsonp.config.ConfigUtil;
import com.gomeplus.venus.common.imageutil.*;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseAsyncCompletionHandler extends AsyncCompletionHandler<Response> {
	private JsonpRequest request;
    ResponseAsyncCompletionHandler(JsonpRequest request) {
        this.request = request;
    }

    static final String CONS_PRE = "pre";
    static final String CONS_I_PRE = "i-pre";
    static final String PRE_PREFIX = "https://i-pre.meixincdn.com";
    static final String PRO_PREFIX = "https://i0.meixincdn.com";
    static final String newGomeImgHost = "meixincdn.com";
/**
 *                 
                1, 案例  pro
                        http://img5.gomein.net.cn/image/prodimg/productDesc/descImg/201505/desc1417/1122470422/1_05.jpg
                to:     https://iX.meixincdn.com/gi/prodimg/productDesc/descImg/201505/desc1417/1122470422/1_05.jpg
案例  pre
                        http://img2.atguat.net.cn/image/bbcimg/production_image/nimg/20160614/14/8001019700/13061814_800.jpg
                to:     https://i-pre.meixincdn.com/gi/bbcimg/production_image/nimg/20160614/14/8001019700/13061814_800.jpg
                2, 案例  pro
                        http://gfs10.gomein.net.cn/T1iECTBm_v1RCvBVdK
                to:     https://i3.meixincdn.com/g/T1iECTBm_v1RCvBVdK
案例  pre
                        http://gfs2.atguat.net.cn/T1ca_TByhT1RCvBVdK
                to:     http://i-pre.meixincdn.com/g/T1ca_TByhT1RCvBVdK
 */

	@Override
	public Response onCompleted(Response response) throws Exception {
		// 修改在线请求返回的内容
        try {
            String line                     = response.getResponseBody();
            String newHtml                  = JsonpImgConverter.convert(line);

            FullHttpResponse finalResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(newHtml.getBytes()));
            writeResponse(request.ctx, finalResponse);
        } catch (IOException e) {
            log.info("exception " + Long.toString(Thread.currentThread().getId()));
            e.printStackTrace();
            doErrorResponse("get responseBody exception!\r\n");
        }
		return response;
	}


    @Override
    public void onThrowable(Throwable t) {
        // Something wrong happened.
        log.info("exception " + Long.toString(Thread.currentThread().getId()));
        t.printStackTrace();
        doErrorResponse("other exception\r\n");
    }
	
	private void writeResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
		response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        // Write the response.
        ctx.write(response);
        // close the connection once the content is fully written.
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    private FullHttpResponse buildErrorResponse(String msg) {
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }

    private void doErrorResponse(String msg) {
        //if (responded) return;
        FullHttpResponse fullHttpResponse = buildErrorResponse(msg);
        writeResponse(request.ctx, fullHttpResponse);
    }
}
