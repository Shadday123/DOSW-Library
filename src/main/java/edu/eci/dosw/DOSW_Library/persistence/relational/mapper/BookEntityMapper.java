package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookEntityMapper {

    public BookEntity toEntity(Book book) {
        if (book == null) return null;
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setAvailableCopies(book.getAvailableCopies());
        entity.setTotalCopies(book.getTotalCopies());
        return entity;
    }

    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        Book book = new Book();
        book.setId(entity.getId());
        book.setTitle(entity.getTitle());
        book.setAuthor(entity.getAuthor());
        book.setAvailableCopies(entity.getAvailableCopies());
        book.setTotalCopies(entity.getTotalCopies());
        return book;
    }
}
