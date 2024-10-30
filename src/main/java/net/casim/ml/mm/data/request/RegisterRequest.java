package net.casim.ml.mm.data.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.casim.ml.mm.data.UserRole;

import java.util.List;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

    private List<String> roles;
}
