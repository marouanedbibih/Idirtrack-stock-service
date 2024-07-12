package com.idirtrack.stock_service.device;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceTypeRequeste;

import jakarta.validation.Valid;

@Service
public class DeviceTypeService {
  

  @Autowired
    private  DeviceTypeRepository deviceTypeRepository;

    // Save device type
    public BasicResponse createDeviceType(@Valid DeviceTypeRequeste request, BindingResult bindingResult) throws BasicException {
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

        // Check if the device type already exists
        try {
            ifExistsByName(request.getName());
        } catch (BasicException e) {
            return e.getResponse();
        }

        // Transform the request to entity
        DeviceType deviceType = transformResponseDTO(request);

        // Save the device type entity
        deviceType = deviceTypeRepository.save(deviceType);

        // Return a success response
        return BasicResponse.builder()
                .data(deviceType)
                .message("Device type created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl("/device-types")
                .build();
    }

    // Check if device type with the given name already exists
    private void ifExistsByName(String name) throws BasicException {
        if (deviceTypeRepository.existsByName(name)) {
            Map<String, String> messagesList = new HashMap<>();
            messagesList.put("name", "Device type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .data(null)
                    .message("Device type already exists")
                    .messagesList(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .build());
        }
    }

    // Transform request DTO to entity
    private DeviceType transformResponseDTO(DeviceTypeRequeste request) {
        return DeviceType.builder()
                .name(request.getName())
                // Set other fields if necessary
                .build();
    }
}
