package space.yangtao.springbootjson.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Jackson工具类
 *
 * @author yangtao
 * @since 2025/7/21 16:37
 */
public final class JacksonUtil {

    private static volatile ObjectMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(JacksonUtil.class);

    private JacksonUtil() {
    }

    /*==================  Spring 注入 mapper  ==================*/

    /**
     * 通过 Spring 自动注入全局 ObjectMapper 并填充静态变量
     * 只在启动阶段执行一次，保证线程安全
     */
    @Configuration(proxyBeanMethods = false)
    @DependsOn("JacksonConfigObjectMapper")
    static class JacksonUtilInitializer implements InitializingBean {

        private final ObjectMapper injectedMapper;

        JacksonUtilInitializer(@Qualifier("JacksonConfigObjectMapper") ObjectMapper injectedMapper) {
            this.injectedMapper = injectedMapper;
        }

        @Override
        public void afterPropertiesSet() {
            JacksonUtil.mapper = this.injectedMapper;
        }
    }

    /*==================  内部异常包装  ==================*/

    public static class JacksonException extends RuntimeException {
        public JacksonException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    /**
     * 获取ObjectMapper实例
     */
    public static ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (JacksonUtil.class) {
                if (mapper == null) {
                    mapper = SpringUtil.getBean("JacksonConfigObjectMapper", ObjectMapper.class);
                }
            }
        }
        return mapper;
    }

    /*==================  异常处理模板  ==================*/

    /**
     * 执行一个操作，如果抛出异常则返回默认值
     */
    public static <T> T executeWithDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.debug("操作失败，返回默认值，异常信息：", e);
            return defaultValue;
        }
    }

    /*==================  核心序列化  ==================*/

    /**
     * 对象序列化为JSON字符串（紧凑）
     */
    public static String toJsonString(Object obj) {
        return executeWithDefault(() -> toJsonStringOrThrow(obj), "{}");
    }

    /**
     * 对象序列化为JSON字符串（紧凑），如果失败则抛出异常
     */
    public static String toJsonStringOrThrow(Object obj) {
        try {
            return getMapper().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("对象序列化为JSON字符串（紧凑）失败，obj =  {}", obj, e);
            throw new JacksonException("对象序列化为JSON字符串（紧凑）失败", e);
        }
    }

    /**
     * 对象序列化为JSON字符串（美化）
     */
    public static String toPrettyJsonString(Object obj) {
        return executeWithDefault(() -> toPrettyJsonStringOrThrow(obj), "{}");
    }

    /**
     * 对象序列化为JSON字符串（美化），如果失败则抛出异常
     */
    public static String toPrettyJsonStringOrThrow(Object obj) {
        try {
            return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("对象序列化为JSON字符串（美化）失败，obj =  {}", obj, e);
            throw new JacksonException("对象序列化为JSON字符串（美化）失败", e);
        }
    }

    /**
     * 对象序列化为字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        return executeWithDefault(() -> toJsonBytesOrThrow(obj), new byte[0]);
    }

    /**
     * 对象序列化为字节数组，如果失败则抛出异常
     */
    public static byte[] toJsonBytesOrThrow(Object obj) {
        try {
            return getMapper().writeValueAsBytes(obj);
        } catch (Exception e) {
            log.error("对象序列化为字节数组失败，obj =  {}", obj, e);
            throw new JacksonException("对象序列化为字节数组失败", e);
        }
    }

    /*==================  核心反序列化  ==================*/

    /**
     * JSON字符串反序列化为对象（简单类型）
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return executeWithDefault(() -> parseObjectOrThrow(json, clazz), null);
    }

    /**
     * JSON字符串反序列化为对象（简单类型），如果失败则抛出异常
     */
    public static <T> T parseObjectOrThrow(String json, Class<T> clazz) {
        try {
            return getMapper().readValue(json, clazz);
        } catch (Exception e) {
            log.error("JSON字符串反序列化为对象（简单类型）失败，json = {}", json, e);
            throw new JacksonException("JSON字符串反序列化为对象（简单类型）失败", e);
        }
    }

    /**
     * JSON字符串反序列化为对象（泛型TypeReference）
     */
    public static <T> T parseObject(String json, TypeReference<T> typeRef) {
        return executeWithDefault(() -> parseObjectOrThrow(json, typeRef), null);
    }

    /**
     * JSON字符串反序列化为对象（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> T parseObjectOrThrow(String json, TypeReference<T> typeRef) {
        try {
            return getMapper().readValue(json, typeRef);
        } catch (Exception e) {
            log.error("JSON字符串反序列化为对象（泛型TypeReference）失败，json = {}", json, e);
            throw new JacksonException("JSON字符串反序列化为对象（泛型TypeReference）失败", e);
        }
    }

    /**
     * 字节数组反序列化为对象（简单类型）
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        return executeWithDefault(() -> parseObjectOrThrow(bytes, clazz), null);
    }

    /**
     * 字节数组反序列化为对象（简单类型），如果失败则抛出异常
     */
    public static <T> T parseObjectOrThrow(byte[] bytes, Class<T> clazz) {
        try {
            return getMapper().readValue(bytes, clazz);
        } catch (Exception e) {
            log.error("字节数组反序列化为对象（简单类型）失败，bytes = {}", bytes, e);
            throw new JacksonException("字节数组反序列化为对象（简单类型）失败", e);
        }
    }

    /**
     * 字节数组反序列化为对象（泛型TypeReference）
     */
    public static <T> T parseObject(byte[] bytes, TypeReference<T> typeRef) {
        return executeWithDefault(() -> parseObjectOrThrow(bytes, typeRef), null);
    }

    /**
     * 字节数组反序列化为对象（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> T parseObjectOrThrow(byte[] bytes, TypeReference<T> typeRef) {
        try {
            return getMapper().readValue(bytes, typeRef);
        } catch (Exception e) {
            log.error("字节数组反序列化为对象（泛型TypeReference）失败，bytes = {}", bytes, e);
            throw new JacksonException("字节数组反序列化为对象（泛型TypeReference）失败", e);
        }
    }

    /**
     * JSON反序列化为列表（简单类型）
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return executeWithDefault(() -> parseArrayOrThrow(json, clazz), Collections.emptyList());
    }

    /**
     * JSON反序列化为列表（简单类型），如果失败则抛出异常
     */
    public static <T> List<T> parseArrayOrThrow(String json, Class<T> clazz) {
        try {
            return getMapper().readValue(json, getMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.error("JSON反序列化为列表（简单类型）失败，json = {}", json, e);
            throw new JacksonException("JSON反序列化为列表（简单类型）失败", e);
        }
    }

    /**
     * JSON反序列化为列表（泛型TypeReference）
     */
    public static <T> List<T> parseArray(String json, TypeReference<T> typeRef) {
        return executeWithDefault(() -> parseArrayOrThrow(json, typeRef), Collections.emptyList());
    }

    /**
     * JSON反序列化为列表（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> List<T> parseArrayOrThrow(String json, TypeReference<T> typeRef) {
        try {
            List<?> tmp = getMapper().readValue(json, List.class);
            return convertList(tmp, typeRef);
        } catch (Exception e) {
            log.error("JSON反序列化为列表（泛型TypeReference）失败，json = {}", json, e);
            throw new JacksonException("JSON反序列化为列表（泛型TypeReference）失败", e);
        }
    }

    /**
     * 字节数组反序列化为列表（简单类型）
     */
    public static <T> List<T> parseArray(byte[] bytes, Class<T> clazz) {
        return executeWithDefault(() -> parseArrayOrThrow(bytes, clazz), Collections.emptyList());
    }

    /**
     * 字节数组反序列化为列表（简单类型），如果失败则抛出异常
     */
    public static <T> List<T> parseArrayOrThrow(byte[] bytes, Class<T> clazz) {
        try {
            return getMapper().readValue(bytes, getMapper().getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.error("字节数组反序列化为列表（简单类型）失败，bytes = {}", bytes, e);
            throw new JacksonException("字节数组反序列化为列表（简单类型）", e);
        }
    }

    /**
     * 字节数组反序列化为列表（泛型TypeReference）
     */
    public static <T> List<T> parseArray(byte[] bytes, TypeReference<T> typeRef) {
        return executeWithDefault(() -> parseArrayOrThrow(bytes, typeRef), null);
    }

    /**
     * 字节数组反序列化为列表（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> List<T> parseArrayOrThrow(byte[] bytes, TypeReference<T> typeRef) {
        try {
            List<?> tmp = getMapper().readValue(bytes, List.class);
            return convertListOrThrow(tmp, typeRef);
        } catch (Exception e) {
            log.error("字节数组反序列化为列表（泛型TypeReference）失败，bytes = {}", bytes, e);
            throw new JacksonException("字节数组反序列化为列表（泛型TypeReference）失败", e);
        }
    }

    /*==================  进阶功能  ==================*/

    /**
     * 对象深克隆（简单类型）
     */
    public static <T> T clone(Object source, Class<T> clazz) {
        return executeWithDefault(() -> cloneOrThrow(source, clazz), null);
    }

    /**
     * 对象深克隆（简单类型），如果失败则抛出异常
     */
    public static <T> T cloneOrThrow(Object source, Class<T> clazz) {
        try {
            return parseObjectOrThrow(toJsonString(source), clazz);
        } catch (Exception e) {
            log.error("对象深克隆（简单类型）失败，source = {}", source, e);
            throw new JacksonException("对象深克隆（简单类型）失败", e);
        }
    }

    /**
     * 对象深克隆（泛型TypeReference）
     */
    public static <T> T clone(Object source, TypeReference<T> typeRef) {
        return executeWithDefault(() -> cloneOrThrow(source, typeRef), null);
    }

    /**
     * 对象深克隆（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> T cloneOrThrow(Object source, TypeReference<T> typeRef) {
        try {
            return parseObjectOrThrow(toJsonString(source), typeRef);
        } catch (Exception e) {
            log.error("对象深克隆（泛型TypeReference）失败，source = {}", source, e);
            throw new JacksonException("对象深克隆（泛型TypeReference）失败", e);
        }
    }

    /**
     * 对象转换（简单类型）
     */
    public static <T> T convert(Object source, Class<T> targetType) {
        return executeWithDefault(() -> convertOrThrow(source, targetType), null);
    }

    /**
     * 对象转换（简单类型），如果失败则抛出异常
     */
    public static <T> T convertOrThrow(Object source, Class<T> targetType) {
        try {
            return getMapper().convertValue(source, targetType);
        } catch (Exception e) {
            log.error("对象转换（简单类型）失败，source = {}", source, e);
            throw new JacksonException("对象转换（简单类型）失败", e);
        }
    }

    /**
     * 对象转换（泛型TypeReference）
     */
    public static <T> T convert(Object source, TypeReference<T> typeRef) {
        return executeWithDefault(() -> convertOrThrow(source, typeRef), null);
    }

    /**
     * 对象转换（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> T convertOrThrow(Object source, TypeReference<T> typeRef) {
        try {
            return getMapper().convertValue(source, typeRef);
        } catch (Exception e) {
            log.error("对象转换（泛型TypeReference）失败，source = {}", source, e);
            throw new JacksonException("对象转换（泛型TypeReference）失败", e);
        }
    }

    /**
     * 列表转换（简单类型）
     */
    public static <T> List<T> convertList(List<?> source, Class<T> targetType) {
        return executeWithDefault(() -> convertListOrThrow(source, targetType), Collections.emptyList());
    }

    /**
     * 列表转换（简单类型），如果失败则抛出异常
     */
    public static <T> List<T> convertListOrThrow(List<?> source, Class<T> targetType) {
        try {
            return getMapper().convertValue(source, getMapper().getTypeFactory().constructCollectionType(List.class, targetType));
        } catch (Exception e) {
            log.error("列表转换（简单类型）失败，source = {}", source, e);
            throw new JacksonException("列表转换（简单类型）失败", e);
        }
    }

    /**
     * 列表转换（泛型TypeReference）
     */
    public static <T> List<T> convertList(List<?> source, TypeReference<T> typeRef) {
        return executeWithDefault(() -> convertListOrThrow(source, typeRef), Collections.emptyList());
    }

    /**
     * 列表转换（泛型TypeReference），如果失败则抛出异常
     */
    public static <T> List<T> convertListOrThrow(List<?> source, TypeReference<T> typeRef) {
        try {
            if (source.isEmpty()) {
                return Collections.emptyList();
            }
            return source.stream()
                    .map(item -> getMapper().convertValue(item, typeRef))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("列表转换（泛型TypeReference）失败，source = {}", source, e);
            throw new JacksonException("列表转换（泛型TypeReference）失败", e);
        }
    }

    /**
     * JSON增量更新对象
     */
    public static <T> T update(String jsonPatch, T target) {
        return executeWithDefault(() -> updateOrThrow(jsonPatch, target), null);
    }

    /**
     * JSON增量更新对象，如果失败则抛出异常
     */
    public static <T> T updateOrThrow(String jsonPatch, T target) {
        try {
            ObjectReader updater = getMapper().readerForUpdating(target);
            return updater.readValue(jsonPatch);
        } catch (Exception e) {
            log.error("JSON增量更新对象失败，jsonPatch = {}, target = {}", jsonPatch, target, e);
            throw new JacksonException("JSON增量更新对象失败", e);
        }
    }

    /**
     * JSON解析为树模型
     */
    public static JsonNode parseTree(String json) {
        return executeWithDefault(() -> parseTreeOrThrow(json), null);
    }

    /**
     * JSON解析为树模型，如果失败则抛出异常
     */
    public static JsonNode parseTreeOrThrow(String json) {
        try {
            return getMapper().readTree(json);
        } catch (Exception e) {
            log.error("JSON解析为树模型失败，json = {}", json, e);
            throw new JacksonException("JSON解析为树模型失败", e);
        }
    }

    /**
     * 判断是否为合法的JSON字符串
     */
    public static boolean isValidJson(String json) {
        try {
            getMapper().readTree(json);
            return true;
        } catch (Exception e) {
            log.warn("无效的JSON字符串：{}", json, e);
            return false;
        }
    }

}
