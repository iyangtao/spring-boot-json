package space.yangtao.springbootjson.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yangtao
 * @since 2025/7/14 15:29
 */
public class Student {

    private Long id;
    private String name;

//    @JsonCreator
//    public Student(@JsonProperty("id") Long id,
//                   @JsonProperty("name") String name) {
//        this.id = id;
//        this.name =  "姓名：" + name;
//    }

    @JsonCreator
    public static Student create(@JsonProperty("id") Long id,
                                             @JsonProperty("name") String name) {
        Student student = new Student();
        student.id = id;
        student.name = name;
        return student;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
