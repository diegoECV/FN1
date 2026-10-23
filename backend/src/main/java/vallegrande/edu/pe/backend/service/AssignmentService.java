package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.AssignmentDto;
import vallegrande.edu.pe.backend.model.Assignment;
import vallegrande.edu.pe.backend.model.AssignmentTracking;
import vallegrande.edu.pe.backend.repository.AssignmentRepository;
import vallegrande.edu.pe.backend.repository.AssignmentTrackingRepository;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentTrackingRepository assignmentTrackingRepository;

    public Flux<AssignmentDto> findAll() {
        return assignmentRepository.findAll()
                .flatMap(assignment -> assignmentTrackingRepository.findByAssignmentId(assignment.getAssignmentId())
                        .map(tracking -> buildDto(assignment, tracking))
                        .defaultIfEmpty(buildDto(assignment, new AssignmentTracking(null, assignment.getAssignmentId(), null))));
    }

    public Mono<AssignmentDto> findById(Integer id) {
        return assignmentRepository.findById(id)
                .flatMap(assignment -> assignmentTrackingRepository.findByAssignmentId(id)
                        .map(tracking -> buildDto(assignment, tracking))
                        .defaultIfEmpty(buildDto(assignment, new AssignmentTracking(null, id, null))));
    }

    public Mono<AssignmentDto> save(AssignmentDto dto) {
        Assignment assignment = Assignment.builder()
                .orderId(dto.getOrderId())
                .assignmentType(dto.getAssignmentType())
                .assignedTo(dto.getAssignedTo())
                .assignedBy(dto.getAssignedBy())
                .priority(dto.getPriority())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .assignmentDate(dto.getAssignmentDate())
                .dueDate(dto.getDueDate())
                .completionDate(dto.getCompletionDate())
                .build();

        return assignmentRepository.save(assignment)
                .flatMap(savedAssignment -> {
                    AssignmentTracking tracking = AssignmentTracking.builder()
                            .assignmentId(savedAssignment.getAssignmentId())
                            .events(dto.getCustodyEvents())
                            .build();
                    return assignmentTrackingRepository.save(tracking)
                            .map(savedTracking -> buildDto(savedAssignment, savedTracking));
                });
    }

    public Mono<AssignmentDto> update(Integer id, AssignmentDto dto) {
        return assignmentRepository.findById(id)
                .flatMap(existingAssignment -> {
                    existingAssignment.setOrderId(dto.getOrderId());
                    existingAssignment.setAssignmentType(dto.getAssignmentType());
                    existingAssignment.setAssignedTo(dto.getAssignedTo());
                    existingAssignment.setAssignedBy(dto.getAssignedBy());
                    existingAssignment.setPriority(dto.getPriority());
                    existingAssignment.setStatus(dto.getStatus());
                    existingAssignment.setNotes(dto.getNotes());
                    existingAssignment.setAssignmentDate(dto.getAssignmentDate());
                    existingAssignment.setDueDate(dto.getDueDate());
                    existingAssignment.setCompletionDate(dto.getCompletionDate());
                    return assignmentRepository.save(existingAssignment);
                })
                .flatMap(updatedAssignment -> assignmentTrackingRepository.findByAssignmentId(id)
                        .flatMap(existingTracking -> {
                            existingTracking.setEvents(dto.getCustodyEvents());
                            return assignmentTrackingRepository.save(existingTracking);
                        })
                        .switchIfEmpty(assignmentTrackingRepository.save(AssignmentTracking.builder()
                                .assignmentId(id)
                                .events(dto.getCustodyEvents())
                                .build()))
                        .map(updatedTracking -> buildDto(updatedAssignment, updatedTracking)));
    }

    private AssignmentDto buildDto(Assignment assignment, AssignmentTracking tracking) {
        return AssignmentDto.builder()
                .assignmentId(assignment.getAssignmentId())
                .orderId(assignment.getOrderId())
                .assignmentType(assignment.getAssignmentType())
                .assignedTo(assignment.getAssignedTo())
                .assignedBy(assignment.getAssignedBy())
                .priority(assignment.getPriority())
                .status(assignment.getStatus())
                .notes(assignment.getNotes())
                .assignmentDate(assignment.getAssignmentDate())
                .dueDate(assignment.getDueDate())
                .completionDate(assignment.getCompletionDate())
                .custodyEvents(tracking.getEvents())
                .build();
    }
}
