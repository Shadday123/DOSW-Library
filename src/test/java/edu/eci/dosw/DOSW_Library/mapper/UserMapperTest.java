package edu.eci.dosw.DOSW_Library.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.UserMapper;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de UserMapper")
class UserMapperTest {

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
    @DisplayName("modelToDTO - Convierte User a UserDTO con todos los campos")
    void modelToDTO_validUser_mapsAllFields() {
        UserDTO result = UserMapper.modelToDTO(testUser);

        assertNotNull(result);
        assertEquals("USR_test01", result.getId());
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("modelToDTO - User nulo retorna null")
    void modelToDTO_nullUser_returnsNull() {
        UserDTO result = UserMapper.modelToDTO(null);

        assertNull(result);
    }

    @Test
    @DisplayName("modelToDTO - Mapea usuario con campos nulos")
    void modelToDTO_nullFields_mapsAsNull() {
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setName(null);

        UserDTO result = UserMapper.modelToDTO(userWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }

    @Test
    @DisplayName("modelToDTO - Preserva nombre con caracteres especiales y tildes")
    void modelToDTO_specialCharsInName_preserves() {
        testUser.setName("María José Ñoño-Hernández");

        UserDTO result = UserMapper.modelToDTO(testUser);

        assertEquals("María José Ñoño-Hernández", result.getName());
    }

    @Test
    @DisplayName("modelToDTO - Crea una nueva instancia de UserDTO")
    void modelToDTO_returnsNewInstance() {
        UserDTO result1 = UserMapper.modelToDTO(testUser);
        UserDTO result2 = UserMapper.modelToDTO(testUser);

        assertNotSame(result1, result2);
    }

    @Test
    @DisplayName("modelToDTO - ID con prefijo USR_ se mapea correctamente")
    void modelToDTO_userIdWithPrefix_mapsCorrectly() {
        testUser.setId("USR_abc123def456");

        UserDTO result = UserMapper.modelToDTO(testUser);

        assertEquals("USR_abc123def456", result.getId());
    }

    // --- dtoToModel ---

    @Test
    @DisplayName("dtoToModel - Convierte UserDTO a User con todos los campos")
    void dtoToModel_validDTO_mapsAllFields() {
        User result = UserMapper.dtoToModel(testUserDTO);

        assertNotNull(result);
        assertEquals("USR_test01", result.getId());
        assertEquals("Juan Pérez", result.getName());
    }

    @Test
    @DisplayName("dtoToModel - UserDTO nulo retorna null")
    void dtoToModel_nullDTO_returnsNull() {
        User result = UserMapper.dtoToModel(null);

        assertNull(result);
    }

    @Test
    @DisplayName("dtoToModel - Mapea DTO con campos nulos")
    void dtoToModel_nullFields_mapsAsNull() {
        UserDTO dtoWithNulls = new UserDTO();
        dtoWithNulls.setId(null);
        dtoWithNulls.setName(null);

        User result = UserMapper.dtoToModel(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }

    @Test
    @DisplayName("dtoToModel - Crea una nueva instancia de User")
    void dtoToModel_returnsNewInstance() {
        User result1 = UserMapper.dtoToModel(testUserDTO);
        User result2 = UserMapper.dtoToModel(testUserDTO);

        assertNotSame(result1, result2);
    }

    // --- Conversión de ida y vuelta ---

    @Test
    @DisplayName("modelToDTO -> dtoToModel - Ida y vuelta preserva todos los datos")
    void roundTrip_modelToDTOAndBack_preservesAllData() {
        UserDTO dto = UserMapper.modelToDTO(testUser);
        User result = UserMapper.dtoToModel(dto);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
    }

    @Test
    @DisplayName("dtoToModel -> modelToDTO - Vuelta y ida preserva todos los datos")
    void roundTrip_dtoToModelAndBack_preservesAllData() {
        User user = UserMapper.dtoToModel(testUserDTO);
        UserDTO result = UserMapper.modelToDTO(user);

        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getName(), result.getName());
    }

    @Test
    @DisplayName("modelToDTO - Nombre de un solo carácter se mapea correctamente")
    void modelToDTO_singleCharName_mapsCorrectly() {
        testUser.setName("A");

        UserDTO result = UserMapper.modelToDTO(testUser);

        assertEquals("A", result.getName());
    }

    @Test
    @DisplayName("dtoToModel - Nombre largo se mapea correctamente")
    void dtoToModel_longName_mapsCorrectly() {
        String longName = "Juan Antonio Carlos Sebastián del Monte Pérez García";
        testUserDTO.setName(longName);

        User result = UserMapper.dtoToModel(testUserDTO);

        assertEquals(longName, result.getName());
    }
}
