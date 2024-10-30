package net.casim.ml.mm.repository;

import net.casim.ml.mm.data.TrainingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrainingDataRepository extends JpaRepository<TrainingData, UUID> {
    Optional<TrainingData> findByModelId(UUID modelId);
}
