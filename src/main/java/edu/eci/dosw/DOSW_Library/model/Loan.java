package edu.eci.dosw.DOSW_Library.model;

import lombok.Data;

import java.util.Date;

@Data
public class Loan {
    private Book book;
    private User user;
    private Date loanDate;
    private boolean status;
    private Date returnDate;
}
