package com.yarnstash.yarn;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class YarnService {
    private final YarnRepository yarnRepository;

    public YarnService(YarnRepository yarnRepository) {
        this.yarnRepository = yarnRepository;
    }

    public List<YarnResponse> findAll() {
        return yarnRepository.findAll().stream()
                .map(YarnResponse::from)
                .toList();
    }

    public Optional<YarnResponse> findById(Long id) {
        return yarnRepository.findById(id)
                .map(YarnResponse::from);
    }

    @Transactional
    public YarnResponse add(YarnRequest request) {
        Yarn saved = yarnRepository.save(request.toEntity());
        return YarnResponse.from(saved);
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

        return Optional.of(YarnResponse.from(yarn));
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
}
