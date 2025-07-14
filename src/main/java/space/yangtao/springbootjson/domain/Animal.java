package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author yangtao
 * @since 2025/7/14 11:59
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Animal.Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Animal.Cat.class, name = "cat")
})
public class Animal {

    public static class Dog extends Animal {
        public int boneCount;
    }

    public static class Cat extends Animal {
        public boolean lazy;
    }

}
