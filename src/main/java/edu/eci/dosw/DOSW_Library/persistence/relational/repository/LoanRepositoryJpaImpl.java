package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.persistence.LoanRepository;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.LoanEntityMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("relational")
public class LoanRepositoryJpaImpl implements LoanRepository {

    private final JpaLoanRepository repository;
    private final LoanEntityMapper mapper;

    public LoanRepositoryJpaImpl(JpaLoanRepository repository, LoanEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        return mapper.toDomain(repository.save(mapper.toEntity(loan)));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Loan> findActiveByUserId(String userId) {
        return repository.findByUserIdAndStatusTrue(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
