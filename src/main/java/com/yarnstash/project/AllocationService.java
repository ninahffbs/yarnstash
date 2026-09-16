package com.yarnstash.project;

import com.yarnstash.yarn.Yarn;
import com.yarnstash.yarn.YarnRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AllocationService {
    private final ProjectRepository projectRepository;
    private final YarnRepository yarnRepository;
    private final ProjectYarnRepository projectYarnRepository;

    public AllocationService(ProjectRepository projectRepository, YarnRepository yarnRepository, ProjectYarnRepository projectYarnRepository) {
        this.projectRepository = projectRepository;
        this.yarnRepository = yarnRepository;
        this.projectYarnRepository = projectYarnRepository;
    }

    public List<AllocationResponse> findByProject(Long projectId) {
        requireProject(projectId);
        return projectYarnRepository.findByProjectId(projectId).stream()
                .map(AllocationResponse::from)
                .toList();
    }

    @Transactional
    public AllocationResponse allocate(Long projectId, AllocationRequest request) {
        Project project = requireProject(projectId);
        Yarn yarn = requireYarn(request.yarnId());

        projectYarnRepository.findByProjectIdAndYarnId(projectId, yarn.getId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Yarn %d is already allocated to project %d".formatted(yarn.getId(), projectId));
                });
        ProjectYarn allocation = new ProjectYarn(project, yarn, request.yardsUsed());
        project.addAllocation(allocation);
        return AllocationResponse.from(projectYarnRepository.save(allocation));
    }

    private Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "project %d not found".formatted(projectId)));
    }

    private Yarn requireYarn(Long yarnId) {
        return yarnRepository.findById(yarnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "yarn %d not found".formatted(yarnId)));
    }

    public List<YarnUsageResponse> findProjectsForYarn(Long yarnId) {
        requireYarn(yarnId);
        return projectYarnRepository.findByYarnId(yarnId).stream()
                .map(YarnUsageResponse::from)
                .toList();
    }
}
