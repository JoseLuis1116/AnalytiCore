package com.analyticore.analysis.dto;

import java.util.List;

public record AnalysisResponse(
        String jobId,
        String status,
        String sentiment,
        List<String> keywords
) {
}