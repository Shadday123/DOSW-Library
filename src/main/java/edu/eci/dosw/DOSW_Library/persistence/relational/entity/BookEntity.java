package edu.eci.dosw.DOSW_Library.persistence.relational.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity {
    @Id
    private String id;
    private String title;
    private String author;
    private int availableCopies;
    private int totalCopies;
}
