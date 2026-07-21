package com.analyticore.analysis.controller;

import com.analyticore.analysis.dto.AnalysisResponse;
import com.analyticore.analysis.service.AnalysisService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analysis/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    public AnalysisResponse analyze(
            @PathVariable String jobId
    ) {
        try {
            return analysisService.analyze(jobId);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }
}