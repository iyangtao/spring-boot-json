package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangtao
 * @since 2025/7/14 16:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Config {

    private String name;

    private Map<String, Object> settings = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getSettings() {
        return settings;
    }

}
