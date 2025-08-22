package com.springai.api_monitoring.service;

import com.springai.api_monitoring.model.APIs;
import com.springai.api_monitoring.repository.APIsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class APIsService {
    private final APIsRepository apisRepository;

    public APIsService(APIsRepository apisRepository) {
        this.apisRepository = apisRepository;
    }

    public List<APIs> getAllAPIs() {
        return apisRepository.findAll();
    }

    public Optional<APIs> getAPIById(Long id) {
        return apisRepository.findById(id);
    }

    public APIs saveAPI(APIs api) {
        return apisRepository.save(api);
    }

    public APIs updateAPI(Long id, APIs apiDetails) {
        APIs api = apisRepository.findById(id).orElseThrow();
        api.setName(apiDetails.getName());
        api.setUrl(apiDetails.getUrl());
        api.setOwner(apiDetails.getOwner());
        api.setStatus(apiDetails.getStatus());
        api.setUpdatedAt(java.time.LocalDateTime.now());
        return apisRepository.save(api);
    }

    public void deleteAPI(Long id) {
        apisRepository.deleteById(id);
    }
}
