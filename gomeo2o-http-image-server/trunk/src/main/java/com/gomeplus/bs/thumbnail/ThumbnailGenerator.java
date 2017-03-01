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

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ThumbnailParams {
    static final byte DEFAULT_QUALITY = 90;

	private ThumbnailParams() {}
    private int w = 0;
    private int h = 0;
    private Positions c = null;
    private boolean z = false;
    private byte q = DEFAULT_QUALITY;

    int width() { return w; }
    int height() { return h; }
    Positions crop() { return c; }
    boolean zoomBigger() { return z; }
    byte quality() { return q; }

    final static class Builder {
        final private ThumbnailParams params = new ThumbnailParams();
        ThumbnailParams build() { return params; }
        Builder width(int w) { params.w = w; return this; }
        Builder height(int h) { params.h = h; return this; }
        Builder crop(Positions c) { params.c = c; return this; }
        Builder zoomBigger(boolean z) { params.z = z; return this; }
        Builder quality(byte q) { params.q = q; return this; }
    }
}

class TargetImageGenerator {
    private BufferedImage image;
    private float quality;
    TargetImageGenerator(BufferedImage image, byte quality) {
        this.image = image;
        this.quality = quality/100.0f;
    }

    private static Set<String> noAlphaFormats = new HashSet<>();
    static {
        noAlphaFormats.add("bmp");
        noAlphaFormats.add("jpg");
        noAlphaFormats.add("jpeg");
    }

    byte[] generate(String format) {
        if (format.equals("meta")) {
            String meta = String.format("{ \"width\": %d,  \"height\": %d }", image.getWidth(), image.getHeight());
            return meta.getBytes(StandardCharsets.UTF_8);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            BufferedImage newImage = image;
            final int width = image.getWidth();
            final int height = image.getHeight();

            if (noAlphaFormats.contains(format.toLowerCase())) {
                newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g = newImage.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                g.drawImage(image, 0, 0, null);
                g.dispose();
            }

            Thumbnails.of(newImage)
                    .size(width, height)
                    .outputQuality(quality)
                    .outputFormat(format)
                    .toOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }
}

/**
 * Created by jyw on 2016/1/27.
 * Thumbnail Generator
 */
class ThumbnailGenerator {
    private BufferedImage image;

    ThumbnailGenerator (BufferedImage image) {
        this.image = image;
    }

    TargetImageGenerator doThumbnail(String params) {
        if (params.equals("-"))
            return new TargetImageGenerator(image, ThumbnailParams.DEFAULT_QUALITY);

        ThumbnailParams tbParams = parseThumbnailParams(params);
        return new TargetImageGenerator(doThumbnail(tbParams), tbParams.quality());
    }

    private BufferedImage doThumbnail(ThumbnailParams tbParams) {
        try {
            if (!tbParams.zoomBigger() && (image.getWidth() <= tbParams.width() && image.getHeight() <= tbParams.height()))
                return image;
            
            if (!tbParams.zoomBigger() && (tbParams.height()==0 && image.getWidth() <= tbParams.width()))
            	return image;
            
            if(!tbParams.zoomBigger() && (tbParams.width()==0 && image.getHeight() <= tbParams.height()))
            	return image;
        	
        	if (tbParams.crop()!=null)
        		return Thumbnails.of(image).size(tbParams.width(), tbParams.height()).crop(tbParams.crop()).asBufferedImage();
            
            if(tbParams.width()==0)
            	return Thumbnails.of(image).height(tbParams.height()).asBufferedImage();
            
            if(tbParams.height()==0)
            	return Thumbnails.of(image).width(tbParams.width()).asBufferedImage();

            return Thumbnails.of(image).size(tbParams.width(), tbParams.height()).asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ThumbnailParams parseThumbnailParams(String params) {
        String regex="(\\d+)?(?:x(\\d+))?(c([TB]?[LR]?))?(z)?(?:q(\\d+))?";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(params);
	    int w = 0, h = 0;
        boolean z = false;
        Positions c = null;
        byte q = ThumbnailParams.DEFAULT_QUALITY;
        if(matcher.matches()){
        	w = matcher.group(1)==null? 0: Integer.parseInt(matcher.group(1));
        	h = matcher.group(2)==null? 0: Integer.parseInt(matcher.group(2));
            c = matcher.group(3)==null? null: parsePositions(matcher.group(4));
        	z = matcher.group(5)!=null;
            q = matcher.group(6)==null? q: (byte)Integer.parseInt(matcher.group(6));
        }
        return new ThumbnailParams.Builder().width(w).height(h).crop(c).zoomBigger(z).quality(q).build();
    }

    private final static class ParamToPositions {
        private Map<String, Positions> paramToPositions = new HashMap<>();
        ParamToPositions() {
            paramToPositions.put("TL", Positions.TOP_LEFT);
            paramToPositions.put("T",  Positions.TOP_CENTER);
            paramToPositions.put("TR", Positions.TOP_RIGHT);
            paramToPositions.put("L",  Positions.CENTER_LEFT);
            paramToPositions.put("",   Positions.CENTER);
            paramToPositions.put("R",  Positions.CENTER_RIGHT);
            paramToPositions.put("BL", Positions.BOTTOM_LEFT);
            paramToPositions.put("B",  Positions.BOTTOM_CENTER);
            paramToPositions.put("BR", Positions.BOTTOM_RIGHT);
        }
        Positions parse(String param) {
            return paramToPositions.get(param);
        }
    }
    private final static ParamToPositions paramToPositions = new ParamToPositions();

    private Positions parsePositions(String param) {
        return paramToPositions.parse(param);
    }
}
