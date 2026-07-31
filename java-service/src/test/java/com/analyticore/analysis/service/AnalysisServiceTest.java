package com.analyticore.analysis.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.analyticore.analysis.dto.AnalysisResponse;
import com.analyticore.analysis.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private JobRepository jobRepository;

    private AnalysisService analysisService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();

        analysisService = new AnalysisService(
                jobRepository,
                objectMapper
        );
    }

    @Test
    void shouldClassifyPositiveText() {
        String jobId = "job-positive";
        String text = "El servicio es excelente, bueno y de gran calidad";

        when(jobRepository.findTextById(jobId))
                .thenReturn(Optional.of(text));

        AnalysisResponse response = analysisService.analyze(jobId);

        assertEquals("COMPLETADO", response.status());
        assertEquals("POSITIVO", response.sentiment());
        assertFalse(response.keywords().isEmpty());

        verify(jobRepository).markAsProcessing(jobId);
        verify(jobRepository).complete(
                eq(jobId),
                eq("POSITIVO"),
                anyString()
        );
    }

    @Test
    void shouldClassifyNegativeText() {
        String jobId = "job-negative";
        String text = "El resultado fue terrible, lento y deficiente";

        when(jobRepository.findTextById(jobId))
                .thenReturn(Optional.of(text));

        AnalysisResponse response = analysisService.analyze(jobId);

        assertEquals("COMPLETADO", response.status());
        assertEquals("NEGATIVO", response.sentiment());

        verify(jobRepository).markAsProcessing(jobId);
        verify(jobRepository).complete(
                eq(jobId),
                eq("NEGATIVO"),
                anyString()
        );
    }

    @Test
    void shouldClassifyNeutralText() {
        String jobId = "job-neutral";
        String text = "La mesa contiene documentos y carpetas";

        when(jobRepository.findTextById(jobId))
                .thenReturn(Optional.of(text));

        AnalysisResponse response = analysisService.analyze(jobId);

        assertEquals("COMPLETADO", response.status());
        assertEquals("NEUTRO", response.sentiment());

        verify(jobRepository).markAsProcessing(jobId);
        verify(jobRepository).complete(
                eq(jobId),
                eq("NEUTRO"),
                anyString()
        );
    }
}