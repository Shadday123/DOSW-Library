package edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDocument {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
}
