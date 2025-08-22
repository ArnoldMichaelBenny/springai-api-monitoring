package com.springai.api_monitoring.service;

import com.springai.api_monitoring.model.Actions;
import com.springai.api_monitoring.repository.ActionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionsService {
    private final ActionsRepository actionsRepository;

    public ActionsService(ActionsRepository actionsRepository) {
        this.actionsRepository = actionsRepository;
    }

    public List<Actions> getAllActions() {
        return actionsRepository.findAll();
    }

    public Optional<Actions> getActionById(Long id) {
        return actionsRepository.findById(id);
    }

    public Actions saveAction(Actions action) {
        return actionsRepository.save(action);
    }

    public Actions updateAction(Long id, Actions actionDetails) {
        Actions action = actionsRepository.findById(id).orElseThrow();
        action.setApi(actionDetails.getApi());
        action.setAnomaly(actionDetails.getAnomaly());
        action.setActionType(actionDetails.getActionType());
        action.setStatus(actionDetails.getStatus());
        action.setExecutedAt(actionDetails.getExecutedAt());
        return actionsRepository.save(action);
    }

    public void deleteAction(Long id) {
        actionsRepository.deleteById(id);
    }
}
