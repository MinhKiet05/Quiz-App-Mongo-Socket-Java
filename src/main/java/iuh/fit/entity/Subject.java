package iuh.fit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @BsonId
    private String id;
    private String name;

    @BsonProperty("course_code")
    private String courseCode;

    private String description;
}