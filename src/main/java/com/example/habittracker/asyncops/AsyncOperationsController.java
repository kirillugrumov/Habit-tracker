package com.example.habittracker.asyncops;

import com.example.habittracker.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async-operations")
@Tag(name = "Async Operations", description = "Asynchronous business operation demos")
public class AsyncOperationsController {

    private final AsyncBusinessOperationService service;

    public AsyncOperationsController(AsyncBusinessOperationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Start async operation", description = "Starts an async operation and returns a task id.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Task started",
                    content = @Content(schema = @Schema(implementation = StartAsyncOperationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<StartAsyncOperationResponse> start(@Valid @RequestBody StartAsyncOperationRequest request) {
        StartAsyncOperationResponse response = service.start(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get async operation status", description = "Returns status and result/error of a task.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status returned",
                    content = @Content(schema = @Schema(implementation = AsyncTaskSnapshot.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<AsyncTaskSnapshot> status(@PathVariable String taskId) {
        return ResponseEntity.ok(service.status(taskId));
    }
}
