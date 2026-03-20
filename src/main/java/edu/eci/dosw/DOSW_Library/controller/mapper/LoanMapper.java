package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;

public class LoanMapper {

    private LoanMapper() {
    }

    public static LoanDTO modelToDTO(Loan loan) {
        if (loan == null) {
            return null;
        }
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setBookId(loan.getBook() != null ? loan.getBook().getId() : null);
        dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setActualReturnDate(loan.getActualReturnDate());
        dto.setStatus(loan.isStatus());
        return dto;
    }
}