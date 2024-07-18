package com.idirtrack.stock_service.sim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.sim.https.SimTypeRequest;

import java.util.Map;

@Service
public class SimTypeService {

    @Autowired
    private SimTypeRepository simTypeRepository;

    public BasicResponse createSimType(SimTypeRequest request, BindingResult bindingResult) throws BasicException {
        // Validate the request
        Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
        if (!messagesList.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .data(null)
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
                    .data(null)
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
                .build();

        // Save the SIM type entity
        simType = simTypeRepository.save(simType);

        // Return a success response
        return BasicResponse.builder()
                .data(simType)
                .message("SIM type created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl("/sim-types")
                .build();
    }
}
