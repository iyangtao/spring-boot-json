package space.yangtao.springbootjson.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.yangtao.springbootjson.utils.JacksonUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yangtao
 * @since 2025/7/23 11:25
 */
@RestController
@RequestMapping("/jackson")
public class JacksonUtilTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class User {
        private Long id;                           // Long 精度测试
        private String name;                       // 普通字符串
        private Integer age;                       // 整数
        private Boolean active;                    // 布尔
        private BigDecimal balance;                // BigDecimal 精度
        private LocalDate birthday;                // Java 8 日期
        private LocalDateTime createTime;          // Java 8 日期时间
        private Date updateTime;                   // 旧版 Date
        private List<String> roles;                // List<String>
        private Map<String, Object> attributes;    // Map<String,Object>
        private Gender gender;                     // 枚举

        @Getter
        @AllArgsConstructor
        public enum Gender {
            MALE("M"),
            FEMALE("F");

            private final String code;

            @Override
            public String toString() {
                return code;
            }

            // 反序列化时使用的工厂方法
            @JsonCreator
            public static Gender fromValue(String value) {
                for (Gender gender : values()) {
                    if (gender.code.equals(value)) {
                        return gender;
                    }
                }
                return null; // 如果没有匹配的值，返回默认值
            }
        }
    }

    // 返回一个 Map<String,Object> 的测试数据
    public Map<String, Object> getMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", 1001L);
        map.put("name", "yangtao");
        map.put("age", 30);
        map.put("active", true);
        map.put("balance", new BigDecimal("12345.67"));
        map.put("birthday", LocalDate.of(1990, 1, 1));
        map.put("createTime", LocalDateTime.of(2025, 7, 23, 16, 0, 0));
        map.put("updateTime", new Date());
        map.put("roles", Arrays.asList("USER", "ADMIN"));
        Map<String, Object> nested = new HashMap<>();
        nested.put("featureX", "enabled");
        nested.put("maxCount", 5);
        map.put("attributes", nested);
        map.put("gender", User.Gender.MALE);
        return map;
    }

    // 返回一个单个 User 对象的测试数据
    public User getUser() {
        return new User()
                .setId(1002L)
                .setName("zhangsan")
                .setAge(28)
                .setActive(false)
                .setBalance(new BigDecimal("9876.54"))
                .setBirthday(LocalDate.of(1985, 12, 15))
                .setCreateTime(LocalDateTime.of(2025, 7, 23, 14, 30, 0))
                .setUpdateTime(new Date())
                .setRoles(Collections.singletonList("EDITOR"))
                .setAttributes(getMap())
                .setGender(User.Gender.FEMALE);
    }

    // 返回一个 User 列表的测试数据
    public List<User> getUserList() {
        return Arrays.asList(
                getUser(),
                new User()
                        .setId(1003L)
                        .setName("lisi")
                        .setAge(35)
                        .setActive(true)
                        .setBalance(new BigDecimal("5000.00"))
                        .setBirthday(LocalDate.of(1992, 6, 30))
                        .setCreateTime(LocalDateTime.of(2025, 7, 22, 10, 15, 0))
                        .setUpdateTime(new Date())
                        .setRoles(Arrays.asList("VIEWER", "USER"))
                        .setAttributes(Collections.singletonMap("note", "second user"))
                        .setGender(User.Gender.MALE)
        );
    }

    // 测试JacksonUtil的全局ObjectMapper
    @GetMapping("/1")
    public void test1() throws JsonProcessingException {
        ObjectMapper mapper = JacksonUtil.getMapper();
        System.out.println(objectMapper == mapper);
        System.out.println(mapper.writeValueAsString(getUser()));
    }

    // 测试JacksonUtil的toJsonString方法
    @GetMapping("/2")
    public void test2() throws JsonProcessingException {
        String json = JacksonUtil.toJsonString(getUser());
        System.out.println(json);
    }

    // 测试JacksonUtil的toJsonString方法抛异常
    @GetMapping("/3")
    public void test3() throws JsonProcessingException {
        String json = JacksonUtil.toJsonStringOrThrow(getUser());
        System.out.println(json);
    }

    // 测试JacksonUtil的toPrettyJsonStringOrThrow方法
    @GetMapping("/4")
    public void test4() throws JsonProcessingException {
        String json = JacksonUtil.toPrettyJsonString(getUser());
        System.out.println(json);
    }

    // 测试JacksonUtil的toPrettyJsonStringOrThrow方法（异常）
    @GetMapping("/5")
    public void test5() throws JsonProcessingException {
        String json = JacksonUtil.toPrettyJsonStringOrThrow(getUser());
        System.out.println(json);
    }

    // 测试JacksonUtil的toPrettyJsonStringOrThrow方法
    @GetMapping("/6")
    public void test6() throws JsonProcessingException {
        byte[] bytes = JacksonUtil.toJsonBytes(getUser());
        System.out.println(Arrays.toString(bytes));
    }

    // 测试JacksonUtil的toPrettyJsonStringOrThrow方法（异常）
    @GetMapping("/7")
    public void test7() throws JsonProcessingException {
        byte[] bytes = JacksonUtil.toJsonBytesOrThrow(getUser());
        System.out.println(Arrays.toString(bytes));
    }

    // 测试JSON字符串反序列化为对象（简单类型）
    @GetMapping("/8")
    public void test8() throws JsonProcessingException {
//        String json = "{";
//        String json = "{";
        String json = "{\"id\":\"1002\",\"name\":\"zhangsan\",\"age\":28,\"active\":false,\"balance\":\"9876.54\",\"birthday\":\"1985-12-15\",\"createTime\":\"2025-07-23 14:30:00\",\"updateTime\":\"2025-07-23 21:54:25\",\"roles\":[\"EDITOR\"],\"attributes\":{\"active\":true,\"age\":30,\"attributes\":{\"featureX\":\"enabled\",\"maxCount\":5},\"balance\":\"12345.67\",\"birthday\":\"1990-01-01\",\"createTime\":\"2025-07-23 16:00:00\",\"gender\":\"M\",\"id\":\"1001\",\"name\":\"yangtao\",\"roles\":[\"USER\",\"ADMIN\"],\"updateTime\":\"2025-07-23 21:54:25\"},\"gender\":\"F\"}";
//        User user = JacksonUtil.parseObject(json, User.class);
        Map<String, Object> user = JacksonUtil.parseObject(json, new TypeReference<Map<String, Object>>() {});
        System.out.println(user);
    }

    @GetMapping("/9")
    public void test9() {
        byte[] bytes = new byte[] {123, 34, 105, 100, 34, 58, 34, 49, 48, 48, 50, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 122, 104, 97, 110, 103, 115, 97, 110, 34, 44, 34, 97, 103, 101, 34, 58, 50, 56, 44, 34, 97, 99, 116, 105, 118, 101, 34, 58, 102, 97, 108, 115, 101, 44, 34, 98, 97, 108, 97, 110, 99, 101, 34, 58, 34, 57, 56, 55, 54, 46, 53, 52, 34, 44, 34, 98, 105, 114, 116, 104, 100, 97, 121, 34, 58, 34, 49, 57, 56, 53, 45, 49, 50, 45, 49, 53, 34, 44, 34, 99, 114, 101, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 49, 52, 58, 51, 48, 58, 48, 48, 34, 44, 34, 117, 112, 100, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 50, 50, 58, 48, 51, 58, 53, 53, 34, 44, 34, 114, 111, 108, 101, 115, 34, 58, 91, 34, 69, 68, 73, 84, 79, 82, 34, 93, 44, 34, 97, 116, 116, 114, 105, 98, 117, 116, 101, 115, 34, 58, 123, 34, 97, 99, 116, 105, 118, 101, 34, 58, 116, 114, 117, 101, 44, 34, 97, 103, 101, 34, 58, 51, 48, 44, 34, 97, 116, 116, 114, 105, 98, 117, 116, 101, 115, 34, 58, 123, 34, 102, 101, 97, 116, 117, 114, 101, 88, 34, 58, 34, 101, 110, 97, 98, 108, 101, 100, 34, 44, 34, 109, 97, 120, 67, 111, 117, 110, 116, 34, 58, 53, 125, 44, 34, 98, 97, 108, 97, 110, 99, 101, 34, 58, 34, 49, 50, 51, 52, 53, 46, 54, 55, 34, 44, 34, 98, 105, 114, 116, 104, 100, 97, 121, 34, 58, 34, 49, 57, 57, 48, 45, 48, 49, 45, 48, 49, 34, 44, 34, 99, 114, 101, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 49, 54, 58, 48, 48, 58, 48, 48, 34, 44, 34, 103, 101, 110, 100, 101, 114, 34, 58, 34, 77, 34, 44, 34, 105, 100, 34, 58, 34, 49, 48, 48, 49, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 121, 97, 110, 103, 116, 97, 111, 34, 44, 34, 114, 111, 108, 101, 115, 34, 58, 91, 34, 85, 83, 69, 82, 34, 44, 34, 65, 68, 77, 73, 78, 34, 93, 44, 34, 117, 112, 100, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 50, 50, 58, 48, 51, 58, 53, 53, 34, 125, 44, 34, 103, 101, 110, 100, 101, 114, 34, 58, 34, 70, 34, 125};
        // Map<String, Object> user = JacksonUtil.parseObject(bytes, new TypeReference<Map<String, Object>>() {});
//        User user = JacksonUtil.parseObject(bytes, User.class);
        User user = JacksonUtil.parseObjectOrThrow(bytes, User.class);
        System.out.println(user);
    }

    @GetMapping("/10")
    public void test10() {
        List<User> userList = getUserList();
//        System.out.println(JacksonUtil.toJsonString(userList));
        System.out.println(Arrays.toString(JacksonUtil.toJsonBytes(userList)));
    }

    @GetMapping("/11")
    public void test11() {
        String json = "[{\"id\":\"1002\",\"name\":\"zhangsan\",\"age\":28,\"active\":false,\"balance\":\"9876.54\",\"birthday\":\"1985-12-15\",\"createTime\":\"2025-07-23 14:30:00\",\"updateTime\":\"2025-07-23 22:10:11\",\"roles\":[\"EDITOR\"],\"attributes\":{\"active\":true,\"age\":30,\"attributes\":{\"featureX\":\"enabled\",\"maxCount\":5},\"balance\":\"12345.67\",\"birthday\":\"1990-01-01\",\"createTime\":\"2025-07-23 16:00:00\",\"gender\":\"M\",\"id\":\"1001\",\"name\":\"yangtao\",\"roles\":[\"USER\",\"ADMIN\"],\"updateTime\":\"2025-07-23 22:10:11\"},\"gender\":\"F\"},{\"id\":\"1003\",\"name\":\"lisi\",\"age\":35,\"active\":true,\"balance\":\"5000.00\",\"birthday\":\"1992-06-30\",\"createTime\":\"2025-07-22 10:15:00\",\"updateTime\":\"2025-07-23 22:10:11\",\"roles\":[\"VIEWER\",\"USER\"],\"attributes\":{\"note\":\"second user\"},\"gender\":\"M\"}]";
        // System.out.println(JacksonUtil.parseArray(json, User.class));
        System.out.println(JacksonUtil.parseArray(json, new TypeReference<Map<String, Object>>() {}));
    }

    @GetMapping("/12")
    public void test12() {
        byte[] bytes = new byte[] {91, 123, 34, 105, 100, 34, 58, 34, 49, 48, 48, 50, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 122, 104, 97, 110, 103, 115, 97, 110, 34, 44, 34, 97, 103, 101, 34, 58, 50, 56, 44, 34, 97, 99, 116, 105, 118, 101, 34, 58, 102, 97, 108, 115, 101, 44, 34, 98, 97, 108, 97, 110, 99, 101, 34, 58, 34, 57, 56, 55, 54, 46, 53, 52, 34, 44, 34, 98, 105, 114, 116, 104, 100, 97, 121, 34, 58, 34, 49, 57, 56, 53, 45, 49, 50, 45, 49, 53, 34, 44, 34, 99, 114, 101, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 49, 52, 58, 51, 48, 58, 48, 48, 34, 44, 34, 117, 112, 100, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 50, 50, 58, 49, 53, 58, 50, 50, 34, 44, 34, 114, 111, 108, 101, 115, 34, 58, 91, 34, 69, 68, 73, 84, 79, 82, 34, 93, 44, 34, 97, 116, 116, 114, 105, 98, 117, 116, 101, 115, 34, 58, 123, 34, 97, 99, 116, 105, 118, 101, 34, 58, 116, 114, 117, 101, 44, 34, 97, 103, 101, 34, 58, 51, 48, 44, 34, 97, 116, 116, 114, 105, 98, 117, 116, 101, 115, 34, 58, 123, 34, 102, 101, 97, 116, 117, 114, 101, 88, 34, 58, 34, 101, 110, 97, 98, 108, 101, 100, 34, 44, 34, 109, 97, 120, 67, 111, 117, 110, 116, 34, 58, 53, 125, 44, 34, 98, 97, 108, 97, 110, 99, 101, 34, 58, 34, 49, 50, 51, 52, 53, 46, 54, 55, 34, 44, 34, 98, 105, 114, 116, 104, 100, 97, 121, 34, 58, 34, 49, 57, 57, 48, 45, 48, 49, 45, 48, 49, 34, 44, 34, 99, 114, 101, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 49, 54, 58, 48, 48, 58, 48, 48, 34, 44, 34, 103, 101, 110, 100, 101, 114, 34, 58, 34, 77, 34, 44, 34, 105, 100, 34, 58, 34, 49, 48, 48, 49, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 121, 97, 110, 103, 116, 97, 111, 34, 44, 34, 114, 111, 108, 101, 115, 34, 58, 91, 34, 85, 83, 69, 82, 34, 44, 34, 65, 68, 77, 73, 78, 34, 93, 44, 34, 117, 112, 100, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 50, 50, 58, 49, 53, 58, 50, 50, 34, 125, 44, 34, 103, 101, 110, 100, 101, 114, 34, 58, 34, 70, 34, 125, 44, 123, 34, 105, 100, 34, 58, 34, 49, 48, 48, 51, 34, 44, 34, 110, 97, 109, 101, 34, 58, 34, 108, 105, 115, 105, 34, 44, 34, 97, 103, 101, 34, 58, 51, 53, 44, 34, 97, 99, 116, 105, 118, 101, 34, 58, 116, 114, 117, 101, 44, 34, 98, 97, 108, 97, 110, 99, 101, 34, 58, 34, 53, 48, 48, 48, 46, 48, 48, 34, 44, 34, 98, 105, 114, 116, 104, 100, 97, 121, 34, 58, 34, 49, 57, 57, 50, 45, 48, 54, 45, 51, 48, 34, 44, 34, 99, 114, 101, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 50, 32, 49, 48, 58, 49, 53, 58, 48, 48, 34, 44, 34, 117, 112, 100, 97, 116, 101, 84, 105, 109, 101, 34, 58, 34, 50, 48, 50, 53, 45, 48, 55, 45, 50, 51, 32, 50, 50, 58, 49, 53, 58, 50, 50, 34, 44, 34, 114, 111, 108, 101, 115, 34, 58, 91, 34, 86, 73, 69, 87, 69, 82, 34, 44, 34, 85, 83, 69, 82, 34, 93, 44, 34, 97, 116, 116, 114, 105, 98, 117, 116, 101, 115, 34, 58, 123, 34, 110, 111, 116, 101, 34, 58, 34, 115, 101, 99, 111, 110, 100, 32, 117, 115, 101, 114, 34, 125, 44, 34, 103, 101, 110, 100, 101, 114, 34, 58, 34, 77, 34, 125, 93};
//        System.out.println(JacksonUtil.parseObject(bytes, new TypeReference<List<User>>() {}));
//        System.out.println(JacksonUtil.parseArray(bytes, new TypeReference<Map<String, Object>>() {}));
        System.out.println(JacksonUtil.parseArray(bytes, User.class));
    }

    @GetMapping("/13")
    public void test13() {
        User user1 = getUser();
        User clone = JacksonUtil.clone(user1, User.class);
        clone.setActive(true);
        System.out.println(user1);
        System.out.println(clone);
        System.out.println(user1 == clone);
        Map cloneMap = JacksonUtil.clone(user1, Map.class);
        System.out.println(cloneMap);
    }

    @GetMapping("/14")
    public void test14() {
        User user1 = getUser();
        User clone = JacksonUtil.clone(user1, new TypeReference<User>() {});
        clone.setActive(true);
        System.out.println(user1);
        System.out.println(clone);
        System.out.println(user1 == clone);
        Map<String, Object> cloneMap = JacksonUtil.clone(user1, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(cloneMap);
    }

    @GetMapping("/15")
    public void test15() {
        User user1 = getUser();
        User clone = JacksonUtil.convert(user1, User.class);
        clone.setActive(true);
        System.out.println(user1);
        System.out.println(clone);
        System.out.println(user1 == clone);
        Map cloneMap = JacksonUtil.convert(user1, Map.class);
        System.out.println(cloneMap);
    }

    @GetMapping("/16")
    public void test16() {
        User user1 = getUser();
        User clone = JacksonUtil.convert(user1, new TypeReference<User>() {});
        clone.setActive(true);
        System.out.println(user1);
        System.out.println(clone);
        System.out.println(user1 == clone);
        Map<String, Object> cloneMap = JacksonUtil.convert(user1, new TypeReference<Map<String, Object>>() {});
        System.out.println(cloneMap);

        System.out.println(JacksonUtil.clone(cloneMap, User.class));
        System.out.println(JacksonUtil.clone(cloneMap, new TypeReference<Map<String, Object>>() {
        }));
    }
    
    @GetMapping("/17")
    public void test17() {
        List<User> userList = getUserList();
        List<Map<String, Object>> x = JacksonUtil.convertList(userList, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(x);
        List<User> users = JacksonUtil.convertList(x, User.class);
        System.out.println(users);
    }

    @GetMapping("/18")
    public void test18() {
        User user = getUser();
        User targetUser = new User();
        User update = JacksonUtil.update(JacksonUtil.toJsonString(user), targetUser);
        System.out.println(user);
        System.out.println(targetUser);
        System.out.println(update);
    }

    @GetMapping("/19")
    public void test19() {
        List<User> userList = getUserList();
        List<User> targetList = new ArrayList<>();
        List<User> updateList = JacksonUtil.update(JacksonUtil.toJsonString(userList), targetList);
        System.out.println(userList);
        System.out.println(targetList);
        System.out.println(updateList);
    }

    @GetMapping("/20")
    public void test20() {
        String jsonString = JacksonUtil.toJsonString(getUser());
        JsonNode jsonNode = JacksonUtil.parseTree(jsonString);
        System.out.println(jsonString);
        System.out.println(jsonNode);
    }

    @GetMapping("/21")
    public void test21() {
        System.out.println(JacksonUtil.isValidJson("{}"));
        System.out.println(JacksonUtil.isValidJson("{"));
        System.out.println(JacksonUtil.isValidJson(""));
        System.out.println(JacksonUtil.isValidJson(null));
    }


}
