package iuh.fit.network;


import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Request implements Serializable { // Thêm implements ở đây
    private static final long serialVersionUID = 1L;
    private CommandType commandType;
    private Object object;
}
