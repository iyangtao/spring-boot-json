package space.yangtao.springbootjson.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
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


}
