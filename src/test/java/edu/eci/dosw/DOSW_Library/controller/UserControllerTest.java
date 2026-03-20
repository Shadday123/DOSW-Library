package edu.eci.dosw.DOSW_Library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Pruebas unitarias de UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    @DisplayName("POST /api/users - Nombre vacío propaga excepción del servicio")
    void createUser_emptyName_propagatesException() throws Exception {
        UserDTO emptyDTO = new UserDTO();
        emptyDTO.setName("");
        when(userService.registerUser(""))
                .thenThrow(new IllegalArgumentException("El nombre no puede ser vacío"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDTO))));
    }

    @Test
    @DisplayName("POST /api/users - Nombre nulo propaga excepción del servicio")
    void createUser_nullName_propagatesException() throws Exception {
        UserDTO nullNameDTO = new UserDTO();
        nullNameDTO.setName(null);
        when(userService.registerUser(null))
                .thenThrow(new IllegalArgumentException("El nombre no puede ser nulo"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullNameDTO))));
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
    @DisplayName("GET /api/users/{userId} - Usuario no encontrado propaga excepción")
    void getUserById_notFound_propagatesException() {
        when(userService.getUserById("USR_inexistente"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado: USR_inexistente"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/users/USR_inexistente")));
    }

    // --- PUT /api/users/{userId} ---

    @Test
    @DisplayName("PUT /api/users/{userId} - Actualiza nombre de usuario con 200")
    void updateUser_success_returns200() throws Exception {
        User updatedUser = new User();
        updatedUser.setId("USR_test01");
        updatedUser.setName("Juan Actualizado");

        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Juan Actualizado");

        when(userService.updateUser("USR_test01", "Juan Actualizado")).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/USR_test01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("USR_test01"))
                .andExpect(jsonPath("$.name").value("Juan Actualizado"));

        verify(userService).updateUser("USR_test01", "Juan Actualizado");
    }

    @Test
    @DisplayName("PUT /api/users/{userId} - Usuario no encontrado propaga excepción")
    void updateUser_notFound_propagatesException() throws Exception {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Nombre Nuevo");

        when(userService.updateUser("USR_inexistente", "Nombre Nuevo"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/api/users/USR_inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))));
    }

    // --- DELETE /api/users/{userId} ---

    @Test
    @DisplayName("DELETE /api/users/{userId} - Elimina usuario con 204")
    void deleteUser_success_returns204() throws Exception {
        when(userService.deleteUser("USR_test01")).thenReturn(true);

        mockMvc.perform(delete("/api/users/USR_test01"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("USR_test01");
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - Usuario no encontrado propaga excepción")
    void deleteUser_notFound_propagatesException() {
        when(userService.deleteUser("USR_inexistente"))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(delete("/api/users/USR_inexistente")));
    }

    // --- GET /api/users/stats/total ---

    @Test
    @DisplayName("GET /api/users/stats/total - Retorna total de usuarios registrados")
    void getTotalUsers_returnsCount() throws Exception {
        when(userService.getTotalUsers()).thenReturn(10);

        mockMvc.perform(get("/api/users/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    @DisplayName("GET /api/users/stats/total - Sin usuarios retorna 0")
    void getTotalUsers_noUsers_returnsZero() throws Exception {
        when(userService.getTotalUsers()).thenReturn(0);

        mockMvc.perform(get("/api/users/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
