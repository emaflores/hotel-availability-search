package com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for POST /search containing the generated search id")
public record SearchResponse(
        @Schema(example = "e8a4e5a2-...") String searchId
) {}
