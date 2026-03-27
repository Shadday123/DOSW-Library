package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.MetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MetaDataRepository extends MongoRepository<MetaData, String> {

}
