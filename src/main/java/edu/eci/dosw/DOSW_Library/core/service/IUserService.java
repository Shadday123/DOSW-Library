package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.User;

import java.util.List;

public interface IUserService {

    User registerUser(String name);

    List<User> getAllUsers();

    User getUserById(String userId);
}
