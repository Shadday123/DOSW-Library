package edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDocument {
    @Id
    private String id;
    private String title;
    private String author;
    private int availableCopies;
    private int totalCopies;
}
