package space.yangtao.springbootjson.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Jackson工具类
 *
 * @author yangtao
 * @since 2025/7/21 16:37
 */
public final class JacksonUtil {

    private static volatile ObjectMapper mapper;

    private JacksonUtil() {
    }

    /*==================  Spring 注入 mapper  ==================*/

    /**
     * 通过 Spring 自动注入全局 ObjectMapper 并填充静态变量
     * 只在启动阶段执行一次，保证线程安全
     */
    @Configuration(proxyBeanMethods = false)
    static class JacksonUtilInitializer implements InitializingBean {
        private final ObjectMapper injectedMapper;

        /**
         * 构造注入；@Lazy防止循环依赖
         */
        JacksonUtilInitializer(@Lazy ObjectMapper injectedMapper) {
            this.injectedMapper = injectedMapper;
        }

        @Override
        public void afterPropertiesSet() {
            JacksonUtil.mapper = this.injectedMapper;
        }
    }

    /**
     * 获取ObjectMapper实例
     */
    public static ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (JacksonUtil.class) {
                if (mapper == null) {
                    mapper = SpringUtil.getBean(ObjectMapper.class);
                }
            }
        }
        return mapper;
    }

    /*==================  核心序列化  ==================*/

    /**
     * 对象 → JSON字符串（紧凑）
     */
    @SneakyThrows
    public static String toJsonString(@NonNull Object obj) {
        return getMapper().writeValueAsString(obj);
    }

    /**
     * 对象 → JSON 字符串（美化）
     */
    @SneakyThrows
    public static String toPrettyJsonString(@NonNull Object obj) {
        return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * 对象 → UTF-8字节数组
     */
    @SneakyThrows
    public static byte[] toJsonBytes(@NonNull Object obj) {
        return getMapper().writeValueAsBytes(obj);
    }

    /*==================  核心反序列化  ==================*/

    /**
     * JSON → 对象（简单类型）
     */
    @SneakyThrows
    public static <T> T parseObject(@NonNull String json, @NonNull Class<T> clazz) {
        return getMapper().readValue(json, clazz);
    }

    /**
     * JSON → 列表
     */
    @SneakyThrows
    public static <T> List<T> parseArray(@NonNull String json, @NonNull Class<T> clazz) {
        return getMapper().readValue(json, getMapper().getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * JSON → 对象（支持泛型TypeReference）
     */
    @SneakyThrows
    public static <T> T parseObject(@NonNull String json, @NonNull TypeReference<T> typeRef) {
        return getMapper().readValue(json, typeRef);
    }

    /**
     * 字节数组 → 对象
     */
    @SneakyThrows
    public static <T> T parseBytes(@NonNull byte[] bytes, @NonNull Class<T> clazz) {
        return getMapper().readValue(bytes, clazz);
    }

    /**
     * 字节数组 → 列表
     */
    @SneakyThrows
    public static <T> List<T> parseBytesToList(@NonNull byte[] bytes, @NonNull Class<T> clazz) {
        return getMapper().readValue(bytes, getMapper().getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * 字节数组 → 对象（支持泛型TypeReference）
     */
    @SneakyThrows
    public static <T> T parseBytes(@NonNull byte[] bytes, @NonNull TypeReference<T> typeRef) {
        return getMapper().readValue(bytes, typeRef);
    }

    /*==================  进阶功能  ==================*/

    /**
     * 对象深克隆
     */
    public static <T> T clone(@NonNull T src, @NonNull Class<T> clazz) {
        return parseObject(toJsonString(src), clazz);
    }

    /**
     * 将对象转为指定类型的对象
     */
    public static <T> T convert(@NonNull Object source, @NonNull Class<T> targetType) {
        return getMapper().convertValue(source, targetType);
    }

    /**
     * 将对象转换为指定类型（支持泛型TypeReference）
     */
    public static <T> T convert(@NonNull Object source, @NonNull TypeReference<T> typeRef) {
        return getMapper().convertValue(source, typeRef);
    }

    /**
     * 将列表转换为指定类型的列表
     */
    public static <T> List<T> convertList(@NonNull List<?> source, @NonNull Class<T> targetType) {
        return getMapper().convertValue(source, getMapper().getTypeFactory().constructCollectionType(List.class, targetType));
    }

    /**
     * 将列表转换为指定类型的列表（支持泛型TypeReference）
     */
    public static <T> List<T> convertList(@NonNull List<?> source, @NonNull TypeReference<T> typeRef) {
        if (source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream()
                 .map(item -> getMapper().convertValue(item, typeRef))
                 .collect(Collectors.toList());
    }

    /**
     * 将JSON字符串增量更新到指定对象
     */
    @SneakyThrows
    public static <T> T update(@NonNull String jsonPatch, @NonNull T target) {
        ObjectReader updater = getMapper().readerForUpdating(target);
        return updater.readValue(jsonPatch);
    }

    /**
     * 解析为树模型
     */
    @SneakyThrows
    public static JsonNode parseTree(@NonNull String json) {
        return getMapper().readTree(json);
    }

}
