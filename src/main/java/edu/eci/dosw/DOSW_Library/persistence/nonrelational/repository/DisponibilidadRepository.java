package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Disponibilidad;
import edu.eci.dosw.DOSW_Library.core.model.HistoryLoan;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DisponibilidadRepository extends MongoRepository<Disponibilidad, String> {

}