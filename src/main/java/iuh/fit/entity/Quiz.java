package iuh.fit.entity;

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
public class Quiz {
    @BsonId
    private String id;

    @BsonProperty("subject_id")
    private String subjectId;

    private String title;

    @BsonProperty("duration_minutes")
    private int durationMinutes;

    @BsonProperty("open_time")
    private String openTime;

    @BsonProperty("close_time")
    private String closeTime;

    private String status;

    @BsonProperty("created_by")
    private String createdBy;

    @BsonProperty("question_ids")
    private List<String> questionIds;
}
