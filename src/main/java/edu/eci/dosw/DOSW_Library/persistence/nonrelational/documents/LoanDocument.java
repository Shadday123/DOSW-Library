package edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "loans")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDocument {
    @Id
    private String id;
    private String bookId;
    private String userId;
    private Date loanDate;
    private Date returnDate;
    private boolean status;
    private Date actualReturnDate;
}
