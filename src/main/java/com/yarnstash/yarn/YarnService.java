package com.yarnstash.yarn;

import com.yarnstash.common.ConflictException;
import com.yarnstash.common.NotFoundException;
import com.yarnstash.common.PageResponse;
import com.yarnstash.project.ProjectYarnRepository;
import com.yarnstash.project.YarnAllocationTotal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class YarnService {
    private final YarnRepository yarnRepository;
    private final ProjectYarnRepository projectYarnRepository;

    public YarnService(YarnRepository yarnRepository, ProjectYarnRepository projectYarnRepository) {
        this.yarnRepository = yarnRepository;
        this.projectYarnRepository = projectYarnRepository;
    }

    public PageResponse<YarnResponse> search(YarnWeight weight, String fiber, Pageable pageable) {
        String fiberFilter = (fiber == null || fiber.isBlank()) ? null : fiber.trim();

        Page<Yarn> page = yarnRepository.search(weight, fiberFilter, pageable);
        Map<Long, Integer> allocated = allocatedYardsByYarn();

        return PageResponse.from(
                page.map(yarn -> YarnResponse.from(yarn, allocated.getOrDefault(yarn.getId(), 0))));
    }

    public StashStats stats() {
        return yarnRepository.stats();
    }

    public YarnResponse findById(Long id) {
        return YarnResponse.from(getOrThrow(id), allocatedYardsFor(id));
    }

    @Transactional
    public YarnResponse add(YarnRequest request) {
        Yarn saved = yarnRepository.save(request.toEntity());
        return YarnResponse.from(saved, 0);
    }

    @Transactional
    public YarnResponse update(Long id, YarnRequest request) {
        Yarn yarn = getOrThrow(id);

        yarn.setBrand(request.brand());
        yarn.setColorway(request.colorway());
        yarn.setWeight(request.weight());
        yarn.setFiber(request.fiber());
        yarn.setSkeins(request.skeins());
        yarn.setYardsPerSkein(request.yardsPerSkein());
        yarn.setPurchasedOn(request.purchasedOn());

        return YarnResponse.from(yarn, allocatedYardsFor(id));
    }

    @Transactional
    public void delete(Long id) {
        Yarn yarn = getOrThrow(id);

        if (projectYarnRepository.existsByYarnId(id)) {
            throw new ConflictException(
                    "Yarn %d is allocated to one or more projects and cannot be deleted".formatted(id));
        }

        yarnRepository.delete(yarn);
    }

    private Yarn getOrThrow(Long id) {
        return yarnRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Yarn %d not found".formatted(id)));
    }

    private int allocatedYardsFor(Long yarnId) {
        return projectYarnRepository.sumYardsUsedForYarn(yarnId);
    }

    private Map<Long, Integer> allocatedYardsByYarn() {
        return projectYarnRepository.sumYardsUsedByYarn().stream()
                .collect(Collectors.toMap(YarnAllocationTotal::yarnId,
                        total -> (int) total.totalYardsUsed()));
    }
}
