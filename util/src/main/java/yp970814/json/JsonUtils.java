package yp970814.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import yp970814.enums.RespCodeEnum;
import yp970814.exception.SystemRuntimeException;

import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:10
 */
@Slf4j
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new JsonConfig().objectMapper();

    /**
     * 对象转json
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String toJson(T t) {
        try {
            return OBJECT_MAPPER.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.info("json 转换失败!", e);
        }
        return null;
    }

    /**
     * json  转对象
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
//    public static <T> T toObject(String json, Class<T> clazz){
//        try {
//            return OBJECT_MAPPER.readValue(json, clazz);
//        } catch (JsonProcessingException e) {
//            log.info("json 转换失败!", e);
//        }
//        return null;
//    }


    /**
     * 转换对象
     *
     * @param json
     * @param valueTypeRef
     * @param <T>
     * @return
     */
//    public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
//        try {
//            return OBJECT_MAPPER.readValue(json, valueTypeRef);
//        } catch (JsonProcessingException e) {
//            log.info("json 转换失败!", e);
//        }
//        return null;
//    }

    /**
     * 对象转换为map
     *
     * @param obj
     * @return
     */
    public static Map<String, String> object2Map(Object obj) {
        Map<String, String> params;
        try {
            params = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            log.warn("数据转换失败！", e);
            throw new SystemRuntimeException(RespCodeEnum.A0400);
        }
        return params;
    }

    /**
     * 对象转换为map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> object2MapObj(Object obj) {
        Map<String, Object> params;
        try {
            params = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.warn("数据转换失败！", e);
            throw new SystemRuntimeException(RespCodeEnum.A0400);
        }
        return params;
    }

    /**
     * 对象转换为map
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> jsonString2MapObj(String jsonStr) {
        Map<String, Object> params;
        try {
            params = OBJECT_MAPPER.readValue(jsonStr, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.warn("数据转换失败！", e);
            throw new SystemRuntimeException(RespCodeEnum.A0400);
        }
        return params;
    }

}
