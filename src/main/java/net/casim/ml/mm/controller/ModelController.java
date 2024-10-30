package net.casim.ml.mm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.casim.ml.mm.data.LLMModel;
import net.casim.ml.mm.data.TrainingData;
import net.casim.ml.mm.data.request.CreateModelRequest;
import net.casim.ml.mm.repository.ModelRepository;
import net.casim.ml.mm.repository.TrainingDataRepository;
import net.casim.ml.mm.service.FileUploadService;
import net.casim.ml.mm.service.ModelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Tag(name = "Model Management", description = "APIs for managing machine learning models")
@RestController
@RequestMapping("/models")
public class ModelController {

    private final ModelService modelService;
    private final FileUploadService fileUploadService;
    private final TrainingDataRepository trainingDataRepository;

    public ModelController(ModelService modelService, FileUploadService fileUploadService, ModelRepository modelRepository, TrainingDataRepository trainingDataRepository) {
        this.modelService = modelService;
        this.fileUploadService = fileUploadService;
        this.trainingDataRepository = trainingDataRepository;
    }

    @Operation(summary = "Get all models", description = "Retrieve a list of all machine learning models")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Models"),
            @ApiResponse(responseCode = "400", description = "Exception")
    })
    @GetMapping
    public List<LLMModel> getAllModels() {
        return modelService.getAllModels();
    }

    @Operation(summary = "Create a new model", description = "Create a new machine learning model with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Model created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<String> createModel(@Valid @RequestBody CreateModelRequest request) {
        LLMModel model = modelService.createModel(request);
        return ResponseEntity.ok(model.getName() + " created successfully");
    }

    @Operation(summary = "Get model by ID", description = "Retrieve a specific model by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Model"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/{modelId}")
    public ResponseEntity<LLMModel> getModelById(@PathVariable UUID modelId) {
        LLMModel model = modelService.getModelById(modelId);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Train a model", description = "Initiate training of a specific model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training started"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/{modelId}/train")
    public ResponseEntity<String> trainModel(
            @PathVariable UUID modelId) {
        synchronized (this) {
            if (modelService.isTraining()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Training server is busy, try again later.");
            }
        }
        modelService.trainModel(modelId);
        return ResponseEntity.ok("Training started successfully!");
    }

    @Operation(summary = "Upload training data", description = "Upload a file to be used as training data for a model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment uploaded"),
            @ApiResponse(responseCode = "400", description = "Attachment can not updated")
    })
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/{modelId}/upload")
    public ResponseEntity<String> uploadAttachment(
            @PathVariable UUID modelId,
            @RequestParam(value = "file") MultipartFile file) {
        try {
            if (file == null)
                return ResponseEntity.badRequest().body("No file provided");

            LLMModel model = modelService.getModelById(modelId);
            File savedFile = fileUploadService.saveFile(file);

            TrainingData trainingData = new TrainingData();
            trainingData.setName(file.getOriginalFilename());
            trainingData.setSize(file.getSize());
            trainingData.setFilePath(savedFile.getAbsolutePath());
            trainingData.setUploadDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            trainingData.setModel(model);
            trainingDataRepository.save(trainingData);

            return ResponseEntity.ok("File uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Get training status", description = "Check the training status of a specific model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training Status"),
    })
    @GetMapping("/{modelId}/status")
    public String getTrainingStatus(@PathVariable UUID modelId) {
        return modelService.getTrainingStatus(modelId);
    }

    @Operation(summary = "Delete model", description = "Delete Model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status"),
    })
    @DeleteMapping("/{modelId}")
    public ResponseEntity<String> deleteModel(@PathVariable UUID modelId) {
        modelService.deleteModel(modelId);
        return ResponseEntity.ok("Model deleted successfully");
    }

    @DeleteMapping("/{modelId}/trainingData/{trainingDataId}")
    public ResponseEntity<?> deleteTD(@PathVariable UUID trainingDataId) {
        modelService.deleteTrainingData(trainingDataId);
        return ResponseEntity.ok().build();
    }

}
