package iuh.fit.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @BsonId
    private String id;

    @BsonProperty("subject_id")
    @JsonProperty("subject_id")
    private String subjectId;

    private String content;
    private List<String> options;

    @BsonProperty("correct_answer")
    @JsonProperty("correct_answer")
    private String correctAnswer;

    private String difficulty;

    @BsonProperty("created_by")
    @JsonProperty("created_by")
    private String createdBy;
}