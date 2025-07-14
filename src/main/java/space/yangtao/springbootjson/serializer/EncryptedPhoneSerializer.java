package space.yangtao.springbootjson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import space.yangtao.springbootjson.domain.EncryptedPhone;

import java.io.IOException;

/**
 * @author yangtao
 * @since 2025/7/13 18:03
 */
public class EncryptedPhoneSerializer extends JsonSerializer<EncryptedPhone> {

    @Override
    public void serialize(EncryptedPhone value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || value.getOriginalNum() == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.getOriginalNum().replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1****$3"));
    }
}
