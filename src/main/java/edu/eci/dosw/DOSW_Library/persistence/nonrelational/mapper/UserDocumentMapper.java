package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.documents.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserDocumentMapper {

    public UserDocument toDocument(User user) {
        if (user == null) return null;
        UserDocument doc = new UserDocument();
        doc.setId(user.getId());
        doc.setName(user.getName());
        return doc;
    }

    public User toDomain(UserDocument doc) {
        if (doc == null) return null;
        User user = new User();
        user.setId(doc.getId());
        user.setName(doc.getName());
        return user;
    }
}
