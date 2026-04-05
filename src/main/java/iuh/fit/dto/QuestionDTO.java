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
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @JsonProperty("subject_id")
    private String subjectId;

    private String content;
    private List<String> options;
    private String difficulty;
}