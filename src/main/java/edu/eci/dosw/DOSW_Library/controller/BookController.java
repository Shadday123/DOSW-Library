package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de libros
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "API para gestión de libros de la biblioteca")
public class BookController {

    private BookService bookService;

    /**
     * Agrega un nuevo libro al sistema
     */
    @PostMapping
    @Operation(summary = "Agregar nuevo libro",
            description = "Crea un nuevo libro en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        Book book = bookService.addBook(bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getTotalCopies());
        return ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.modelToDTO(book));
    }

    /**
     * Obtiene todos los libros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los libros",
            description = "Retorna una lista de todos los libros disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    /**
     * Obtiene un libro por su ID
     */
    @GetMapping("/{bookId}")
    @Operation(summary = "Obtener libro por ID",
            description = "Retorna los detalles de un libro específico")
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

    /**
     * Busca libros por título
     */
    @GetMapping("/search/title")
    @Operation(summary = "Buscar libros por título",
            description = "Retorna libros que coincidan con el título buscado")
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    public ResponseEntity<List<BookDTO>> getBooksByTitle(
            @Parameter(description = "Título o parte del título", required = true, example = "Quijote")
            @RequestParam String title) {
        List<Book> books = bookService.getBooksByTitle(title);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    /**
     * Busca libros por autor
     */
    @GetMapping("/search/author")
    @Operation(summary = "Buscar libros por autor",
            description = "Retorna libros del autor especificado")
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(
            @Parameter(description = "Nombre del autor", required = true, example = "Cervantes")
            @RequestParam String author) {
        List<Book> books = bookService.getBooksByAuthor(author);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::modelToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    /**
     * Verifica si un libro está disponible
     */
    @GetMapping("/{bookId}/available")
    @Operation(summary = "Verificar disponibilidad",
            description = "Verifica si un libro tiene copias disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad verificada"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Boolean> isBookAvailable(
            @Parameter(description = "ID del libro", required = true, example = "BK_xyz789")
            @PathVariable String bookId) {
        boolean isAvailable = bookService.isBookAvailable(bookId);
        return ResponseEntity.ok(isAvailable);
    }

    /**
     * Actualiza la disponibilidad de un libro
     */
    @PutMapping("/{bookId}/availability")
    @Operation(summary = "Actualizar disponibilidad",
            description = "Actualiza el número de copias disponibles")
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

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Eliminar libro",
            description = "Elimina un libro de la biblioteca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Libro eliminado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID del libro a eliminar", required = true, example = "BK_xyz789")
            @PathVariable String bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/stats/total")
    @Operation(summary = "Obtener total de libros",
            description = "Retorna el número total de libros únicos")
    @ApiResponse(responseCode = "200", description = "Total obtenido")
    public ResponseEntity<Integer> getTotalBooks() {
        return ResponseEntity.ok(bookService.getTotalBooks());
    }


    @GetMapping("/stats/available")
    @Operation(summary = "Obtener copias disponibles",
            description = "Retorna el total de copias disponibles")
    @ApiResponse(responseCode = "200", description = "Total obtenido")
    public ResponseEntity<Integer> getTotalAvailableCopies() {
        return ResponseEntity.ok(bookService.getTotalAvailableCopies());
    }
}