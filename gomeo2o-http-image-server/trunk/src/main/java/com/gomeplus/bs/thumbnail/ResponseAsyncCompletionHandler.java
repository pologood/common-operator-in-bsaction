/*
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

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Response Handler
 * Created by jyw on 2016/1/18.
 */
final class ResponseAsyncCompletionHandler extends AsyncCompletionHandler<Response> {
    private List<ImageRequest> requests0;
    private String key;
    private boolean responded = false;
    private final static Map<String, String> suffixToMIMETypeMap = new HashMap<>();
    static {
        suffixToMIMETypeMap.put("jpg", "image/jpeg");
        suffixToMIMETypeMap.put("meta", "application/json");
    }

    ResponseAsyncCompletionHandler(String key, List<ImageRequest> requests) {
        this.key = key;
        this.requests0 = requests;
    }

    @Override
    public Response onCompleted(Response response) throws Exception {
        // Do something with the Response
        // ...
        System.out.println("finish " + Long.toString(Thread.currentThread().getId()));
        try {
            if (response.getStatusCode() >= 400)
                doErrorResponse("get image failure");

            doResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void onThrowable(Throwable t) {
        // Something wrong happened.
        System.out.println("exception " + Long.toString(Thread.currentThread().getId()));
        t.printStackTrace();
        doErrorResponse("other exception\r\n");
    }

    private void doResponse(Response response) {
        List<ImageRequest> requests = getImageRequests();

        ThumbnailGenerator generator;
        try {
            InputStream is = new ByteArrayInputStream(response.getResponseBodyAsBytes());
            generator = new ThumbnailGenerator(keepAlpha(is));
        } catch (IOException e) {
            generator = null;
        }

        Map<String, Map<String, byte[]>> destImages = new HashMap<>();
        Map<String, TargetImageGenerator> thumbnails = new HashMap<>();
        for (ImageRequest request: requests) {
            String params = request.params;
            String suffix = request.suffix;

            if (!destImages.containsKey(params)) {
                destImages.put(params, new HashMap<String, byte[]>());
                thumbnails.put(params, generator==null? null: generator.doThumbnail(params));
            }

            Map<String, byte[]> imagesSameSize = destImages.get(params);
            if (!imagesSameSize.containsKey(suffix))
                imagesSameSize.put(suffix, thumbnails.get(params).generate(suffix));
        }

        for (ImageRequest request: requests) {
            String suffix = request.suffix;
            writeResponse(request.ctx, buildResponse(destImages.get(request.params).get(suffix), getMIMEType(suffix)));
        }

        responded = true;
    }

    private FullHttpResponse buildResponse(byte[] buf, String mimeType) {

        if (buf == null)
            return buildErrorResponse("io exception\r\n");

        return buildSuccessResponse(buf, mimeType);
    }

    private List<ImageRequest> getImageRequests() {
        if (key == null)
            return requests0;

        return HttpImageServerHandler.pending_requests.remove(key);
    }

    private void doErrorResponse(String msg) {
        if (responded)
            return;

        List<ImageRequest> requests = getImageRequests();

        FullHttpResponse fullHttpResponse = buildErrorResponse(msg);

        for (ImageRequest request: requests) {
            writeResponse(request.ctx, fullHttpResponse);
        }
    }

    private FullHttpResponse buildErrorResponse(String msg) {
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        return response;
    }

    private FullHttpResponse buildSuccessResponse(byte[] buf, String mimeType) {
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(buf));
        response.headers().set(CONTENT_TYPE, mimeType);
        response.headers().set(CACHE_CONTROL, "max-age=" + Integer.toString(3600*24*90));
        return response;
    }

    private void writeResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        // Write the response.
        ctx.write(response);
        // close the connection once the content is fully written.
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    static private String getMIMEType(String suffix) {
        final String mime = suffixToMIMETypeMap.get(suffix);
        return null == mime? "image/" + suffix: mime;
    }

    private BufferedImage keepAlpha(InputStream is) throws IOException {
        BufferedImage image = ImageIO.read(is);
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
