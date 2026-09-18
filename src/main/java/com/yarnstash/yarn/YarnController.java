package com.yarnstash.yarn;


import com.yarnstash.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/yarns")
public class YarnController {
    private final YarnService yarnService;

    public YarnController(YarnService yarnService) {
        this.yarnService = yarnService;
    }

    @GetMapping
    public PageResponse<YarnResponse> list(@RequestParam(required = false) YarnWeight weight, @RequestParam(required = false) String fiber, @PageableDefault(size = 20, sort = "brand") Pageable pageable) {
        return yarnService.search(weight, fiber, pageable);
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
        return new YardageStats((int) yarnService.count(), yarnService.totalYardsInStash());
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
