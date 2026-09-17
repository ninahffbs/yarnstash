package com.yarnstash.project;

import com.yarnstash.yarn.Yarn;
import com.yarnstash.yarn.YarnRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

        requireAvailableYardage(yarn, projectId, request.yardsUsed());
        return AllocationResponse.from(projectYarnRepository.save(allocation));
    }

    @Transactional
    public AllocationResponse amend(Long projectId, Long yarnId, AllocationUpdateRequest request) {
        requireProject(projectId);
        Yarn yarn = requireYarn(yarnId);

        ProjectYarn allocation = projectYarnRepository.findByProjectIdAndYarnId(projectId, yarnId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "yarn %d is not allocated to project %d".formatted(yarnId, projectId)));

        requireAvailableYardage(yarn, projectId, request.yardsUsed());
        allocation.setYardsUsed(request.yardsUsed());
        return AllocationResponse.from(allocation);
    }

    @Transactional
    public boolean remove(Long projectId, Long yarnId) {
        requireProject(projectId);

        Optional<ProjectYarn> found = projectYarnRepository.findByProjectIdAndYarnId(projectId, yarnId);
        if(found.isEmpty()) {
            return false;
        }

        projectYarnRepository.delete(found.get());
        return true;
    }

    private void requireAvailableYardage(Yarn yarn, Long projectId, int requestedYards) {
        int allocatedElsewhere = projectYarnRepository.sumYardsUsedForYarnExcludingProject(yarn.getId(), projectId);
        int available = yarn.getTotalYards() - allocatedElsewhere;

        if(requestedYards > available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "not enough yarn: requested %d yards of yarn %d but only %d available".formatted(requestedYards, yarn.getId(), available));
        }
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
