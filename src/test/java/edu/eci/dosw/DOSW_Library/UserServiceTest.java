package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de UserService")
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ==================== registerUser ====================

    @Test
    @DisplayName("registerUser: debe registrar un usuario exitosamente")
    void registerUser_success() {
        User user = userService.registerUser("Juan Pérez");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertTrue(user.getId().startsWith("USR_"));
        assertEquals("Juan Pérez", user.getName());
    }

    @Test
    @DisplayName("registerUser: cada usuario debe tener un ID único")
    void registerUser_uniqueIds() {
        User user1 = userService.registerUser("Usuario Uno");
        User user2 = userService.registerUser("Usuario Dos");

        assertNotEquals(user1.getId(), user2.getId());
    }

    @Test
    @DisplayName("registerUser: debe lanzar excepción si el nombre está vacío")
    void registerUser_emptyName_throwsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser("")
        );
        assertTrue(ex.getMessage().contains("Nombre del usuario"));
    }

    @Test
    @DisplayName("registerUser: debe lanzar excepción si el nombre es nulo")
    void registerUser_nullName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(null));
    }

    @Test
    @DisplayName("registerUser: debe lanzar excepción si el nombre es solo espacios")
    void registerUser_blankName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser("   "));
    }

    // ==================== getAllUsers ====================

    @Test
    @DisplayName("getAllUsers: debe retornar lista vacía cuando no hay usuarios")
    void getAllUsers_empty_returnsEmptyList() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("getAllUsers: debe retornar todos los usuarios registrados")
    void getAllUsers_withUsers_returnsAll() {
        userService.registerUser("Usuario A");
        userService.registerUser("Usuario B");
        userService.registerUser("Usuario C");

        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size());
    }

    // ==================== getUserById ====================

    @Test
    @DisplayName("getUserById: debe retornar el usuario correcto")
    void getUserById_exists_returnsUser() {
        User registered = userService.registerUser("Juan Pérez");
        User found = userService.getUserById(registered.getId());

        assertNotNull(found);
        assertEquals(registered.getId(), found.getId());
        assertEquals("Juan Pérez", found.getName());
    }

    @Test
    @DisplayName("getUserById: debe lanzar UserNotFoundException si el usuario no existe")
    void getUserById_notFound_throwsUserNotFoundException() {
        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById("USR_inexistente")
        );
        assertTrue(ex.getMessage().contains("USR_inexistente"));
    }

    @Test
    @DisplayName("getUserById: debe lanzar excepción si el ID es vacío")
    void getUserById_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(""));
    }

    @Test
    @DisplayName("getUserById: debe lanzar excepción si el ID es nulo")
    void getUserById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(null));
    }
}
