package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.BookRepository;
import edu.eci.dosw.DOSW_Library.persistence.relational.mapper.BookEntityMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("relational")
public class BookRepositoryJpaImpl implements BookRepository {

    private final JpaBookRepository repository;
    private final BookEntityMapper mapper;

    public BookRepositoryJpaImpl(JpaBookRepository repository, BookEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(repository.save(mapper.toEntity(book)));
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
