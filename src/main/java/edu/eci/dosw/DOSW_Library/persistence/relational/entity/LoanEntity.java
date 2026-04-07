package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "loans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanEntity {
    @Id
    private String id;
    private String bookId;
    private String userId;
    private Date loanDate;
    private Date returnDate;
    private boolean status;
    private Date actualReturnDate;
}
