package com.gomeplus.comx.source.sourcebase;

import com.gomeplus.comx.context.Context;
import com.gomeplus.comx.schema.TinyTemplate;
import com.gomeplus.comx.source.Source;
import com.gomeplus.comx.source.SourceException;
import com.gomeplus.comx.utils.config.Config;
import com.gomeplus.comx.utils.config.ConfigException;
import com.gomeplus.comx.utils.rest.RequestMessage;
import com.gomeplus.comx.utils.rest.ReservedParameterManager;
import com.gomeplus.comx.utils.rest.Url;
import com.gomeplus.comx.utils.rest.UrlException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xue on 12/23/16.
 */
abstract public class AbstractRequestBasedSourceBase extends AbstractSourceBase{
    public AbstractRequestBasedSourceBase(Config conf) {super(conf);}

    /**
     *  针对 requestbase 处理 url 时 将url query 部分 encode
     *  这个涉及正则，需要再次验证
     * @param context
     * @param sourceOptions
     * @param reservedVariables
     * @return Object data
     * @throws ConfigException
     * @throws IOException
     * @throws SourceException
     */
    public Object executeLoading(Context context, Config sourceOptions, HashMap reservedVariables) throws ConfigException, IOException, SourceException{
        String method                   = sourceOptions.str(Source.FIELD_METHOD, RequestMessage.METHOD_GET);
        Integer timeout                 = sourceOptions.intvalue(Source.FIELD_TIMEOUT, RequestMessage.DEFAULT_TIMEOUT);
        RequestMessage currentRequest   = context.getRequest();
        ReservedParameterManager reservedParameterManager = ReservedParameterManager.fromRequest(currentRequest);

        String targetUrl = this.getResourceUrl(context, sourceOptions.rstr(Source.FIELD_URI), reservedVariables, reservedParameterManager.getReservedQueryParams());
        context.getLogger().debug("source load remote data, method:"+ method + " targetUrl:" + targetUrl);
        Map requestData = null;
        if (method.equals(RequestMessage.METHOD_POST) || method.equals(RequestMessage.METHOD_PUT)) {
            if (!method.equals(currentRequest.getMethod())) {
                throw new UnmatchedRequestMethodException("Source loading, method unmatched: Current method is:"+ currentRequest.getMethod());
            }
            requestData = currentRequest.getData();
        }

        try {
            // Url 生成错误 处理
            Url url = new Url(targetUrl);
            RequestMessage request = new RequestMessage(url, method, requestData, reservedParameterManager.getFilteredReservedHeaders(context), timeout);
            return this.doRequest(request, context);
        } catch (SourceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SourceException(ex);
        }
    }



    private String getResourceUrl(Context context, String uri, HashMap reservedVariables, HashMap reservedQueryParams) throws ConfigException{
        String renderedUri;

        if (reservedVariables.containsKey(Source.RESERVED_RENDERED_URI)) {
            renderedUri = (String)reservedVariables.get(Source.RESERVED_RENDERED_URI);
        } else {
            TinyTemplate tpl = new TinyTemplate(uri);
            renderedUri = tpl.render(reservedVariables, context, true);
        }

        if (isFullUri(renderedUri)) {
            return renderedUri;
        }
        // TODO url form url;
        renderedUri = this.getUrlPrefix(context) + renderedUri;
        return renderedUri;
    }

    private boolean isFullUri(String renderedUri) {
        return renderedUri.startsWith("http://") || renderedUri.startsWith("https://");
    }
    abstract Object doRequest(RequestMessage request,Context context) throws SourceException;
    abstract String getUrlPrefix(Context context) throws ConfigException;




}
