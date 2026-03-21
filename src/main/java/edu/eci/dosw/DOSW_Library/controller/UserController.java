package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios de la biblioteca")
public class UserController {

    private IUserService userService;

    @Autowired
    @Required
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userService.registerUser(userDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.modelToDTO(user));
    }


    @GetMapping
    @Operation(summary = "Obtener todos los usuarios",
            description = "Retorna una lista completa de todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }


    @GetMapping("/{userId}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Retorna los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID del usuario", required = true, example = "USR_abc123")
            @PathVariable String userId) throws Exception {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(UserDTO.modelToDTO(user));
    }


    @PutMapping("/{userId}")
    @Operation(summary = "Actualizar usuario",
            description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "USR_abc123")
            @PathVariable String userId,
            @RequestBody UserDTO userDTO) throws Exception {
        User user = userService.updateUser(userId, userDTO.getName());
        return ResponseEntity.ok(UserDTO.modelToDTO(user));
    }


    @DeleteMapping("/{userId}")
    @Operation(summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "USR_abc123")
            @PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/stats/total")
    @Operation(summary = "Obtener total de usuarios",
            description = "Retorna el número total de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Total de usuarios obtenido")
    public ResponseEntity<Integer> getTotalUsers() {
        return ResponseEntity.ok(userService.getTotalUsers());
    }
}