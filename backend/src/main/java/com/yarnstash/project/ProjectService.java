package com.yarnstash.project;

import com.yarnstash.common.NotFoundException;
import com.yarnstash.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public PageResponse<ProjectResponse> search(ProjectStatus status, Pageable pageable) {
        Page<Project> page = projectRepository.search(status, pageable);
        return PageResponse.from(page.map(ProjectResponse::from));
    }

    public ProjectDetailResponse findById(Long id) {
        return projectRepository.findWithAllocationsById(id)
                .map(ProjectDetailResponse::from)
                .orElseThrow(() -> new NotFoundException("Project %d not found".formatted(id)));
    }

    @Transactional
    public ProjectResponse add(ProjectRequest request) {
        Project project = request.toEntity();
        applyStatusRules(project);
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getOrThrow(id);

        project.setName(request.name());
        project.setStatus(request.status());
        project.setHookSize(request.hookSize());
        project.setNotes(request.notes());
        project.setStartedOn(request.startedOn());
        project.setFinishedOn(request.finishedOn());

        applyStatusRules(project);

        return ProjectResponse.from(project);
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.delete(getOrThrow(id));
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project %d not found".formatted(id)));
    }

    private void applyStatusRules(Project project) {
        if (project.getStatus() == null) {
            project.setStatus(ProjectStatus.PLANNED);
        }

        if (project.getStatus() == ProjectStatus.FINISHED) {
            if (project.getFinishedOn() == null) {
                project.setFinishedOn(LocalDate.now());
            }
        } else {
            project.setFinishedOn(null);
        }
    }
}
