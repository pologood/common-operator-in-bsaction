package com.gomeplus.bs.image_uploader.mongo;

import lombok.Data;
import lombok.ToString;

/**
 * Created by wangchangye on 2017/2/13.
 *
 * @Author wangchangye
 */

public class ImageInfoAudit {
    private String url;
    private int width;
    private int height;
    //默认不需要审核
    private Boolean needAuit = false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Boolean getNeedAuit() {
        return needAuit;
    }

    public void setNeedAuit(Boolean needAuit) {
        this.needAuit = needAuit;
    }

    @Override
    public String toString() {
        return "ImageInfoAudit{" +
                "url='" + url + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", needAuit=" + needAuit +
                '}';
    }
}
