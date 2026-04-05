package iuh.fit.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @BsonId
    private String id;
    private String username;
    private String password;
    private String role;
    private String status;
}