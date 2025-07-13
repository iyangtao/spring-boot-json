package space.yangtao.springbootjson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import space.yangtao.springbootjson.domain.User;

import java.io.IOException;

/**
 * @author yangtao
 * @since 2025/7/13 18:10
 */
public class GenderDeserializer extends JsonDeserializer<User.Gender> {
    @Override
    public User.Gender deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return User.Gender.UNKNOWN;
        }
        switch (value) {
            case "男":
                return User.Gender.MALE;
            case  "女":
                return User.Gender.FEMALE;
            default:
                return User.Gender.UNKNOWN;
        }
    }
}
