package com.example.habittracker.concurrency;

import com.example.habittracker.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/concurrency")
@Tag(name = "Concurrency Demos", description = "Race condition and thread-safety demos")
public class ConcurrencyDemoController {

    private final RaceConditionDemoService demoService;

    public ConcurrencyDemoController(RaceConditionDemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/race-demo")
    @Operation(summary = "Race condition demo", description = "Runs unsafe/synchronized/atomic counters under contention.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demo results returned",
                    content = @Content(schema = @Schema(implementation = RaceDemoResult.class))),
            @ApiResponse(responseCode = "400", description = "Bad request parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<RaceDemoResult>> raceDemo(
            @RequestParam(defaultValue = "50") @Min(1) @Max(500) int threads,
            @RequestParam(defaultValue = "200000") @Min(1) @Max(5_000_000) int tasks,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int trials
    ) {
        return ResponseEntity.ok(demoService.runDemo(threads, tasks, trials));
    }
}
