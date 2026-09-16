package com.yarnstash.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AllocationController {
    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping("/projects/{projectId}/yarns")
    public List<AllocationResponse> list(@PathVariable Long projectId) {
        return allocationService.findByProject(projectId);
    }

    @PostMapping("/projects/{projectId}/yarns")
    @ResponseStatus(HttpStatus.CREATED)
    public AllocationResponse allocate(@PathVariable Long projectId, @Valid @RequestBody AllocationRequest request) {
        return allocationService.allocate(projectId, request);
    }

    @GetMapping("/yarns/{yarnId}/projects")
    public List<YarnUsageResponse> projectsForYarn(@PathVariable Long yarnId) {
        return allocationService.findProjectsForYarn(yarnId);
    }
}
