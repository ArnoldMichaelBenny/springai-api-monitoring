package com.springai.api_monitoring.controller;

import com.springai.api_monitoring.model.Actions;
import com.springai.api_monitoring.service.ActionsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
public class ActionsController {

    private final ActionsService actionsService;

    public ActionsController(ActionsService actionsService) {
        this.actionsService = actionsService;
    }

    @GetMapping
    public List<Actions> getAllActions() {
        return actionsService.getAllActions();
    }

    @GetMapping("/{id}")
    public Actions getActionById(@PathVariable Long id) {
        return actionsService.getActionById(id).orElseThrow();
    }

    @PostMapping
    public Actions createAction(@RequestBody Actions action) {
        return actionsService.saveAction(action);
    }

    @PutMapping("/{id}")
    public Actions updateAction(@PathVariable Long id, @RequestBody Actions actionDetails) {
        return actionsService.updateAction(id, actionDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteAction(@PathVariable Long id) {
        actionsService.deleteAction(id);
    }
}
