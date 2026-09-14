package com.yarnstash.yarn;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yarns")
public class YarnController {
    private final YarnService yarnService;

    public YarnController(YarnService yarnService) {
        this.yarnService = yarnService;
    }

    @GetMapping
    public List<YarnResponse> list() {
        return yarnService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<YarnResponse> byId(@PathVariable Long id) {
        return yarnService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public YarnResponse create(@Valid @RequestBody YarnRequest request) {
        return yarnService.add(request);
    }

    @GetMapping("/stats")
    public YardageStats stats() {
        return new YardageStats(yarnService.findAll().size(), yarnService.totalYardsInStash());
    }

    @PutMapping("/{id}")
    public ResponseEntity<YarnResponse> update(@PathVariable Long id, @Valid @RequestBody YarnRequest request) {
        return yarnService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if(yarnService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static class YardageStats {
        private final int distinctYarns;
        private final int totalYards;

        public YardageStats(int distinctYarns, int totalYards) {
            this.distinctYarns = distinctYarns;
            this.totalYards = totalYards;
        }

        public int getDistinctYarns() {
            return distinctYarns;
        }

        public int getTotalYards() {
            return totalYards;
        }
    }
}
