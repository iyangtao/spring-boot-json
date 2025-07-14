package space.yangtao.springbootjson.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 加密手机号类
 *
 * @author yangtao
 * @since 2025/7/14 17:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class EncryptedPhone {

    private String originalNum;

}
