
package edu.eci.dosw.DOSW_Library.controller.dto;

import edu.eci.dosw.DOSW_Library.core.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {
    private String id;
    private String bookId;
    private String userId;
    private Date loanDate;
    private Date returnDate;
    private Date actualReturnDate;
    private boolean status;

    public static LoanDTO modelToDTO(Loan loan) {
        if (loan == null) {
            return null;
        }
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setBookId(loan.getBook().getId());
        loanDTO.setUserId(loan.getUser().getId());
        loanDTO.setLoanDate(loan.getLoanDate());
        loanDTO.setReturnDate(loan.getReturnDate());
        loanDTO.setActualReturnDate(loan.getActualReturnDate());
        loanDTO.setStatus(loan.isStatus());
        return loanDTO;
    }
}

