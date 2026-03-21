package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.model.Book;

import java.util.List;

public interface IBookService {

    Book addBook(String title, String author, int totalCopies);

    List<Book> getAllBooks();

    Book getBookById(String bookId);

    List<Book> getBooksByTitle(String title);

    List<Book> getBooksByAuthor(String author);

    boolean isBookAvailable(String bookId);

    void decreaseAvailableCopies(String bookId);

    void increaseAvailableCopies(String bookId);

    Book updateBookAvailability(String bookId, int newAvailableCopies);

    boolean deleteBook(String bookId);

    int getTotalBooks();

    int getTotalAvailableCopies();
}
