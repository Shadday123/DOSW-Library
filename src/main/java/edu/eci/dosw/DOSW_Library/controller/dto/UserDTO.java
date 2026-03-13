package edu.eci.dosw.DOSW_Library.controller.dto;

import edu.eci.dosw.DOSW_Library.core.model.User;
import lombok.Data;
public class UserDTO {
    private String name;
    private String id;

    public static UserDTO ModelToDTO(User user){
        UserDTO userDTO = new UserDTO();
        String id = user.get
    }
}
