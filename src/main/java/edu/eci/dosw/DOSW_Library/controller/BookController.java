package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.IBookService;
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
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "API para gestión de libros de la biblioteca")
public class BookController {

    private IBookService bookService;

    @Autowired
    @Required
    public void setBookService(IBookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Agregar nuevoo libro", description = "Crea un nuevo libro en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookService.addBook(bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getTotalCopies());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.modelToDTO(book));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los libros", description = "Retorna una lista de todos los libros disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Obtener libro por ID", description = "Retorna los detalles de un libro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BookDTO> getBookById(
            @Parameter(description = "ID del libro", required = true, example = "BK_xyz789")
            @PathVariable String bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseEntity.ok(BookDTO.modelToDTO(book));
    }

    @PutMapping("/{bookId}/availability")
    @Operation(summary = "Actualizar disponibilidad", description = "Actualiza el número de copias disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BookDTO> updateBookAvailability(
            @Parameter(description = "ID del libro", required = true, example = "BK_xyz789")
            @PathVariable String bookId,
            @Parameter(description = "Copias disponibles", required = true, example = "3")
            @RequestParam int availableCopies) {
        Book book = bookService.updateBookAvailability(bookId, availableCopies);
        return ResponseEntity.ok(BookDTO.modelToDTO(book));
    }
}
