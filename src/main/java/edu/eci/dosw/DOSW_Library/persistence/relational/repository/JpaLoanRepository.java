package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaLoanRepository extends JpaRepository<LoanEntity, String> {

    List<LoanEntity> findByUserIdAndStatusTrue(String userId);
}
