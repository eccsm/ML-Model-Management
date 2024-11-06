package net.casim.ml.mm.service;

import lombok.extern.slf4j.Slf4j;
import net.casim.ml.mm.data.LLMModel;
import net.casim.ml.mm.data.TrainingData;
import net.casim.ml.mm.data.request.CreateModelRequest;
import net.casim.ml.mm.exception.ResourceNotFoundException;
import net.casim.ml.mm.repository.ModelRepository;
import net.casim.ml.mm.repository.TrainingDataRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ModelService {

    private final ModelRepository modelRepository;
    private final TrainingDataRepository trainingDataRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean isTraining = false;
    private final Random random = new Random();

    public ModelService(ModelRepository modelRepository, TrainingDataRepository trainingDataRepository) {
        this.modelRepository = modelRepository;
        this.trainingDataRepository = trainingDataRepository;
    }

    public List<LLMModel> getAllModels() {
        log.debug("Fetching all models");
        return modelRepository.findAll();
    }

    public LLMModel createModel(CreateModelRequest request) {
        log.info("Creating model with name: {}", request.getModelName());
        LLMModel model = new LLMModel();
        model.setName(request.getModelName());
        model.setLayers(request.getLayers());
        model.setStatus("Not Trained");
        LLMModel savedModel = modelRepository.save(model);
        log.info("Model '{}' created successfully with ID: {}", savedModel.getName(), savedModel.getId());
        return savedModel;
    }

    public LLMModel getModelById(UUID id) {
        log.debug("Fetching model by ID: {}", id);
        return modelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Model not found with ID: {}", id);
                    return new ResourceNotFoundException("Model not found with id: " + id);
                });
    }

    public String getTrainingStatus(UUID id) {
        LLMModel model = getModelById(id);
        log.debug("Model '{}' has status: {}", model.getName(), model.getStatus());
        return model.getStatus();
    }

    public void deleteModel(UUID id){
        LLMModel model = getModelById(id);
        TrainingData trainingData = trainingDataRepository.findByModelId(id).isPresent() ? trainingDataRepository.findByModelId(id).get() :null;

        if (trainingData != null) {
            trainingDataRepository.delete(trainingData);
        }
            modelRepository.delete(model);

        log.debug("Model deleted successfully");
    }

    public void deleteTrainingData(UUID id) {
        if (trainingDataRepository.findById(id).isPresent()) {
            TrainingData td = trainingDataRepository.findById(id).get();

            trainingDataRepository.delete(td);
            log.debug("Training Data deleted successfully");
        }
    }

    @Async
    public void trainModel(UUID modelId) {
        log.info("Received request to train model with ID: {}", modelId);

        int trainingDuration = 30 + random.nextInt(151);
        log.debug("Simulating training for {} seconds", trainingDuration);

        synchronized (this) {
            isTraining = true;
        }

        scheduler.schedule(() -> {
            try {
                LLMModel model = getModelById(modelId);
                TrainingData trainingData = trainingDataRepository.findByModelId(modelId)
                        .orElseThrow(() -> new ResourceNotFoundException("TrainingData not found with modelId: " + modelId));

                model.setStatus("Being Trained");
                modelRepository.save(model);
                trainingDataRepository.save(trainingData);

                log.info("Started training model '{}' with training data '{}' duration '{}' seconds", model.getName(), trainingData.getName(), trainingDuration );

                Thread.sleep(trainingDuration * 1000L);

                BigDecimal accuracy = BigDecimal.valueOf(70 + random.nextDouble() * 29)
                        .setScale(2, RoundingMode.HALF_UP);

                model.setStatus("Trained");
                model.setTrainingDuration(trainingDuration);
                model.setAccuracyPercentage(accuracy.doubleValue());
                modelRepository.save(model);
                trainingDataRepository.save(trainingData);

                log.info("Model '{}' training completed with training data '{}' , accuracy: {}%", model.getName(), trainingData.getName(),accuracy);
            } catch (InterruptedException e) {
                log.error("Training interrupted", e);
                Thread.currentThread().interrupt(); // Preserve interrupt status
            } catch (Exception e) {
                log.error("Unexpected error occurred during model training", e);
            } finally {
                synchronized (this) {
                    isTraining = false;
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    public boolean isTraining() {
        return isTraining;
    }

}
