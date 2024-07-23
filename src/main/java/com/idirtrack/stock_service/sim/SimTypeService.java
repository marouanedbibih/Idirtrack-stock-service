package com.idirtrack.stock_service.sim;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.sim.https.SimTypeRequest;

import jakarta.validation.Valid;

@Service
public class SimTypeService {

    @Autowired
    private SimTypeRepository simTypeRepository;

    public BasicResponse createSimType(@Valid SimTypeRequest request, BindingResult bindingResult) throws BasicException {
        // Validate the request
        Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
        if (!messagesList.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("Validation Error")
                    .messagesList(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Check if the SIM type already exists
        if (simTypeRepository.existsByType(request.getType())) {
            messagesList.put("type", "SIM type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("SIM type already exists")
                    .messagesList(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Transform the request to entity
        SimType simType = SimType.builder()
                .type(request.getType())
                .createdAt(new Date(System.currentTimeMillis()))
                .build();

        // Save the SIM type entity
        simType = simTypeRepository.save(simType);

        // Return a success response
        return BasicResponse.builder()
                .content(simType)
                .message("SIM type created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl("/sim-types")
                .build();
    }

    public BasicResponse createSimTypes(@Valid List<SimTypeRequest> requests, BindingResult bindingResult) throws BasicException {
        // Validate each request
        for (SimTypeRequest request : requests) {
            Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
            if (!messagesList.isEmpty()) {
                throw new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("Validation Error")
                        .messagesList(messagesList)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.BAD_REQUEST)
                        .redirectUrl(null)
                        .build());
            }

            // Check if the SIM type already exists
            if (simTypeRepository.existsByType(request.getType())) {
                messagesList.put("type", "SIM type with this name already exists");
                throw new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type already exists")
                        .messagesList(messagesList)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.BAD_REQUEST)
                        .redirectUrl(null)
                        .build());
            }
        }

        // Transform the request to entity and save
        List<SimType> simTypes = requests.stream()
                .map(request -> SimType.builder()
                        .type(request.getType())
                        .createdAt(new Date(System.currentTimeMillis()))
                        .build())
                .collect(Collectors.toList());

        // Save all SIM types
        simTypes = simTypeRepository.saveAll(simTypes);

        // Return a success response
        return BasicResponse.builder()
                .content(simTypes)
                .message("SIM types created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl("/sim-types")
                .build();
    }

    public BasicResponse updateSimType(Long id, @Valid SimTypeRequest request, BindingResult bindingResult) throws BasicException {
        // Validate the request
        Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
        if (!messagesList.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("Validation Error")
                    .messagesList(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Check if the SIM type exists
        SimType existingSimType = simTypeRepository.findById(id)
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type not found")
                        .messagesList(null)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .redirectUrl(null)
                        .build()));

        if (simTypeRepository.existsByType(request.getType()) && !existingSimType.getType().equals(request.getType())) {
            messagesList.put("type", "SIM type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("SIM type already exists")
                    .messagesList(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Update the SIM type
        existingSimType.setType(request.getType());

        // Save the updated SIM type
        SimType updatedSimType = simTypeRepository.save(existingSimType);

        // Return a success response
        return BasicResponse.builder()
                .content(updatedSimType)
                .message("SIM type updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .redirectUrl("/sim-types")
                .build();
    }

    public BasicResponse deleteSimTypes(List<Long> ids) throws BasicException {
        // Check if each SIM type exists before deleting
        for (Long id : ids) {
            SimType simType = simTypeRepository.findById(id)
                    .orElseThrow(() -> new BasicException(BasicResponse.builder()
                            .content(null)
                            .message("SIM type not found: " + id)
                            .messagesList(null)
                            .messageType(MessageType.ERROR)
                            .status(HttpStatus.NOT_FOUND)
                            .redirectUrl(null)
                            .build()));

            simTypeRepository.delete(simType);
        }

        // Return a success response
        return BasicResponse.builder()
                .content(null)
                .message("SIM types deleted successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .redirectUrl("/sim-types")
                .build();
    }

    public BasicResponse getAllSimTypes() {
        List<SimType> simTypes = simTypeRepository.findAll();
        return BasicResponse.builder()
                .content(simTypes)
                .message("SIM types retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    public BasicResponse getSimTypeById(Long id) throws BasicException {
        SimType simType = simTypeRepository.findById(id)
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type not found")
                        .messagesList(null)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .redirectUrl(null)
                        .build()));
        return BasicResponse.builder()
                .content(simType)
                .message("SIM type retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }
}
