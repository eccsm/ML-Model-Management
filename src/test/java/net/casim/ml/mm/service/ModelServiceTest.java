package net.casim.ml.mm.service;

import net.casim.ml.mm.data.LLMModel;
import net.casim.ml.mm.data.ModelLayer;
import net.casim.ml.mm.data.TrainingData;
import net.casim.ml.mm.data.request.CreateModelRequest;
import net.casim.ml.mm.exception.ResourceNotFoundException;
import net.casim.ml.mm.repository.ModelRepository;
import net.casim.ml.mm.repository.TrainingDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private TrainingDataRepository trainingDataRepository;

    @InjectMocks
    private ModelService modelService;

    private UUID modelId;
    private LLMModel model;
    private TrainingData trainingData;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        modelId = UUID.randomUUID();
        model = new LLMModel();
        model.setId(modelId);
        model.setName("Test Model");
        model.setStatus("Not Trained");

        // Set up TrainingData associated with the model
        trainingData = new TrainingData();
        trainingData.setModel(model);
        trainingData.setName("Sample Training Data");
    }

    @Test
    public void testGetAllModels() {
        when(modelRepository.findAll()).thenReturn(List.of(model));
        List<LLMModel> models = modelService.getAllModels();
        assertEquals(1, models.size());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    public void testCreateModel() {
        CreateModelRequest request = new CreateModelRequest();
        request.setModelName("Test Model");
        request.setLayers(List.of(ModelLayer.TEXT_CLASSIFIER, ModelLayer.VISUAL_CLASSIFIER));


        when(modelRepository.save(any(LLMModel.class))).thenReturn(model);
        LLMModel createdModel = modelService.createModel(request);

        assertNotNull(createdModel);
        assertEquals("Test Model", createdModel.getName());
        assertEquals("Not Trained", createdModel.getStatus());
        verify(modelRepository, times(1)).save(any(LLMModel.class));
    }

    @Test
    public void testGetModelByIdSuccess() {
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(model));
        LLMModel foundModel = modelService.getModelById(modelId);

        assertNotNull(foundModel);
        assertEquals("Test Model", foundModel.getName());
        verify(modelRepository, times(1)).findById(modelId);
    }

    @Test
    public void testGetModelByIdNotFound() {
        when(modelRepository.findById(modelId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> modelService.getModelById(modelId));
        verify(modelRepository, times(1)).findById(modelId);
    }

    @Test
    public void testGetTrainingStatus() {
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(model));
        String status = modelService.getTrainingStatus(modelId);

        assertEquals("Not Trained", status);
        verify(modelRepository, times(1)).findById(modelId);
    }

    @Test
    public void testTrainModel() throws Exception {
        when(modelRepository.findById(modelId)).thenReturn(Optional.of(model));
        when(trainingDataRepository.findByModelId(modelId)).thenReturn(Optional.of(trainingData));

        modelService.trainModel(modelId);

        Thread.sleep(100); // Small wait for async to kick off
        verify(modelRepository, atLeastOnce()).save(model);
        verify(trainingDataRepository, atLeastOnce()).save(trainingData);
    }
}
