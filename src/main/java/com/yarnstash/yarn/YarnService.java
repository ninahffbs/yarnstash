package com.yarnstash.yarn;

import com.yarnstash.project.ProjectYarnRepository;
import com.yarnstash.project.YarnAllocationTotal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public List<YarnResponse> findAll() {
        Map<Long, Integer> allocated = allocatedYardsByYarn();
        return yarnRepository.findAll().stream()
                .map(yarn -> YarnResponse.from(yarn, allocated.getOrDefault(yarn.getId(), 0)))
                .toList();
    }

    public Optional<YarnResponse> findById(Long id) {
        return yarnRepository.findById(id)
                .map(yarn -> YarnResponse.from(yarn, allocatedYardsFor(id)));
    }

    @Transactional
    public YarnResponse add(YarnRequest request) {
        Yarn saved = yarnRepository.save(request.toEntity());
        return YarnResponse.from(saved, 0);
    }

    @Transactional
    public Optional<YarnResponse> update(Long id, YarnRequest request) {
        Optional<Yarn> found = yarnRepository.findById(id);
        if(found.isEmpty()) {
            return Optional.empty();
        }

        Yarn yarn = found.get();

        yarn.setBrand(request.brand());
        yarn.setColorway(request.colorway());
        yarn.setWeight(request.weight());
        yarn.setFiber(request.fiber());
        yarn.setSkeins(request.skeins());
        yarn.setYardsPerSkein(request.yardsPerSkein());

        return Optional.of(YarnResponse.from(yarn, allocatedYardsFor(id)));
    }

    @Transactional
    public boolean delete(Long id) {
        if(!yarnRepository.existsById(id)) {
            return false;
        }
        yarnRepository.deleteById(id);
        return true;
    }

    public int totalYardsInStash() {
        return yarnRepository.findAll().stream()
                .mapToInt(Yarn::getTotalYards)
                .sum();
    }

    private int allocatedYardsFor(Long yarnId) {
        return projectYarnRepository.sumYardsUsedForYarn(yarnId);
    }

    private Map<Long, Integer> allocatedYardsByYarn() {
        return projectYarnRepository.sumYardsUsedByYarn().stream()
                .collect(Collectors.toMap(YarnAllocationTotal::yarnId, total -> (int) total.totalYardsUsed()));
    }
}
