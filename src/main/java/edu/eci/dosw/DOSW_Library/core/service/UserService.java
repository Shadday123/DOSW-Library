package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import edu.eci.dosw.DOSW_Library.persistence.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String name, String email, String password) {
        ValidationUtil.validateNotEmpty(name, "Nombre del usuario");
        ValidationUtil.validateNotEmpty(email, "Email del usuario");
        ValidationUtil.validateNotEmpty(password, "Contraseña del usuario");

        User user = new User();
        user.setId(IdGeneratorUtil.generateUserId());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        ValidationUtil.validateNotEmpty(userId, "ID del usuario");

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId, true));
    }
}
