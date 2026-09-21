package com.yarnstash.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
            select p from Project p
            where (:status is null or p.status = :status)
            """)
    Page<Project> search(@Param("status") ProjectStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"allocations", "allocations.yarn"})
    Optional<Project> findWithAllocationsById(Long id);
}
