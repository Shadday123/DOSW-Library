package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.HistoryLoan;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.Membresia;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MembresiaRepository extends MongoRepository<Membresia, String> {

}