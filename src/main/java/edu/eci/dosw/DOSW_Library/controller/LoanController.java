package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de préstamos
 */
@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "API para gestión de préstamos de libros")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /**
     * Crea un nuevo préstamo
     */
    @PostMapping
    @Operation(summary = "Crear nuevo préstamo",
            description = "Crea un préstamo de un libro a un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Préstamo creado"),
            @ApiResponse(responseCode = "400", description = "Límite excedido o datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado"),
            @ApiResponse(responseCode = "409", description = "Libro no disponible"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LoanDTO> createLoan(
            @Parameter(description = "ID del usuario", required = true, example = "USR_abc123")
            @RequestParam String userId,
            @Parameter(description = "ID del libro", required = true, example = "BK_xyz789")
            @RequestParam String bookId) throws Exception {
        Loan loan = loanService.createLoan(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanDTO.modelToDTO(loan));
    }

    /**
     * Devuelve un libro prestado
     */
    @PutMapping("/{loanId}/return")
    @Operation(summary = "Devolver libro",
            description = "Registra la devolución de un libro prestado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolución registrada"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Préstamo ya devuelto"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LoanDTO> returnLoan(
            @Parameter(description = "ID del préstamo", required = true, example = "LN_loan123")
            @PathVariable String loanId) {
        Loan loan = loanService.returnLoan(loanId);
        return ResponseEntity.ok(LoanDTO.modelToDTO(loan));
    }

    /**
     * Obtiene un préstamo por ID
     */
    @GetMapping("/{loanId}")
    @Operation(summary = "Obtener préstamo por ID",
            description = "Retorna los detalles de un préstamo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LoanDTO> getLoanById(
            @Parameter(description = "ID del préstamo", required = true, example = "LN_loan123")
            @PathVariable String loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(LoanDTO.modelToDTO(loan));
    }

    /**
     * Obtiene todos los préstamos
     */
    @GetMapping
    @Operation(summary = "Obtener todos los préstamos",
            description = "Retorna una lista de todos los préstamos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }

    /**
     * Obtiene préstamos activos de un usuario
     */
    @GetMapping("/user/{userId}/active")
    @Operation(summary = "Obtener préstamos activos",
            description = "Retorna los préstamos pendientes de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamos obtenidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<LoanDTO>> getActiveLoansForUser(
            @Parameter(description = "ID del usuario", required = true, example = "USR_abc123")
            @PathVariable String userId) throws Exception {
        List<Loan> loans = loanService.getActiveLoansForUser(userId);
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }

    /**
     * Obtiene todos los préstamos de un usuario
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener historial de usuario",
            description = "Retorna todos los préstamos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<LoanDTO>> getAllLoansForUser(
            @Parameter(description = "ID del usuario", required = true, example = "USR_abc123")
            @PathVariable String userId) throws Exception {
        List<Loan> loans = loanService.getAllLoansForUser(userId);
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }


    @GetMapping("/book/{bookId}")
    @Operation(summary = "Obtener préstamos de libro",
            description = "Retorna todos los préstamos de un libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamos obtenidos"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<LoanDTO>> getLoansForBook(
            @Parameter(description = "ID del libro", required = true, example = "BK_xyz789")
            @PathVariable String bookId) {
        List<Loan> loans = loanService.getLoansForBook(bookId);
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }


    @GetMapping("/overdue")
    @Operation(summary = "Obtener préstamos vencidos",
            description = "Retorna todos los préstamos no devueltos a tiempo")
    @ApiResponse(responseCode = "200", description = "Préstamos vencidos obtenidos")
    public ResponseEntity<List<LoanDTO>> getOverdueLoans() {
        List<Loan> loans = loanService.getOverdueLoans();
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }


    @GetMapping("/user/{userId}/overdue")
    @Operation(summary = "Obtener préstamos vencidos de usuario",
            description = "Retorna los préstamos vencidos de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamos vencidos obtenidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<LoanDTO>> getOverdueLoansForUser(
            @Parameter(description = "ID del usuario", required = true, example = "USR_abc123")
            @PathVariable String userId) throws Exception {
        List<Loan> loans = loanService.getOverdueLoansForUser(userId);
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }


    @GetMapping("/{loanId}/fine")
    @Operation(summary = "Calcular multa",
            description = "Calcula la multa (1000 pesos/día) por atraso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa calculada"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Long> calculateFine(
            @Parameter(description = "ID del préstamo", required = true, example = "LN_loan123")
            @PathVariable String loanId) {
        long fine = loanService.calculateFine(loanId);
        return ResponseEntity.ok(fine);
    }


    @PutMapping("/{loanId}/renew")
    @Operation(summary = "Renovar préstamo",
            description = "Extiende la fecha de devolución")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Préstamo renovado"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Préstamo ya devuelto o parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LoanDTO> renewLoan(
            @Parameter(description = "ID del préstamo", required = true, example = "LN_loan123")
            @PathVariable String loanId,
            @Parameter(description = "Días adicionales", required = true, example = "7")
            @RequestParam int additionalDays) {
        Loan loan = loanService.renewLoan(loanId, additionalDays);
        return ResponseEntity.ok(LoanDTO.modelToDTO(loan));
    }


    @GetMapping("/stats/active")
    @Operation(summary = "Total de préstamos activos",
            description = "Retorna el número de préstamos pendientes")
    @ApiResponse(responseCode = "200", description = "Total obtenido")
    public ResponseEntity<Integer> getTotalActiveLoans() {
        return ResponseEntity.ok(loanService.getTotalActiveLoans());
    }


    @GetMapping("/stats/completed")
    @Operation(summary = "Total de préstamos completados",
            description = "Retorna el número de préstamos devueltos")
    @ApiResponse(responseCode = "200", description = "Total obtenido")
    public ResponseEntity<Integer> getTotalCompletedLoans() {
        return ResponseEntity.ok(loanService.getTotalCompletedLoans());
    }
}