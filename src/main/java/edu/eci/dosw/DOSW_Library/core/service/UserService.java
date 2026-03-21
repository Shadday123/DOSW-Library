package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {

    private List<User> users;

    public UserService() {
        this.users = new ArrayList<>();
    }

    public User registerUser(String name) {
        ValidationUtil.validateNotEmpty(name, "Nombre del usuario");

        User user = new User();
        user.setId(IdGeneratorUtil.generateUserId());
        user.setName(name);

        users.add(user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(String userId) {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(userId, true));
    }
}
