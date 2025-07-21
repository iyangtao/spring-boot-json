package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Parent {

    private String name;

    @JsonManagedReference  // 主动序列化方
    private Child child;
}
