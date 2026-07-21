package br.com.techchallenge.techchallenge.entities;

import br.com.techchallenge.techchallenge.dtos.UserRequestDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class User {

    private Long id;
    private String name;
    private String email;
    private String userLogin;
    private String password;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastModifiedDate;
    private String address;
    private String userType;

    public User(UserRequestDTO dto) {
        this.name = dto.name();
        this.email = dto.email();
        this.userLogin = dto.userLogin();
        this.password = dto.password();
        this.address = dto.address();
        this.userType = dto.userType();
    }

}