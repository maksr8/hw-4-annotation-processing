package org.example.dto;

import org.example.annotation.GenerateDtoValidator;
import org.example.annotation.MaxLength;
import org.example.annotation.NotEmpty;

@GenerateDtoValidator
public class UserDto {
    @NotEmpty
    private String username;

    @NotEmpty
    @MaxLength(50)
    private String email;

    public UserDto() {
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
