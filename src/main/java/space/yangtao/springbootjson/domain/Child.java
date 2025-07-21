package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author yangtao
 * @since 25/07/14 22:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Child {

    private String name;

    @JsonBackReference     // 反向引用，序列化时忽略
    private Parent parent;
}
