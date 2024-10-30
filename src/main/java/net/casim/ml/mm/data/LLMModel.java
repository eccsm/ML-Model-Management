package net.casim.ml.mm.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.casim.ml.mm.utils.MapToJsonConverter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
public class LLMModel {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @ElementCollection(targetClass = ModelLayer.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "model_layers")
    @Column(name = "layer")
    private List<ModelLayer> layers;

    private String status;

    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> trainingResults;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingData> attachments;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TrainingData> trainingDataList;

    private int trainingDuration;

    private double accuracyPercentage;

}
