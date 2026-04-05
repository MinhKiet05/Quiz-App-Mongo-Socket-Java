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
public class Submission {
    @BsonId
    private String id;

    @BsonProperty("quiz_id")
    private String quizId;

    @BsonProperty("candidate_id")
    private String candidateId;

    @BsonProperty("start_time")
    private String startTime;

    @BsonProperty("submit_time")
    private String submitTime;

    private double score;
    private List<SubmissionDetail> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmissionDetail {
        @BsonProperty("question_id")
        private String questionId;

        @BsonProperty("selected_option")
        private String selectedOption;

        @BsonProperty("is_correct")
        private boolean isCorrect;
    }
}
