package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.LoanEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanEntityMapper {

    public LoanEntity toEntity(Loan loan) {
        if (loan == null) return null;
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId());
        entity.setBookId(loan.getBook() != null ? loan.getBook().getId() : null);
        entity.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        entity.setLoanDate(loan.getLoanDate());
        entity.setReturnDate(loan.getReturnDate());
        entity.setStatus(loan.isStatus());
        entity.setActualReturnDate(loan.getActualReturnDate());
        return entity;
    }

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;
        Loan loan = new Loan();
        loan.setId(entity.getId());
        loan.setLoanDate(entity.getLoanDate());
        loan.setReturnDate(entity.getReturnDate());
        loan.setStatus(entity.isStatus());
        loan.setActualReturnDate(entity.getActualReturnDate());

        if (entity.getBookId() != null) {
            Book book = new Book();
            book.setId(entity.getBookId());
            loan.setBook(book);
        }
        if (entity.getUserId() != null) {
            User user = new User();
            user.setId(entity.getUserId());
            loan.setUser(user);
        }
        return loan;
    }
}
