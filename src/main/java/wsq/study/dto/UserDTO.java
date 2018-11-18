package wsq.study.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable{
    private static final long serialVersionUID = 4482530102153950585L;
    private String username;
    private String password;
    private String role;
    private String sex;
    private String conment;
}
