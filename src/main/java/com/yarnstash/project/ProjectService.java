package com.yarnstash.project;

import com.yarnstash.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Optional<ProjectDetailResponse> findById(Long id) {
        return projectRepository.findWithAllocationsById(id)
                .map(ProjectDetailResponse::from);
    }

    @Transactional
    public ProjectResponse add(ProjectRequest request) {
        Project project = request.toEntity();
        applyStatusRules(project);
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @Transactional
    public Optional<ProjectResponse> update(Long id, ProjectRequest request) {
        Optional<Project> found = projectRepository.findById(id);
        if(found.isEmpty()) {
            return Optional.empty();
        }

        Project project = found.get();
        project.setName(request.name());
        project.setStatus(request.status());
        project.setHookSize(request.hookSize());
        project.setNotes(request.notes());
        project.setStartedOn(request.startedOn());
        project.setFinishedOn(request.finishedOn());

        applyStatusRules(project);

        return Optional.of(ProjectResponse.from(project));
    }

    @Transactional
    public boolean delete(Long id) {
        if(!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }

    private void applyStatusRules(Project project) {
        if(project.getStatus() == null) {
            project.setStatus(ProjectStatus.PLANNED);
        }
        if(project.getStatus() == ProjectStatus.FINISHED) {
            if(project.getFinishedOn() == null) {
                project.setFinishedOn(LocalDate.now());
            }
        }
        else {
            project.setFinishedOn(null);
        }
    }
}
