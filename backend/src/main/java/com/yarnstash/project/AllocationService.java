package com.yarnstash.project;

import com.yarnstash.common.ConflictException;
import com.yarnstash.common.NotFoundException;
import com.yarnstash.yarn.Yarn;
import com.yarnstash.yarn.YarnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AllocationService {
    private final ProjectRepository projectRepository;
    private final YarnRepository yarnRepository;
    private final ProjectYarnRepository projectYarnRepository;

    public AllocationService(ProjectRepository projectRepository,
                             YarnRepository yarnRepository,
                             ProjectYarnRepository projectYarnRepository) {
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

    public List<YarnUsageResponse> findProjectsForYarn(Long yarnId) {
        requireYarn(yarnId);
        return projectYarnRepository.findByYarnId(yarnId).stream()
                .map(YarnUsageResponse::from)
                .toList();
    }

    @Transactional
    public AllocationResponse allocate(Long projectId, AllocationRequest request) {
        Project project = requireProject(projectId);
        Yarn yarn = requireYarn(request.yarnId());

        projectYarnRepository.findByProjectIdAndYarnId(projectId, yarn.getId())
                .ifPresent(existing -> {
                    throw new ConflictException(
                            "Yarn %d is already allocated to project %d - amend the existing allocation instead"
                                    .formatted(yarn.getId(), projectId));
                });

        requireAvailableYardage(yarn, projectId, request.yardsUsed());

        ProjectYarn allocation = new ProjectYarn(project, yarn, request.yardsUsed());
        project.addAllocation(allocation);

        return AllocationResponse.from(projectYarnRepository.save(allocation));
    }

    @Transactional
    public AllocationResponse amend(Long projectId, Long yarnId, AllocationUpdateRequest request) {
        requireProject(projectId);
        Yarn yarn = requireYarn(yarnId);

        ProjectYarn allocation = requireAllocation(projectId, yarnId);

        requireAvailableYardage(yarn, projectId, request.yardsUsed());
        allocation.setYardsUsed(request.yardsUsed());

        return AllocationResponse.from(allocation);
    }

    @Transactional
    public void remove(Long projectId, Long yarnId) {
        requireProject(projectId);
        projectYarnRepository.delete(requireAllocation(projectId, yarnId));
    }

    /**
     * Compares the requested yardage against what is available once every other
     * project's claim on this yarn is accounted for. The current project is
     * excluded so that amending an existing allocation is not judged against
     * its own yardage.
     */
    private void requireAvailableYardage(Yarn yarn, Long projectId, int requestedYards) {
        int allocatedElsewhere =
                projectYarnRepository.sumYardsUsedForYarnExcludingProject(yarn.getId(), projectId);
        int available = yarn.getTotalYards() - allocatedElsewhere;

        if (requestedYards > available) {
            throw new ConflictException(
                    "Not enough yarn: requested %d yards of yarn %d but only %d available"
                            .formatted(requestedYards, yarn.getId(), available));
        }
    }

    private ProjectYarn requireAllocation(Long projectId, Long yarnId) {
        return projectYarnRepository.findByProjectIdAndYarnId(projectId, yarnId)
                .orElseThrow(() -> new NotFoundException(
                        "Yarn %d is not allocated to project %d".formatted(yarnId, projectId)));
    }

    private Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project %d not found".formatted(projectId)));
    }

    private Yarn requireYarn(Long yarnId) {
        return yarnRepository.findById(yarnId)
                .orElseThrow(() -> new NotFoundException("Yarn %d not found".formatted(yarnId)));
    }
}
