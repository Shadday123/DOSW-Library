package edu.eci.dosw.DOSW_Library.persistence;

import edu.eci.dosw.DOSW_Library.core.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(String id);

    List<Book> findAll();

    void delete(String id);
}
