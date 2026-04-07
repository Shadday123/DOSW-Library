package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents.LoanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoLoanRepository extends MongoRepository<LoanDocument, String> {

    List<LoanDocument> findByUserIdAndStatusTrue(String userId);
}
