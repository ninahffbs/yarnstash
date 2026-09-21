package com.yarnstash.project;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(precision = 4, scale = 2)
    private BigDecimal hookSize;

    @Column(columnDefinition = "text")
    private String notes;

    private LocalDate startedOn;

    private LocalDate finishedOn;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectYarn> allocations = new ArrayList<>();

    public Project() {
    }

    public Project(Long id, String name, ProjectStatus status, BigDecimal hookSize, String notes, LocalDate startedOn, LocalDate finishedOn) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.hookSize = hookSize;
        this.notes = notes;
        this.startedOn = startedOn;
        this.finishedOn = finishedOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public BigDecimal getHookSize() {
        return hookSize;
    }

    public void setHookSize(BigDecimal hookSize) {
        this.hookSize = hookSize;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(LocalDate startedOn) {
        this.startedOn = startedOn;
    }

    public LocalDate getFinishedOn() {
        return finishedOn;
    }

    public void setFinishedOn(LocalDate finishedOn) {
        this.finishedOn = finishedOn;
    }

    public List<ProjectYarn> getAllocations() {
        return allocations;
    }

    public void addAllocation(ProjectYarn allocation) {
        allocations.add(allocation);
        allocation.setProject(this);
    }

    public void removeAllocation(ProjectYarn allocation) {
        allocations.remove(allocation);
        allocation.setProject(null);
    }
}
