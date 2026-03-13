package edu.eci.dosw.DOSW_Library.controller;


import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;;

@Data
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping(/user)
    public getUsers(){


    }
}
