package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.APIs;
import com.springai.api_monitoring.service.APIsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apis")
public class APIsController {

    private final APIsService apisService;

    public APIsController(APIsService apisService) {
        this.apisService = apisService;
    }

    @GetMapping
    public List<APIs> getAllAPIs() {
        return apisService.getAllAPIs();
    }

    @GetMapping("/{id}")
    public APIs getAPIById(@PathVariable Long id) {
        return apisService.getAPIById(id).orElseThrow();
    }

    @PostMapping
    public APIs createAPI(@RequestBody APIs api) {
        return apisService.saveAPI(api);
    }

    @PutMapping("/{id}")
    public APIs updateAPI(@PathVariable Long id, @RequestBody APIs apiDetails) {
        return apisService.updateAPI(id, apiDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteAPI(@PathVariable Long id) {
        apisService.deleteAPI(id);
    }
}
