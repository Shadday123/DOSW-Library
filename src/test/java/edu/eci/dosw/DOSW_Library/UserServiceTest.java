package edu.eci.dosw.DOSW_Library;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
import edu.eci.dosw.DOSW_Library.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Mockito handles injection
    }

    // ==================== registerUser ====================

    @Test
    @DisplayName("registerUser: debe registrar un usuario exitosamente")
    void registerUser_success() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser("Juan Pérez");

        assertNotNull(user);
        assertNotNull(user.getId());
        assertTrue(user.getId().startsWith("USR_"));
        assertEquals("Juan Pérez", user.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser: cada usuario debe tener un ID único")
    void registerUser_uniqueIds() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("getAllUsers: debe retornar todos los usuarios registrados")
    void getAllUsers_withUsers_returnsAll() {
        List<User> mockUsers = new ArrayList<>();
        User u1 = new User(); u1.setId("USR_1"); u1.setName("Usuario A");
        User u2 = new User(); u2.setId("USR_2"); u2.setName("Usuario B");
        User u3 = new User(); u3.setId("USR_3"); u3.setName("Usuario C");
        mockUsers.add(u1);
        mockUsers.add(u2);
        mockUsers.add(u3);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size());
    }

    // ==================== getUserById ====================

    @Test
    @DisplayName("getUserById: debe retornar el usuario correcto")
    void getUserById_exists_returnsUser() {
        User mockUser = new User();
        mockUser.setId("USR_test01");
        mockUser.setName("Juan Pérez");
        when(userRepository.findById("USR_test01")).thenReturn(Optional.of(mockUser));

        User found = userService.getUserById("USR_test01");

        assertNotNull(found);
        assertEquals("USR_test01", found.getId());
        assertEquals("Juan Pérez", found.getName());
    }

    @Test
    @DisplayName("getUserById: debe lanzar UserNotFoundException si el usuario no existe")
    void getUserById_notFound_throwsUserNotFoundException() {
        when(userRepository.findById("USR_inexistente")).thenReturn(Optional.empty());

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
