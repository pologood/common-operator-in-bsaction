package com.gomeplus.oversea.bs.service.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Created by shangshengfang on 2017/4/5.
 */
@Slf4j
public class SchemaFactory {

    public static JsonSchema getHeaderSchema(HttpServletRequest request){
        JSONObject schemaJson = initSchema();
        JsonNode swaggerParameters = getSwaggerParameters(request);
        Iterator<JsonNode> iterator = swaggerParameters.iterator();
        JSONObject property=new JSONObject();
        JSONArray required = new JSONArray();
        while (iterator.hasNext()) {
            JsonNode paramNode = iterator.next();
            if(paramNode.get("$ref")!=null){
                JsonNode ref = paramNode.get("$ref");
                JsonNode param =SwaggerUtil.getPubParameters(ref.asText());
                if(param.get("in")!=null && "header".equals(param.get("in").asText())) {
                    JSONObject typeNode = new JSONObject();
                    typeNode.put("type", param.get("type").asText());
                    property.put(param.get("name").asText().toLowerCase(), typeNode);
                    if (param.get("required") != null && param.get("required").asBoolean()) {
                        required.add(param.get("name").asText().toLowerCase());
                    }
                }
            }
        }
        schemaJson.put("properties",property);
        schemaJson.put("required",required);
        JsonSchema schema = createSchema(schemaJson);
        return schema;
    }
    public static JsonSchema getQuerySchema(HttpServletRequest request){
        JSONObject schemaJson = initSchema();
        JsonNode swaggerParameters = getSwaggerParameters(request);
        Iterator<JsonNode> iterator = swaggerParameters.iterator();
        JSONObject property=new JSONObject();
        JSONArray required = new JSONArray();
        while (iterator.hasNext()) {
            JsonNode paramNode = iterator.next();
            if(paramNode.get("$ref")==null && paramNode.get("in")!=null && "query".equals(paramNode.get("in").asText())){
                JSONObject paramProperties = new JSONObject();
                Iterator<String> fields = paramNode.fieldNames();
                while(fields.hasNext()){
                    String fieldName=fields.next();
                    if(SwaggerUtil.validateArgs().contains(fieldName)){
                        paramProperties.put(fieldName,paramNode.get(fieldName));
                    }
                }
                property.put(paramNode.get("name").asText(), paramProperties);
                if (paramNode.get("required") != null && paramNode.get("required").asBoolean()) {
                    required.add(paramNode.get("name"));
                }
            }
        }
        schemaJson.put("properties",property);
        schemaJson.put("required",required);
        JsonSchema schema = createSchema(schemaJson);
        return schema;
    }
    public static JsonSchema getBodySchema(HttpServletRequest request){
        JSONObject schemaJson = initSchema();
        JsonNode swaggerParameters = getSwaggerParameters(request);

        Iterator<JsonNode> iterator = swaggerParameters.iterator();
        JSONObject property=new JSONObject();
        JSONArray required = new JSONArray();
        while (iterator.hasNext()) {
            JsonNode paramNode = iterator.next();
            String path;
            //schema定义的json参数
            if (paramNode.get("schema") != null && paramNode.get("schema").get("$ref") != null) {
                path = paramNode.get("schema").get("$ref").asText();
                JsonNode bodyDefinition = SwaggerUtil.getPubParameters(path);
                schemaJson.put("properties", bodyDefinition.get("properties"));
                if (bodyDefinition.get("required") != null) {
                    ArrayNode requiredNode = (ArrayNode) bodyDefinition.get("required");
                    schemaJson.put("required", requiredNode);
                }
            }
        }
        JsonSchema schema = createSchema(schemaJson);
        return schema;
    }
    static JsonSchema createSchema(JSONObject schema){
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            String schemaString = schema.toJSONString();
            JsonNode schemaJson = JsonLoader.fromString(schemaString);
            JsonSchema jsonSchema = factory.getJsonSchema(schemaJson);
            return jsonSchema;
        }catch(Exception e){
            log.info("getSchemaError:",e);
            return null;
        }
    }
    static JSONObject initSchema(){
        JSONObject schema =new JSONObject();
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        return schema;
    }
    static JsonNode getSwaggerParameters(HttpServletRequest request){
        JsonNode swagger=SwaggerUtil.getSwagger();
        JsonNode paths = swagger.get("paths");
        JsonNode uriNode = paths.get(request.getRequestURI());
        JsonNode methodNode = uriNode.get(request.getMethod().toLowerCase());
        JsonNode paramsNode = methodNode.get("parameters");
        return paramsNode;
    }

}
