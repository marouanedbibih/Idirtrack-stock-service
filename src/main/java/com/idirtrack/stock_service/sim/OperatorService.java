package com.idirtrack.stock_service.sim;

import java.sql.Date;
import java.util.List;
import java.util.Map;
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
public class OperatorService {

    @Autowired
    private OperatorRepository operatorRepository;

    public BasicResponse createSimType(@Valid SimTypeRequest request, BindingResult bindingResult) throws BasicException {
        // Validate the request
        Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
        if (!messagesList.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("Validation Error")
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Check if the SIM type already exists
        if (operatorRepository.existsByName(request.getType())) {
            messagesList.put("type", "SIM type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("SIM type already exists")
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.CONFLICT)
                    .redirectUrl(null)
                    .build());
        }

        // Transform the request to entity
        Operator simType = Operator.builder()
                .name(request.getType())
                .createdAt(new Date(System.currentTimeMillis()))
                .build();

        // Save the SIM type entity
        simType = operatorRepository.save(simType);

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
                        .messagesObject(messagesList)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.BAD_REQUEST)
                        .redirectUrl(null)
                        .build());
            }

            // Check if the SIM type already exists
            if (operatorRepository.existsByName(request.getType())) {
                messagesList.put("type", "SIM type with this name already exists");
                throw new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type already exists")
                        .messagesObject(messagesList)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.CONFLICT)
                        .redirectUrl(null)
                        .build());
            }
        }

        // Transform the request to entity and save
        List<Operator> simTypes = requests.stream()
                .map(request -> Operator.builder()
                        .name(request.getType())
                        .createdAt(new Date(System.currentTimeMillis()))
                        .build())
                .collect(Collectors.toList());

        // Save all SIM types
        simTypes = operatorRepository.saveAll(simTypes);

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
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }

        // Check if the SIM type exists
        Operator existingSimType = operatorRepository.findById(id)
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type not found")
                        .messagesObject(null)
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .redirectUrl(null)
                        .build()));

        if (operatorRepository.existsByName(request.getType()) && !existingSimType.getName().equals(request.getType())) {
            messagesList.put("type", "SIM type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("SIM type already exists")
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.CONFLICT)
                    .redirectUrl(null)
                    .build());
        }

        // Update the SIM type
        existingSimType.setName(request.getType());

        // Save the updated SIM type
        Operator updatedSimType = operatorRepository.save(existingSimType);

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
            Operator simType = operatorRepository.findById(id)
                    .orElseThrow(() -> new BasicException(BasicResponse.builder()
                            .content(null)
                            .message("SIM type not found: " + id)
                            .messagesObject(null)
                            .messageType(MessageType.ERROR)
                            .status(HttpStatus.NOT_FOUND)
                            .redirectUrl(null)
                            .build()));

            operatorRepository.delete(simType);
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
        List<Operator> simTypes = operatorRepository.findAll();
        return BasicResponse.builder()
                .content(simTypes)
                .message("SIM types retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    public BasicResponse getSimTypeById(Long id) throws BasicException {
        Operator simType = operatorRepository.findById(id)
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .content(null)
                        .message("SIM type not found")
                        .messagesObject(null)
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
