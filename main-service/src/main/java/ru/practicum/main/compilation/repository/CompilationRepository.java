package ru.practicum.main.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.main.compilation.entity.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long>, JpaSpecificationExecutor<Compilation> {

}
