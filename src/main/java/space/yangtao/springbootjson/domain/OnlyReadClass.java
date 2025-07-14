package space.yangtao.springbootjson.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yangtao
 * @since 2025/7/14 16:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OnlyReadClass {

    private Long id;

    private String name;

}
