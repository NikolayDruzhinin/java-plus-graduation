package ru.practicum.application.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.application.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findAll(Pageable page);

    @Query("SELECT c FROM Compilation c WHERE (:pinned is null or c.pinned = :pinned)")
    List<Compilation> findAllWithPinned(Boolean pinned, Pageable page);
}
