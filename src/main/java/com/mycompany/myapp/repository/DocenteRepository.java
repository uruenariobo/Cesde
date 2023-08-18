package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Docente;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Docente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {}
