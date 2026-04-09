package edu.eci.dosw.DOSW_Library.controller.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}
