package iuh.fit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @JsonProperty("subject_id")
    private String subjectId;

    private String title;

    @JsonProperty("duration_minutes")
    private int durationMinutes;

    @JsonProperty("open_time")
    private String openTime;

    @JsonProperty("close_time")
    private String closeTime;

    private String status;

    @JsonProperty("question_ids")
    private List<String> questionIds;

    private List<QuestionDTO> questions;
}