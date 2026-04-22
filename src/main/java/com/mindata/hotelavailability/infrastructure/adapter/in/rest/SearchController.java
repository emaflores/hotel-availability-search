package com.mindata.hotelavailability.infrastructure.adapter.in.rest;

import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.CreateSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.CountResponse;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.SearchRequest;
import com.mindata.hotelavailability.infrastructure.adapter.in.rest.dto.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Validated
@Tag(name = "Hotel availability searches", description = "Endpoints for creating and counting searches")
public class SearchController {

    private final CreateSearchUseCase createSearchUseCase;
    private final CountSearchUseCase countSearchUseCase;

    public SearchController(CreateSearchUseCase createSearchUseCase, CountSearchUseCase countSearchUseCase) {
        this.createSearchUseCase = createSearchUseCase;
        this.countSearchUseCase = countSearchUseCase;
    }

    @Operation(summary = "Accepts a search request and publishes it to Kafka for asynchronous persistence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Search accepted (persisted asynchronously by the consumer)"),
            @ApiResponse(responseCode = "400", description = "Invalid payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(path = "/search", consumes = "application/json", produces = "application/json")
    public ResponseEntity<SearchResponse> createSearch(@Valid @RequestBody SearchRequest request) {
        String searchId = createSearchUseCase.createSearch(request.toCommand());
        return ResponseEntity.accepted().body(new SearchResponse(searchId));
    }

    @Operation(summary = "Counts persisted searches equal to a previously created one. "
            + "Because persistence is asynchronous, a 404 right after POST /search is expected until the consumer catches up.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count returned"),
            @ApiResponse(responseCode = "400", description = "Missing or blank searchId parameter",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "searchId is unknown or the consumer has not yet persisted it",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(path = "/count", produces = "application/json")
    public ResponseEntity<CountResponse> count(
            @RequestParam("searchId")
            @NotBlank(message = "searchId must not be null or blank") String searchId) {
        CountSearchUseCase.CountResult result = countSearchUseCase.countBySearchId(searchId);
        return ResponseEntity.ok(CountResponse.from(result));
    }
}
