package edu.eci.dosw.DOSW_Library.dto;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de UserDTO")
class UserDTOTest {

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("USR_test01");
        testUser.setName("Juan Pérez");

        testUserDTO = new UserDTO();
        testUserDTO.setId("USR_test01");
        testUserDTO.setName("Juan Pérez");
    }

    // --- modelToDTO ---

    @Test
    @DisplayName("modelToDTO - Convierte User a UserDTO correctamente")
    void modelToDTO_validUser_returnsCorrectDTO() {
        UserDTO result = UserDTO.modelToDTO(testUser);

        assertNotNull(result);
        assertEquals("USR_test01", result.getId());
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("modelToDTO - User nulo retorna null")
    void modelToDTO_nullUser_returnsNull() {
        UserDTO result = UserDTO.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Copia campos nulos como null")
    void modelToDTO_userWithNullFields_copiesNullFields() {
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setName(null);

        UserDTO result = UserDTO.modelToDTO(userWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }

    @Test
    @DisplayName("modelToDTO - Preserva nombre con caracteres especiales")
    void modelToDTO_nameWithSpecialChars_preservesName() {
        testUser.setName("María José Ñoño");

        UserDTO result = UserDTO.modelToDTO(testUser);

        assertEquals("María José Ñoño", result.getName());
    }

    // --- dtoToModel ---

    @Test
    @DisplayName("dtoToModel - Convierte UserDTO a User correctamente")
    void dtoToModel_validDTO_returnsCorrectUser() {
        User result = UserDTO.dtoToModel(testUserDTO);

        assertNotNull(result);
        assertEquals("USR_test01", result.getId());
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("dtoToModel - UserDTO nulo retorna null")
    void dtoToModel_nullDTO_returnsNull() {
        User result = UserDTO.dtoToModel(null);

        assertNull(result);
    }

    @Test
    @DisplayName("dtoToModel - Copia campos nulos del DTO")
    void dtoToModel_dtoWithNullFields_copiesNullFields() {
        UserDTO dtoWithNulls = new UserDTO();
        dtoWithNulls.setId(null);
        dtoWithNulls.setName(null);

        User result = UserDTO.dtoToModel(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }

    // --- Conversión de ida y vuelta ---

    @Test
    @DisplayName("modelToDTO -> dtoToModel - Conversión de ida y vuelta preserva datos")
    void roundTrip_modelToDTOThenBack_preservesData() {
        UserDTO dto = UserDTO.modelToDTO(testUser);
        User result = UserDTO.dtoToModel(dto);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
    }

    @Test
    @DisplayName("dtoToModel -> modelToDTO - Conversión inversa preserva datos")
    void roundTrip_dtoToModelThenBack_preservesData() {
        User user = UserDTO.dtoToModel(testUserDTO);
        UserDTO result = UserDTO.modelToDTO(user);

        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getName(), result.getName());
    }

    // --- Constructor y getters/setters (Lombok @Data) ---

    @Test
    @DisplayName("Constructor sin argumentos crea objeto vacío")
    void noArgsConstructor_createsEmptyObject() {
        UserDTO dto = new UserDTO();

        assertNull(dto.getId());
        assertNull(dto.getName());
    }

    @Test
    @DisplayName("Constructor con todos los argumentos inicializa correctamente")
    void allArgsConstructor_initializesCorrectly() {
        UserDTO dto = new UserDTO("Juan Pérez", "USR_001");

        assertEquals("Juan Pérez", dto.getName());
        assertEquals("USR_001", dto.getId());
    }

    @Test
    @DisplayName("Setters actualizan los campos correctamente")
    void setters_updateFieldsCorrectly() {
        UserDTO dto = new UserDTO();
        dto.setName("Nuevo Nombre");
        dto.setId("USR_nuevo");

        assertEquals("Nuevo Nombre", dto.getName());
        assertEquals("USR_nuevo", dto.getId());
    }

    @Test
    @DisplayName("Equals - Dos DTOs con mismos datos son iguales")
    void equals_sameDatas_areEqual() {
        UserDTO dto1 = new UserDTO("Juan Pérez", "USR_test01");
        UserDTO dto2 = new UserDTO("Juan Pérez", "USR_test01");

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Equals - DTOs con datos distintos no son iguales")
    void equals_differentData_areNotEqual() {
        UserDTO dto1 = new UserDTO("Juan Pérez", "USR_test01");
        UserDTO dto2 = new UserDTO("María López", "USR_test02");

        assertNotEquals(dto1, dto2);
    }
}
