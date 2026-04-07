package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoBookRepository extends MongoRepository<BookDocument, String> {
}
