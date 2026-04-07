package edu.eci.dosw.DOSW_Library.persistence.relational.repository;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}
