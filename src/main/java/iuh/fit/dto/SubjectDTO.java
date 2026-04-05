package iuh.fit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    @JsonProperty("course_code")
    private String courseCode;

    private String description;
}