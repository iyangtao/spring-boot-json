package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import space.yangtao.springbootjson.GenderDeserializer;
import space.yangtao.springbootjson.config.Views;
import space.yangtao.springbootjson.serializer.PhoneNumberSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User实体类
 *
 * @author yangtao
 * @since 2025/7/13 14:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("userFilter")
public class User {

    @JsonProperty("id")
    @JsonAlias({"id", "userId", "user_id"})
    private Long id;
    private String name;
    private Integer age;
    private BigDecimal balance;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date birthday;

    @JsonDeserialize(using = GenderDeserializer.class)
    private Gender gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private BigDecimal unsafeAmount;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal safeAmount;

    private char[] chars;

    @JsonIgnore
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonView(Views.Internal.class)
    private boolean active;

    @JsonSerialize(using = PhoneNumberSerializer.class)
    private String phoneNumber;

    @JsonView(Views.Public.class)
    public String publicField;

    @JsonView(Views.Internal.class)
    public String internalField;

    public EncryptedPhone phone;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @Getter
    @AllArgsConstructor
    public enum Gender {
        MALE("male", "男"),
        FEMALE("female", "女"),
        UNKNOWN("unknown", "未知");

        private final String value;
        private final String description;

        // ObjectMapper添加WRITE_ENUMS_USING_TO_STRING特性后，会使用toString方法序列化枚举值
        @Override
        public String toString() {
            return value;
        }

        // 反序列化时使用的工厂方法
        // @JsonCreator
        public static Gender fromValue(String value) {
            for (Gender gender : values()) {
                if (gender.value.equals(value)) {
                    return gender;
                }
            }
            return UNKNOWN; // 如果没有匹配的值，返回默认值
        }
    }

}
