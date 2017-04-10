package com.gomeplus.oversea.bs.service.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gomeplus.oversea.bs.service.gateway.filter.AccessFilter;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shangshengfang on 2017/2/28.
 */
@Slf4j
public  class SwaggerUtil {
    public static JsonNode swagger = null;
    public static void loadSwagger(){
        try {
            ClassPathResource resource=new ClassPathResource("swagger.json");
            if(resource!=null) {
                InputStream inputStream = resource.getInputStream();
                if (inputStream != null) {
                    String in = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
                    if (in != null) {
                        in = in.replaceAll("\r\n", "");
                        ObjectMapper errorMapper = new ObjectMapper();
                        errorMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        JsonNode jsonNode = errorMapper.readTree(in);
                        SwaggerUtil.swagger = jsonNode;
                    }
                } else {
                    log.info("ClassPathResource(\"swagger.json\")  is  null");
                }
            }

            if(SwaggerUtil.swagger==null){
                log.info(" swagger file read fail，swagger Object is null ");
            }
        }catch(Exception e){
            log.error("swaggerFileLoadError,{}",e);
        }
    }
    public static JsonNode getSwagger(){
        if(swagger==null){
            loadSwagger();
        }
        return swagger;
    }
    public static JsonNode getPubParameters(String path){
        String [] paths={path};
        if(path.contains("/")){
            paths=path.split("/");
        }
        int start=0;
        if("".equals(paths[0]) || "#".equals(paths[0])){
            start=1;
        }
        JsonNode temp=swagger.get(paths[start]);
        if(temp!=null){
            for(int i=start+1;i<paths.length;i++){
                if(temp!=null){
                    temp=temp.get(paths[i]);
                }else{
                    return null;
                }
            }
        }else{
            return null;
        }
        return temp;
    }

    /**
     * 固定的，作为校验用的schema中可用的属性
     * @return
     */
    public  static List validateArgs(){
        List<String> l=new ArrayList();
        l.add("type");
        l.add("format");
        l.add("allowEmptyValue");
        l.add("items");
        l.add("collectionFormat");
        l.add("default");
        l.add("maximum");
        l.add("exclusiveMaximum");
        l.add("minimum");
        l.add("exclusiveMinimum");
        l.add("maxLength");
        l.add("minLength");
        l.add("pattern");
        l.add("maxItems");
        l.add("minItems");
        l.add("uniqueItems");
        l.add("enum");
        l.add("multipleOf");
        return l;
    }
}
