package space.yangtao.springbootjson.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import space.yangtao.springbootjson.config.EncryptedPhoneModifier;
import space.yangtao.springbootjson.config.Views;
import space.yangtao.springbootjson.domain.*;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yangtao
 * @since 2025/7/13 14:19
 */
public class ObjectMapperTest {

    public static ObjectMapper getCommonObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 配置序列化特征
        // 美化输出（开发环境下使用）
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 禁止序列化空对象（默认是抛出异常）
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 禁止将日期序列化为时间戳（默认是时间戳）
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setTimeZone(SimpleTimeZone.getTimeZone("Asia/Shanghai"));
        mapper.registerModule(new JavaTimeModule());
        // 按照键的顺序序列化Map（默认是无序）
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        // 枚举序列化为字符串（默认是枚举的toString方法）
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        // char数组作为JSON数组序列化（默认是字符串）
        mapper.enable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);

        // 配置反序列化特征
        // 禁止反序列化未知属性（默认是抛出异常）
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 允许读取未知的枚举值为null（默认是抛出异常）
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        // 配置全局映射特性
        // 禁止自动检测字段（默认是自动检测）
        // mapper.disable(MapperFeature.AUTO_DETECT_FIELDS);
        // 禁止自动检测getter方法（默认是自动检测）
        // mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
        // 禁止自动检测is方法（默认是自动检测）
        // mapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        // 禁止自动检测setter方法（默认是自动检测）
        // mapper.disable(MapperFeature.AUTO_DETECT_SETTERS);
        // 启用注解（默认是启用）
        mapper.enable(MapperFeature.USE_ANNOTATIONS);

        // 配置字段包含策略
        // 设置序列化时忽略null值（默认是序列化null值）
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 加密手机模块
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setSerializerModifier(new EncryptedPhoneModifier());
        mapper.registerModule(simpleModule);

        return mapper;
    }

    /**
     * 创建方式
     */
    @Test
    public void test1() {

        // 1. 使用默认构造方法创建
        ObjectMapper objectMapper = new ObjectMapper();

        // 2. 使用静态方法创建
        ObjectMapper objectMapper2 = JsonMapper.builder()
//                .enable(null) // 可以在这里配置一些选项
//                .disable(null) // 可以在这里配置一些选项
                .build();

    }

    /**
     * 测试序列化特征
     */
    @Test
    public void test2() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        User user = new User()
                .setId(1L)
                .setName("user001")
                .setAge(18)
                .setBalance(new BigDecimal("500000000000.00"))
                .setBirthday(LocalDate.now())
                .setCreateTime(LocalDateTime.now())
                .setUnsafeAmount(new BigDecimal("123456789012345678905555555555.123456"))
                .setSafeAmount(new BigDecimal("123456789012345678905555555555.123456"))
                .setChars(new char[]{'a', 'b', 'c'})
                .setGender(User.Gender.MALE);
        String s = mapper.writeValueAsString(user);
        System.out.println(s);

        String json = "{\n" +
                "  \"age\" : 18,\n" +
                "  \"balance\" : 500000000000.00,\n" +
                "  \"gender\" : \"male\",\n" +
                "  \"birthday\" : \"2025-07-13\",\n" +
                "  \"createTime\" : \"2025-07-13 17:12:17\",\n" +
                "  \"unsafeAmount\" : 123456789012345678905555555555.123456,\n" +
                "  \"safeAmount\" : \"123456789012345678905555555555.123456\",\n" +
                "  \"chars\" : \"abc\",\n" +
                "  \"id\" : 1,\n" +
                "  \"name\" : \"user001\"\n" +
                "}";
        User user1 = mapper.readValue(json, User.class);
        System.out.println(user1);
    }

    @Test
    public void test3() throws JsonProcessingException {
        Map<String, List<User>> map = new HashMap<String, List<User>>() {{
            put("users", Collections.singletonList(new User().setId(1L).setName("user001").setAge(18)));
        }};
        ObjectMapper mapper = getCommonObjectMapper();
        System.out.println(mapper.writeValueAsString(map));

        String json = "{\n" +
                "  \"users\" : [ {\n" +
                "    \"age\" : 18,\n" +
                "    \"user_id\" : 1,\n" +
                "    \"name\" : \"user001\"\n" +
                "  } ]\n" +
                "}";
        Map<String, List<User>> map2 = mapper.readValue(json, new TypeReference<Map<String, List<User>>>() {
        });
        System.out.println(map2);
    }

    @Test
    public void test4() {
        ObjectMapper mapper = getCommonObjectMapper();

        String json1 = "{\"userId\":1}";
        String json2 = "{\"user_id\":1}";
        try {
            User user1 = mapper.readValue(json1, User.class);
            System.out.println(user1);
            User user2 = mapper.readValue(json2, User.class);
            System.out.println(user2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test5() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        User user = new User().setId(1L);
        System.out.println(mapper.writeValueAsString(user));
    }

    @Test
    public void test6() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        User user = new User().setPassword("123456");
        System.out.println(mapper.writeValueAsString(user));  // { }

        String json = "{\"password\":1}";
        System.out.println(mapper.readValue(json, User.class));  // User(id=null, password=null)
    }

    @Test
    public void test7() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        User user = new User().setActive(true).setCreateTime(LocalDateTime.now());
        System.out.println(mapper.writeValueAsString(user));  // {"active":true, "createTime":"2025-07-13 17:12:17"}

        String json = "{\"active\":true, \"createTime\":\"2025-07-13 17:12:17\"}";
        System.out.println(mapper.readValue(json, User.class));  // User(active=false, createTime=2025-07-13T17:12:17)
    }

    @Test
    public void test8() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        User user = new User().setPhoneNumber("12345678901");
        System.out.println(mapper.writeValueAsString(user));  // {"phoneNumber":"123****8901"}

    }

    @Test
    public void test9() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        String json = "{\"gender\":\"男\"}";
        User user = mapper.readValue(json, User.class);
        System.out.println(user);
    }

    @Test
    public void test10() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        String json = "{\"type\":\"dog\",\"name\":\"旺财\",\"boneCount\":5}";
        Animal animal = mapper.readValue(json, Animal.class);
        System.out.println(animal instanceof Animal.Dog);  // true
    }

    @Test
    public void test11() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        User user = new User().setPublicField("p").setInternalField("i");
        String json = mapper.writerWithView(Views.Public.class)
                .writeValueAsString(user);
        System.out.println(json);
    }

    @Test
    public void test12() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        String json = "{\"id\":1,\"name\":\"张三\"}";
        Student student = mapper.readValue(json, Student.class);
        System.out.println(student);
    }

    @Test
    public void test13() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        String json = "{\"id\":1,\"name\":\"张三\",\"age\":18}";
        DynamicUser dynamicUser = mapper.readValue(json, DynamicUser.class);
        System.out.println(dynamicUser);
    }

    @Test
    public void test14() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        Config cfg = new Config();
        cfg.setName("系统配置");
        cfg.getSettings().put("threadPool", 10);
        cfg.getSettings().put("timeout", 5000);

        System.out.println(mapper.writeValueAsString(cfg));

    }

    @Test
    public void test15() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        mapper.addMixIn(OnlyReadClass.class, OnlyReadClassMixIn.class);
        OnlyReadClass onlyReadClass = new OnlyReadClass().setId(1L).setName("只读类");
        String json = mapper.writeValueAsString(onlyReadClass);
        System.out.println(json);

        String fromJson = "{\"or_id\":1,\"or_name\":\"只读类\"}";
        OnlyReadClass orClass = mapper.readValue(fromJson, OnlyReadClass.class);
        System.out.println(orClass);

    }

    @Test
    public void test16() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        User user = new User().setPhone(new EncryptedPhone("13545678901"));
        System.out.println(mapper.writeValueAsString(user));
    }

    @Test
    public void test17() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("publicField");
        SimpleFilterProvider userFilter = new SimpleFilterProvider().addFilter("userFilter", filter);
        User user = new User().setPublicField("p").setInternalField("i");
        String json = mapper.writer(userFilter).writeValueAsString(user);
        System.out.println(json);
    }

    @Test
    public void test18() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();

        Parent parent = new Parent();
        Child child = new Child();
        parent.setName("p");
        parent.setChild(child);
        child.setName("c");
        child.setParent(parent);

        System.out.println(mapper.writeValueAsString(parent));
        System.out.println(mapper.writeValueAsString(child));
    }

    @Test
    public void test19() throws JsonProcessingException {
        Person a = new Person().setId(1L).setName("A");
        Person b = new Person().setId(2L).setName("B");
        a.setFriends(Collections.singletonList(b));
        b.setFriends(Collections.singletonList(a));

        ObjectMapper mapper = getCommonObjectMapper();
        System.out.println(mapper.writeValueAsString(a));
        System.out.println(mapper.writeValueAsString(b));
    }

    @Test
    public void test20() throws IOException {
        String json = "{\"id\":1,\"name\":\"张三\"}";

        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(json);

        while (!parser.isClosed()) {
            JsonToken token = parser.nextToken();

            if (JsonToken.FIELD_NAME.equals(token)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken(); // 移动到值
                String value = parser.getText();

                System.out.println(fieldName + " = " + value);
            }
        }

    }

    @Test
    public void test21() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator gen = new JsonFactory().createGenerator(writer);

        gen.writeStartObject();
        gen.writeNumberField("id", 1);
        gen.writeStringField("name", "李四");
        gen.writeEndObject();
        gen.close();

        System.out.println(writer);

    }

    @Test
    public void test22() throws IOException {
        byte[] jsonBytes = "{\"id\":1,\"name\":\"Leo\"}".getBytes(StandardCharsets.UTF_8);

        JsonFactory factory = new JsonFactory();
        NonBlockingJsonParser parser = (NonBlockingJsonParser) factory.createNonBlockingByteArrayParser();

        // 一次性喂数据
        parser.feedInput(jsonBytes, 0, jsonBytes.length);
        parser.endOfInput(); // 告诉解析器数据喂完了

        JsonToken token;
        while ((token = parser.nextToken()) != null && token != JsonToken.NOT_AVAILABLE) {
            if (token == JsonToken.FIELD_NAME) {
                String field = parser.getCurrentName();
                parser.nextToken(); // 获取字段值
                String value = parser.getText();
                System.out.println(field + " = " + value);
            }
        }

    }

    @Test
    public void test23() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"张三\",\"tags\":[\"a\",\"b\"]}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        int id = root.get("id").asInt();            // 1
        String name = root.get("name").asText();    // 张三
        JsonNode tags = root.get("tags");           // ArrayNode

        for (JsonNode tagNode : tags) {
            System.out.println(tagNode.asText());  // a, b
        }

        String nickname = root.path("nickname").asText("默认昵称");  // 不存在时返回默认值

    }

    @Test
    public void test24() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.createObjectNode();

        obj.put("id", 100);
        obj.put("name", "Leo");

        ArrayNode array = mapper.createArrayNode();
        array.add("x").add("y");

        obj.set("tags", array);

        System.out.println(obj.toPrettyString());

    }

    @Test
    public void test25() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        // 添加字段
        root.put("gender", "male");

        // 修改字段
        root.put("name", "李四");

        // 删除字段
        root.remove("tags");

        // 嵌套修改
        ((ArrayNode) root.withArray("tags")).add("c");

    }

    @Test
    public void test26_1() {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();

        // 添加元素
        root.add("apple");
        root.add("banana");

        // 修改元素
        root.set(1, "orange");

        // 删除元素
        root.remove(0);

    }

    @Test
    public void test26() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();


        String json = "{}";
        JsonNode jsonNode = mapper.readTree(json);
        User user = mapper.treeToValue(jsonNode, User.class);
        System.out.println(user);


    }

    @Test
    public void test27() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        String json = "[]";
        JsonNode jsonNode = mapper.readTree(json);
        List<User> users = mapper.convertValue(jsonNode, new TypeReference<List<User>>() {});
        System.out.println(users);
    }

    @Test
    public void test28() {
        // Java对象 -> JsonNode
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        JsonNode jsonNode = mapper.valueToTree(user);

        ObjectNode objectNode = mapper.valueToTree(user);
        ArrayNode arrayNode = mapper.valueToTree(user);
    }

    @Test
    public void test29() {
        // jsonNode的输出
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", 1);
        objectNode.put("name", "张三");
        // 普通输出
        String json = objectNode.toString();
        System.out.println(json);  // {"id":1,"name":"张三"}
        // 美化输出
        String prettyJson = objectNode.toPrettyString();
        System.out.println(prettyJson);  //
    }

    @Test
    public void test30() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        String s = mapper.writeValueAsString(null);
        System.out.println(s == null);
        System.out.println("null".equals(s));
    }

    @Test
    public void test31() throws JsonProcessingException {
        ObjectMapper mapper = getCommonObjectMapper();
        System.out.println(Arrays.toString(mapper.writeValueAsBytes(null)));
    }

}
