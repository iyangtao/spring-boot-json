package space.yangtao.springbootjson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author yangtao
 * @since 2025/7/13 18:03
 */
public class PhoneNumberSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1****$3"));
    }
}
