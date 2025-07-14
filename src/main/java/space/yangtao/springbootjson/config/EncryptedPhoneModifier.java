package space.yangtao.springbootjson.config;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import space.yangtao.springbootjson.domain.EncryptedPhone;
import space.yangtao.springbootjson.serializer.EncryptedPhoneSerializer;

import java.util.List;

/**
 * @author yangtao
 * @since 2025/7/14 17:41
 */
public class EncryptedPhoneModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            if (writer.getType().getRawClass() == EncryptedPhone.class) {
                EncryptedPhoneSerializer encryptedPhoneSerializer = new EncryptedPhoneSerializer();
                writer.assignSerializer((JsonSerializer) encryptedPhoneSerializer);
            }
        }
        return beanProperties;
    }
}
