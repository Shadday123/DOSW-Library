package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents.LoanDocument;
import org.springframework.stereotype.Component;

@Component
public class LoanDocumentMapper {

    public LoanDocument toDocument(Loan loan) {
        if (loan == null) return null;
        LoanDocument doc = new LoanDocument();
        doc.setId(loan.getId());
        doc.setBookId(loan.getBook() != null ? loan.getBook().getId() : null);
        doc.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        doc.setLoanDate(loan.getLoanDate());
        doc.setReturnDate(loan.getReturnDate());
        doc.setStatus(loan.isStatus());
        doc.setActualReturnDate(loan.getActualReturnDate());
        return doc;
    }

    public Loan toDomain(LoanDocument doc) {
        if (doc == null) return null;
        Loan loan = new Loan();
        loan.setId(doc.getId());
        loan.setLoanDate(doc.getLoanDate());
        loan.setReturnDate(doc.getReturnDate());
        loan.setStatus(doc.isStatus());
        loan.setActualReturnDate(doc.getActualReturnDate());

        if (doc.getBookId() != null) {
            Book book = new Book();
            book.setId(doc.getBookId());
            loan.setBook(book);
        }
        if (doc.getUserId() != null) {
            User user = new User();
            user.setId(doc.getUserId());
            loan.setUser(user);
        }
        return loan;
    }
}
