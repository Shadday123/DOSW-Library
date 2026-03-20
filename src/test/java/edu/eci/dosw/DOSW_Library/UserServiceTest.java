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

    @Test
    @DisplayName("getAllUsers: debe retornar una copia defensiva de la lista")
    void getAllUsers_returnsDefensiveCopy() {
        userService.registerUser("Usuario A");
        List<User> users = userService.getAllUsers();
        users.clear();

        assertEquals(1, userService.getTotalUsers());
    }


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


    @Test
    @DisplayName("updateUser: debe actualizar el nombre del usuario exitosamente")
    void updateUser_success() {
        User user = userService.registerUser("Nombre Original");
        User updated = userService.updateUser(user.getId(), "Nombre Actualizado");

        assertEquals("Nombre Actualizado", updated.getName());
        assertEquals(user.getId(), updated.getId());
    }

    @Test
    @DisplayName("updateUser: debe lanzar UserNotFoundException si el usuario no existe")
    void updateUser_notFound_throwsUserNotFoundException() {
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser("USR_inexistente", "Nuevo Nombre"));
    }

    @Test
    @DisplayName("updateUser: debe lanzar excepción si el nuevo nombre está vacío")
    void updateUser_emptyName_throwsException() {
        User user = userService.registerUser("Nombre Original");

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(user.getId(), ""));
    }

    @Test
    @DisplayName("updateUser: debe lanzar excepción si el ID es vacío")
    void updateUser_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser("", "Nuevo Nombre"));
    }


    @Test
    @DisplayName("deleteUser: debe eliminar el usuario y retornar true")
    void deleteUser_exists_returnsTrue() {
        User user = userService.registerUser("Usuario a eliminar");
        boolean deleted = userService.deleteUser(user.getId());

        assertTrue(deleted);
        assertEquals(0, userService.getTotalUsers());
    }

    @Test
    @DisplayName("deleteUser: debe retornar false si el usuario no existe")
    void deleteUser_notFound_returnsFalse() {
        boolean deleted = userService.deleteUser("USR_inexistente");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("deleteUser: debe lanzar excepción si el ID es vacío")
    void deleteUser_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(""));
    }

    @Test
    @DisplayName("userExists: debe retornar true si el usuario existe")
    void userExists_exists_returnsTrue() {
        User user = userService.registerUser("Usuario Existente");
        assertTrue(userService.userExists(user.getId()));
    }

    @Test
    @DisplayName("userExists: debe retornar false si el usuario no existe")
    void userExists_notFound_returnsFalse() {
        assertFalse(userService.userExists("USR_inexistente"));
    }


    @Test
    @DisplayName("getTotalUsers: debe retornar cero cuando no hay usuarios")
    void getTotalUsers_empty_returnsZero() {
        assertEquals(0, userService.getTotalUsers());
    }

    @Test
    @DisplayName("getTotalUsers: debe contar correctamente los usuarios")
    void getTotalUsers_withUsers_returnsCorrectCount() {
        userService.registerUser("Usuario A");
        userService.registerUser("Usuario B");

        assertEquals(2, userService.getTotalUsers());
    }

    @Test
    @DisplayName("getTotalUsers: debe decrementar después de eliminar un usuario")
    void getTotalUsers_afterDelete_decrements() {
        User user = userService.registerUser("Usuario A");
        userService.registerUser("Usuario B");
        userService.deleteUser(user.getId());

        assertEquals(1, userService.getTotalUsers());
    }
}