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
public class SubmissionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @JsonProperty("quiz_id")
    private String quizId;

    @JsonProperty("candidate_id")
    private String candidateId;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("submit_time")
    private String submitTime;

    private double score;
    private List<SubmissionDetailDTO> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmissionDetailDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("question_id")
        private String questionId;

        @JsonProperty("selected_option")
        private String selectedOption;

        @JsonProperty("is_correct")
        private boolean correct;
        
        // Alias methods for compatibility
        public boolean isCorrect() {
            return correct;
        }
        
        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
    }
}