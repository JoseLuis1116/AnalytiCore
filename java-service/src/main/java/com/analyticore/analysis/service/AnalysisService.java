package com.analyticore.analysis.service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.analyticore.analysis.dto.AnalysisResponse;
import com.analyticore.analysis.repository.JobRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisService {

    private static final Set<String> POSITIVE_WORDS = Set.of(
            "excelente",
            "bueno",
            "buena",
            "genial",
            "rapido",
            "facil",
            "recomendado",
            "feliz",
            "positivo",
            "agradable",
            "perfecto",
            "calidad"
    );

    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "malo",
            "mala",
            "pesimo",
            "terrible",
            "lento",
            "dificil",
            "problema",
            "error",
            "negativo",
            "horrible",
            "deficiente"
    );

    private static final Set<String> STOP_WORDS = Set.of(
            "para",
            "como",
            "este",
            "esta",
            "estos",
            "estas",
            "desde",
            "hasta",
            "pero",
            "porque",
            "sobre",
            "entre",
            "tambien",
            "servicio",
            "texto",
            "utilizar",
            "donde",
            "cuando",
            "tiene",
            "tener",
            "muy",
            "fue",
            "una",
            "unos",
            "unas",
            "del",
            "las",
            "los"
    );

    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public AnalysisService(
            JobRepository jobRepository,
            ObjectMapper objectMapper
    ) {
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AnalysisResponse analyze(String jobId) {
        String text = jobRepository.findTextById(jobId)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Trabajo no encontrado"
                        )
                );

        jobRepository.markAsProcessing(jobId);

        try {
            String normalizedText = normalize(text);
            List<String> words = extractWords(normalizedText);

            String sentiment = calculateSentiment(words);
            List<String> keywords = extractKeywords(words);

            String keywordsJson = objectMapper.writeValueAsString(keywords);

            jobRepository.complete(
                    jobId,
                    sentiment,
                    keywordsJson
            );

            return new AnalysisResponse(
                    jobId,
                    "COMPLETADO",
                    sentiment,
                    keywords
            );
        } catch (JacksonException exception) {
            jobRepository.markAsError(jobId);

            throw new IllegalStateException(
                    "No se pudieron guardar las palabras clave",
                    exception
            );
        }
    }

    private String normalize(String text) {
        String withoutAccents = Normalizer
                .normalize(text.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return withoutAccents.replaceAll("[^a-zñ\\s]", " ");
    }

    private List<String> extractWords(String text) {
        return Arrays.stream(text.split("\\s+"))
                .filter(word -> !word.isBlank())
                .toList();
    }

    private String calculateSentiment(List<String> words) {
        long positives = words.stream()
                .filter(POSITIVE_WORDS::contains)
                .count();

        long negatives = words.stream()
                .filter(NEGATIVE_WORDS::contains)
                .count();

        if (positives > negatives) {
            return "POSITIVO";
        }

        if (negatives > positives) {
            return "NEGATIVO";
        }

        return "NEUTRO";
    }

    private List<String> extractKeywords(List<String> words) {
        Map<String, Long> frequencies = words.stream()
                .filter(word -> word.length() >= 4)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        )
                );

        return frequencies.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue(
                                Comparator.reverseOrder()
                        ).thenComparing(Map.Entry.comparingByKey())
                )
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }
}