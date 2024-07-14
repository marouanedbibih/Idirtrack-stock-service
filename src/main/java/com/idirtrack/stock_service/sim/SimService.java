package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.sim.SimRepository;
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimService {

    private final SimRepository simRepository;

    // Save SIM
    public BasicResponse createSim(@Valid SimRequest simRequest, BindingResult bindingResult) throws BasicException {

        // Validate the request
        Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid fields")
                    .messageType(MessageType.ERROR)
                    .data(errors)
                    .build());
        }

        // Check if the SIM already exists
        if (simRepository.existsByCcid(simRequest.getCcid())) {
            Map<String, String> messagesList = new HashMap<>();
            messagesList.put("CCID", "CCID already exists");
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("CCID already exists")
                    .messageType(MessageType.ERROR)
                    .data(messagesList)
                    .build());
        }

        // Transform the request to entity
        Sim sim = transformRequestToEntity(simRequest);

        // Save the SIM entity
        simRepository.save(sim);

        // Transform the entity to DTO
        SimDTO simDTO = transformEntityToDTO(sim);

        // Return a success response
        return BasicResponse.builder()
                .data(simDTO)
                .message("SIM created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl(null)
                .build();
    }

    // Update SIM
    public BasicResponse updateSim(Long id, @Valid SimUpdateRequest simUpdateRequest, BindingResult bindingResult) throws BasicException {

        // Validate the request
        Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid fields")
                    .messageType(MessageType.ERROR)
                    .data(errors)
                    .build());
        }

        Sim existingSim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));

        // Update the existing SIM entity
        existingSim.setPin(simUpdateRequest.getPin());
        existingSim.setPuk(simUpdateRequest.getPuk());
        existingSim.setCcid(simUpdateRequest.getCcid());
        existingSim.setOperatorType(simUpdateRequest.getOperatorType().toUpperCase());
        existingSim.setStatus(simUpdateRequest.getStatus());
        existingSim.setPhoneNumber(simUpdateRequest.getPhoneNumber());
        existingSim.setAddDate(simUpdateRequest.getAddDate());

        simRepository.save(existingSim);

        return BasicResponse.builder()
                .data(transformEntityToDTO(existingSim))
                .message("SIM updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Update SIM status
    public BasicResponse updateSimStatus(Long id, SimStatus status) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        sim.setStatus(status);
        simRepository.save(sim);
        return BasicResponse.builder()
                .data(transformEntityToDTO(sim))
                .message("SIM status updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Delete SIM
    public BasicResponse deleteSim(Long id) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        simRepository.delete(sim);
        return BasicResponse.builder()
                .message("SIM deleted successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Get SIM by ID
    public BasicResponse getSimById(Long id) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        return BasicResponse.builder()
                .data(transformEntityToDTO(sim))
                .message("SIM retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Get all SIMs with pagination
    public BasicResponse getAllSims(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Sim> simPage = simRepository.findAll(pageable);
        List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Search SIMs with pagination
    public BasicResponse searchSims(String query, String operatorType, String status, LocalDateTime date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Sim> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("pin"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("puk"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("ccid"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("phoneNumber"), "%" + query + "%")
                ));
            }
            if (operatorType != null && !operatorType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("operatorType"), operatorType.toUpperCase()));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), SimStatus.valueOf(status)));
            }
            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("addDate"), date));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Sim> simPage = simRepository.findAll(specification, pageable);
        if (simPage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No SIMs found")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Search SIMs by date range
    public BasicResponse searchSimsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sim> sims = simRepository.findByAddDateBetween(startDate, endDate);
        if (sims.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No SIMs found")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<SimDTO> simDTOs = sims.stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        return BasicResponse.builder()
                .data(simDTOs)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Transform request to entity
    private Sim transformRequestToEntity(SimRequest simRequest) {
        return Sim.builder()
                .pin(simRequest.getPin())
                .puk(simRequest.getPuk())
                .ccid(simRequest.getCcid())
                .operatorType(simRequest.getOperatorType().toUpperCase())
                .phoneNumber(simRequest.getPhoneNumber())
                .addDate(simRequest.getAddDate())
                .status(SimStatus.PENDING)
                .build();
    }

    // Transform entity to DTO
    private SimDTO transformEntityToDTO(Sim sim) {
        return SimDTO.builder()
                .id(sim.getId())
                .pin(sim.getPin())
                .puk(sim.getPuk())
                .ccid(sim.getCcid())
                .operatorType(sim.getOperatorType())
                .status(sim.getStatus())
                .phoneNumber(sim.getPhoneNumber())
                .addDate(sim.getAddDate())
                .build();
    }
}
