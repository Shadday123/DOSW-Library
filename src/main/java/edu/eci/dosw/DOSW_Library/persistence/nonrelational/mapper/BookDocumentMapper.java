package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents.BookDocument;
import org.springframework.stereotype.Component;

@Component
public class BookDocumentMapper {

    public BookDocument toDocument(Book book) {
        if (book == null) return null;
        BookDocument doc = new BookDocument();
        doc.setId(book.getId());
        doc.setTitle(book.getTitle());
        doc.setAuthor(book.getAuthor());
        doc.setAvailableCopies(book.getAvailableCopies());
        doc.setTotalCopies(book.getTotalCopies());
        return doc;
    }

    public Book toDomain(BookDocument doc) {
        if (doc == null) return null;
        Book book = new Book();
        book.setId(doc.getId());
        book.setTitle(doc.getTitle());
        book.setAuthor(doc.getAuthor());
        book.setAvailableCopies(doc.getAvailableCopies());
        book.setTotalCopies(doc.getTotalCopies());
        return book;
    }
}
