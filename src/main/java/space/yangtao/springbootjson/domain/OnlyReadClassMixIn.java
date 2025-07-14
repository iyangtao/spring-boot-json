package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author yangtao
 * @since 2025/7/14 16:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OnlyReadClassMixIn {

    @JsonProperty("or_id")
    private Long id;

    @JsonProperty("or_name")
    private String name;

}
