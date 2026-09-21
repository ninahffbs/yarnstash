package com.yarnstash.project;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public AllocationResponse allocate(@PathVariable Long projectId,
                                       @Valid @RequestBody AllocationRequest request) {
        return allocationService.allocate(projectId, request);
    }

    @PutMapping("/projects/{projectId}/yarns/{yarnId}")
    public AllocationResponse amend(@PathVariable Long projectId,
                                    @PathVariable Long yarnId,
                                    @Valid @RequestBody AllocationUpdateRequest request) {
        return allocationService.amend(projectId, yarnId, request);
    }

    @DeleteMapping("/projects/{projectId}/yarns/{yarnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long projectId, @PathVariable Long yarnId) {
        allocationService.remove(projectId, yarnId);
    }

    @GetMapping("/yarns/{yarnId}/projects")
    public List<YarnUsageResponse> projectsForYarn(@PathVariable Long yarnId) {
        return allocationService.findProjectsForYarn(yarnId);
    }
}
