package cn.com.mx.webapi.common.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import cn.com.mx.webapi.common.exceptions.code.C500Exception;

/**
 * Created by neowyp on 2016/4/7. update by zhaozhou
 */
public class ValidateJsonUtil {
	
    private static final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    private static final Map<String, JsonSchema> schemas = new HashMap<String, JsonSchema>();

    /**
     * 根据module，resource，method获取对应的json schema
     *
     * @param module 模块名
     * @param resource 资源名
     * @param method 请求方法（get,post,put,delete）
     * @return 返回json schema，用于校验
     * @throws Exception
     */
    private static JsonSchema getJsonSchema(String module, String resource, String method) throws Exception {
        String path = "/validate/json/" + module + "/" + resource + "_" + method + ".json";
        if (schemas.containsKey(path)) {  //检测内存中存在，直接返回，不用每次加载。
            return schemas.get(path);
        } else {
            JsonNode jsonSchema = JsonLoader.fromResource(path);
            JsonSchema schema = factory.getJsonSchema(jsonSchema);
            schemas.put(path, schema);
            return schema;
        }
    }
    private static JsonSchema getResponseJsonSchema(String module, String resource, String method) throws Exception {
         String path = "/validate/responsejson/" + module + "/" + resource + "_" + method + ".json";
        if (schemas.containsKey(path)) {  //检测内存中存在，直接返回，不用每次加载。
            return schemas.get(path);
        } else {
            JsonNode jsonSchema = JsonLoader.fromResource(path);
            JsonSchema schema = factory.getJsonSchema(jsonSchema);
            schemas.put(path, schema);
            return schema;
        }
    }
    /**
     * @Description json校验工具类
     * @author update by zhaozhou
     * @date 2016年5月12日 下午3:08:52
     * @param module 模块名
     * @param resource 资源名
     * @param method 请求方法（get,post,put,delete）
     * @param jsonStr json串
     * @param type 如果是request表示请求时候进行jsonschema校验,response时进行返回值的jsonschema校验
     * @throws Exception
     */
    public static void validateJsonString(String module, String resource, String method, String jsonStr) throws Exception {
		JsonSchema schema = getJsonSchema(module, resource, method);
		JsonNode jsonNode = JsonLoader.fromString(jsonStr);
		ProcessingReport report = schema.validate(jsonNode);
		if (report.isSuccess())
			return;
		Iterator<ProcessingMessage> iterator = report.iterator();
		while (iterator.hasNext()) {
			ProcessingMessage msg = iterator.next();
				throw new C422Exception(BaseExceptionMessage.CHECK_JSON_MSG, msg.getMessage());
		}
    }
    public static void validateResponseJsonString(String module, String resource, String method, String jsonStr) throws Exception {
		JsonSchema schema = getResponseJsonSchema(module, resource, method);
		JsonNode jsonNode = JsonLoader.fromString(jsonStr);
		ProcessingReport report = schema.validate(jsonNode);
		if (report.isSuccess())
			return;
		Iterator<ProcessingMessage> iterator = report.iterator();
		while (iterator.hasNext()) {
			ProcessingMessage msg = iterator.next();
				throw new C500Exception(BaseExceptionMessage.CHECK_RESPONSEJSON_MSG, msg.getMessage());
			}
    }
    
    /**
     * 检查 是否有jsonschema校验文件
     * @param module
     * @param resource
     * @param method
     * @param type
     * @return
     */
    public static boolean findResponseJsonSchema(String module, String resource, String method){
    	String  path = "/validate/responsejson/" + module + "/" + resource + "_" + method + ".json";
    	try {
			JsonNode jsonSchema = JsonLoader.fromResource(path);
			if(StringUtils.isEmpty(jsonSchema.toString())){
				return false;
			}
		} catch (IOException e) {
			return false;
		}
    	return true;
    }
}
