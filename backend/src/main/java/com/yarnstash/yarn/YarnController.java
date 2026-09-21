package com.yarnstash.yarn;

import com.yarnstash.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/yarns")
public class YarnController {
    private final YarnService yarnService;

    public YarnController(YarnService yarnService) {
        this.yarnService = yarnService;
    }

    @GetMapping
    public PageResponse<YarnResponse> list(@RequestParam(required = false) YarnWeight weight,
                                           @RequestParam(required = false) String fiber,
                                           @PageableDefault(size = 20, sort = "brand") Pageable pageable) {
        return yarnService.search(weight, fiber, pageable);
    }

    @GetMapping("/stats")
    public StashStats stats() {
        return yarnService.stats();
    }

    @GetMapping("/{id}")
    public YarnResponse byId(@PathVariable Long id) {
        return yarnService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public YarnResponse create(@Valid @RequestBody YarnRequest request) {
        return yarnService.add(request);
    }

    @PutMapping("/{id}")
    public YarnResponse update(@PathVariable Long id, @Valid @RequestBody YarnRequest request) {
        return yarnService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        yarnService.delete(id);
    }
}
