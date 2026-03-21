package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.service.ILoanService;
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

@Tag(name = "Préstamos", description = "API para gestión de préstamos de la biblioteca")
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private ILoanService loanService;

    @Autowired
    @Required
    public void setLoanService(ILoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo préstamo", description = "Crea un préstamo de un libro a un usuario")
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

    @PutMapping("/{loanId}/return")
    @Operation(summary = "Devolver libro", description = "Registra la devolución de un libro prestado")
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

    @GetMapping("/{loanId}")
    @Operation(summary = "Obtener préstamo por ID", description = "Retorna los detalles de un préstamo específico")
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

    @GetMapping
    @Operation(summary = "Obtener todos los préstamos", description = "Retorna una lista de todos los préstamos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        List<LoanDTO> loanDTOs = loans.stream()
                .map(LoanDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loanDTOs);
    }
}
