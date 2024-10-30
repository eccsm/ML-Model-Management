package net.casim.ml.mm.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Data
public class TrainingData {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private long size;

    private String uploadDate;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    @JsonIgnore
    private LLMModel model;

    private String filePath;

}

