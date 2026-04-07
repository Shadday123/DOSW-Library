package edu.eci.dosw.DOSW_Library.persistence;

import edu.eci.dosw.DOSW_Library.core.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    List<User> findAll();

    void delete(String id);
}
