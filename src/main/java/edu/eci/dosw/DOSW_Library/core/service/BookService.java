
package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotAvailableException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema
@Service
public class BookService {
    @Schema(hidden = true)
    private Map<String, Book> books;

    public BookService() {
        this.books = new HashMap<>();
    }

    public Book addBook(String title, String author, int totalCopies) {
        ValidationUtil.validateNotEmpty(title, "Título del libro");
        ValidationUtil.validateNotEmpty(author, "Autor del libro");

        if (totalCopies <= 0) {
            throw new IllegalArgumentException("El número de copias debe ser mayor a cero");
        }

        Book book = new Book();
        book.setId(IdGeneratorUtil.generateBookId());
        book.setTitle(title);
        book.setAuthor(author);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);

        books.put(book.getId(), book);
        return book;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Book getBookById(String bookId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");

        Book book = books.get(bookId);
        return book;
    }

    public List<Book> getBooksByTitle(String title) {
        ValidationUtil.validateNotEmpty(title, "Título del libro");

        List<Book> result = new ArrayList<>();
        String searchTerm = title.toLowerCase();

        books.values().forEach(book -> {
            if (book.getTitle().toLowerCase().contains(searchTerm)) {
                result.add(book);
            }
        });

        return result;
    }

    public List<Book> getBooksByAuthor(String author) {
        ValidationUtil.validateNotEmpty(author, "Autor del libro");

        List<Book> result = new ArrayList<>();
        String searchTerm = author.toLowerCase();

        books.values().forEach(book -> {
            if (book.getAuthor().toLowerCase().contains(searchTerm)) {
                result.add(book);
            }
        });

        return result;
    }

    public boolean isBookAvailable(String bookId) {
        Book book = getBookById(bookId);
        return book.getAvailableCopies() > 0;
    }

    public void decreaseAvailableCopies(String bookId) {
        Book book = getBookById(bookId);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
    }

    public void increaseAvailableCopies(String bookId) {
        Book book = getBookById(bookId);

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new IllegalArgumentException("No se pueden aumentar más copias, ya está en el máximo");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    public Book updateBookAvailability(String bookId, int newAvailableCopies) {
        Book book = getBookById(bookId);
        ValidationUtil.validateCopies(newAvailableCopies, book.getTotalCopies());

        book.setAvailableCopies(newAvailableCopies);
        return book;
    }

    public boolean deleteBook(String bookId) {
        ValidationUtil.validateNotEmpty(bookId, "ID del libro");
        return books.remove(bookId) != null;
    }

    public int getTotalBooks() {
        return books.size();
    }

    public int getTotalAvailableCopies() {
        return books.values().stream()
                .mapToInt(Book::getAvailableCopies)
                .sum();
    }
}

