package com.yarnstash.project;

import jakarta.persistence.*;
import com.yarnstash.yarn.Yarn;
import com.yarnstash.project.Project;

@Entity
@Table(
        name = "project_yarn",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_yarn",
                columnNames = {"project_id", "yarn_id"}
        )
)
public class ProjectYarn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "yarn_id", nullable = false)
    private Yarn yarn;

    @Column(nullable = false)
    private int yardsUsed;

    public ProjectYarn() {
    }

    public ProjectYarn(Project project, Yarn yarn, int yardsUsed) {
        this.project = project;
        this.yarn = yarn;
        this.yardsUsed = yardsUsed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Yarn getYarn() {
        return yarn;
    }

    public void setYarn(Yarn yarn) {
        this.yarn = yarn;
    }

    public int getYardsUsed() {
        return yardsUsed;
    }

    public void setYardsUsed(int yardsUsed) {
        this.yardsUsed = yardsUsed;
    }
}


