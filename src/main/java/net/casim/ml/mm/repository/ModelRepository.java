package net.casim.ml.mm.repository;
import net.casim.ml.mm.data.LLMModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ModelRepository extends JpaRepository<LLMModel, UUID> {
}
