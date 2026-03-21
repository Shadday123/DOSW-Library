package edu.eci.dosw.DOSW_Library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Pruebas unitarias de UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("USR_test01");
        testUser.setName("Juan Pérez");

        testUserDTO = new UserDTO();
        testUserDTO.setName("Juan Pérez");
    }

    // --- POST /api/users ---

    @Test
    @DisplayName("POST /api/users - Crea usuario exitosamente y retorna 201")
    void createUser_success_returns201() throws Exception {
        when(userService.registerUser("Juan Pérez")).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("USR_test01"))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));

        verify(userService).registerUser("Juan Pérez");
    }

    @Test
    @DisplayName("POST /api/users - Nombre vacío retorna 400")
    void createUser_emptyName_returns400() throws Exception {
        UserDTO emptyDTO = new UserDTO();
        emptyDTO.setName("");
        when(userService.registerUser(""))
                .thenThrow(new IllegalArgumentException("El nombre no puede ser vacío"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El nombre no puede ser vacío"));
    }

    // --- GET /api/users ---

    @Test
    @DisplayName("GET /api/users - Retorna lista de usuarios con 200")
    void getAllUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("USR_test01"))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"));
    }

    @Test
    @DisplayName("GET /api/users - Lista vacía retorna 200 con array vacío")
    void getAllUsers_emptyList_returnsEmpty() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/users - Retorna múltiples usuarios")
    void getAllUsers_multipleUsers_returnsAll() throws Exception {
        User secondUser = new User();
        secondUser.setId("USR_test02");
        secondUser.setName("María López");

        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser, secondUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].name").value("María López"));
    }

    // --- GET /api/users/{userId} ---

    @Test
    @DisplayName("GET /api/users/{userId} - Retorna usuario por ID con 200")
    void getUserById_found_returns200() throws Exception {
        when(userService.getUserById("USR_test01")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/USR_test01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("USR_test01"))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));
    }

    @Test
    @DisplayName("GET /api/users/{userId} - Usuario no encontrado retorna 404")
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getUserById("USR_inexistente"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado: USR_inexistente"));

        mockMvc.perform(get("/api/users/USR_inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado: USR_inexistente"));
    }
}
