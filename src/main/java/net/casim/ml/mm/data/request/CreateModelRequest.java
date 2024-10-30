package net.casim.ml.mm.data.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.casim.ml.mm.data.ModelLayer;

import java.util.List;

@Data
public class CreateModelRequest {
    @NotBlank(message = "Model name is required")
    private String modelName;
    @Size(min = 1, message = "At least one layer must be selected")
    private List<ModelLayer> layers;
    private String status;

}

