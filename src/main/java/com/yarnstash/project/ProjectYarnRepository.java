package com.yarnstash.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectYarnRepository extends JpaRepository<ProjectYarn, Long> {
    Optional<ProjectYarn> findByProjectIdAndYarnId(Long projectId, Long yarnId);

    List<ProjectYarn> findByProjectId(Long projectId);

    List<ProjectYarn> findByYarnId(Long yarnId);

    @Query("""
            select coalesce(sum(py.yardsUsed), 0)
            from ProjectYarn py
            where py.yarn.id = :yarnId
              and py.project.status <> com.yarnstash.project.ProjectStatus.FROGGED
            """)
    int sumYardsUsedForYarn(@Param("yarnId") Long yarnId);

    @Query("""
            select new com.yarnstash.project.YarnAllocationTotal(py.yarn.id, sum(py.yardsUsed))
            from ProjectYarn py
            where py.project.status <> com.yarnstash.project.ProjectStatus.FROGGED
            group by py.yarn.id
            """)
    List<YarnAllocationTotal> sumYardsUsedByYarn();

    boolean existsByYarnId(Long yarnId);

    @Query("""
            select coalesce(sum(py.yardsUsed), 0)
            from ProjectYarn py
            where py.yarn.id = :yarnId
              and py.project.id <> :projectId
              and py.project.status <> com.yarnstash.project.ProjectStatus.FROGGED
            """)
    int sumYardsUsedForYarnExcludingProject(@Param("yarnId") Long yarnId, @Param("projectId") Long projectId);
}