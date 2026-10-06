package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.BatchDto;
import vallegrande.edu.pe.backend.model.Batch;
import vallegrande.edu.pe.backend.model.BatchInspection;
import vallegrande.edu.pe.backend.repository.BatchRepository;
import vallegrande.edu.pe.backend.repository.BatchInspectionRepository;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchRepository batchRepository;
    private final BatchInspectionRepository batchInspectionRepository;

    public Flux<BatchDto> findAll() {
        return batchRepository.findAll()
                .flatMap(batch -> batchInspectionRepository.findByBatchId(batch.getBatchId())
                        .map(inspection -> buildDto(batch, inspection))
                        .defaultIfEmpty(buildDto(batch, new BatchInspection(null, batch.getBatchId(), null))));
    }

    public Mono<BatchDto> findById(Integer id) {
        return batchRepository.findById(id)
                .flatMap(batch -> batchInspectionRepository.findByBatchId(id)
                        .map(inspection -> buildDto(batch, inspection))
                        .defaultIfEmpty(buildDto(batch, new BatchInspection(null, id, null))));
    }

    public Mono<BatchDto> save(BatchDto dto) {
        Batch batch = Batch.builder()
                .productId(dto.getProductId())
                .providerId(dto.getProviderId())
                .fieldName(dto.getFieldName())
                .harvestDate(dto.getHarvestDate())
                .packingDate(dto.getPackingDate())
                .productionDate(dto.getProductionDate())
                .expirationDate(dto.getExpirationDate())
                .build();

        return batchRepository.save(batch)
                .flatMap(savedBatch -> {
                    BatchInspection inspection = BatchInspection.builder()
                            .batchId(savedBatch.getBatchId())
                            .inspections(dto.getInspections())
                            .build();
                    return batchInspectionRepository.save(inspection)
                            .map(savedInspection -> buildDto(savedBatch, savedInspection));
                });
    }

    public Mono<BatchDto> update(Integer id, BatchDto dto) {
        return batchRepository.findById(id)
                .flatMap(existingBatch -> {
                    existingBatch.setProductId(dto.getProductId());
                    existingBatch.setProviderId(dto.getProviderId());
                    existingBatch.setFieldName(dto.getFieldName());
                    existingBatch.setHarvestDate(dto.getHarvestDate());
                    existingBatch.setPackingDate(dto.getPackingDate());
                    existingBatch.setProductionDate(dto.getProductionDate());
                    existingBatch.setExpirationDate(dto.getExpirationDate());
                    return batchRepository.save(existingBatch);
                })
                .flatMap(updatedBatch -> batchInspectionRepository.findByBatchId(id)
                        .flatMap(existingInspection -> {
                            existingInspection.setInspections(dto.getInspections());
                            return batchInspectionRepository.save(existingInspection);
                        })
                        .switchIfEmpty(batchInspectionRepository.save(BatchInspection.builder()
                                .batchId(id)
                                .inspections(dto.getInspections())
                                .build()))
                        .map(updatedInspection -> buildDto(updatedBatch, updatedInspection)));
    }

    private BatchDto buildDto(Batch batch, BatchInspection inspection) {
        return BatchDto.builder()
                .batchId(batch.getBatchId())
                .productId(batch.getProductId())
                .providerId(batch.getProviderId())
                .fieldName(batch.getFieldName())
                .harvestDate(batch.getHarvestDate())
                .packingDate(batch.getPackingDate())
                .productionDate(batch.getProductionDate())
                .expirationDate(batch.getExpirationDate())
                .inspections(inspection.getInspections())
                .build();
    }
}
