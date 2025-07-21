package space.yangtao.springbootjson.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yangtao
 * @since 2025/7/21 16:37
 */
@Slf4j
public class JacksonUtil {

    /**
     * 获取 ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return SpringUtil.getBean(ObjectMapper.class);
    }

    @SneakyThrows
    public static String toJsonString(Object object) {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static byte[] toJsonByte(Object object) {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.writeValueAsBytes(object);
    }

    @SneakyThrows
    public static String toJsonPrettyString(Object object) {
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    public JsonNode parseObject(String text) {

    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        }
        try {
            ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.readValue(text, clazz);
        } catch (IOException e) {
            log.error("json parse err, json: {}", text, e);
            throw new RuntimeException(e);
        }
    }

}
