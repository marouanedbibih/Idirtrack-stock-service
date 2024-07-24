package com.idirtrack.stock_service.device;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceTypeRequest;

import jakarta.validation.Valid;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.device.https.DeviceTypeRequest;

import jakarta.validation.Valid;
import java.sql.Date;

@Service
public class DeviceTypeService {

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    // Save device type
    public BasicResponse createDeviceType(@Valid DeviceTypeRequest request, BindingResult bindingResult) throws BasicException {
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
                    .metadata(null)
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
                .content(deviceType)
                .message("Device type created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl("/device-types")
                .metadata(null)
                .build();
    }

    // Check if device type with the given name already exists
    private void ifExistsByName(String name) throws BasicException {
        if (deviceTypeRepository.existsByName(name)) {
            Map<String, String> messagesList = new HashMap<>();
            messagesList.put("name", "Device type with this name already exists");
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("Device type already exists")
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .metadata(null)
                    .build());
        }
    }

    // Transform request DTO to entity
    private DeviceType transformResponseDTO(DeviceTypeRequest request) {
        return DeviceType.builder()
                .name(request.getName())
                .build();
    }

    // Get all device types
    public List<DeviceType> getAllDeviceTypes() {
        return deviceTypeRepository.findAll();
    }


    // Delete device type by ID
    public BasicResponse deleteDeviceType(Long id) throws BasicException {
        // Check if the device type exists
        DeviceType deviceType = deviceTypeRepository.findById(id).orElseThrow(() -> new BasicException(BasicResponse.builder()
                .content(null)
                .message("Device type not found")
                .messageType(MessageType.ERROR)
                .status(HttpStatus.NOT_FOUND)
                .redirectUrl(null)
                .metadata(null)
                .build()));

        // Delete the device type
        deviceTypeRepository.delete(deviceType);

        // Return a success response
        return BasicResponse.builder()
                .content(null)
                .message("Device type deleted successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .redirectUrl("/device-types")
                .metadata(null)
                .build();
    }
   

    // Get all devices with pagination
    public BasicResponse getAllDeviceTypes(int page, int size) {
        // Create pagination
        Pageable pageRequest = PageRequest.of(page - 1, size);

        // Retrieve all device type from the database
        Page<DeviceType> devicePage = deviceTypeRepository.findAll(pageRequest);

        // Create a list of DTOs for devices
        List<DeviceType> deviceDTOs = devicePage.getContent().stream()
                .map(device -> DeviceType.builder().id(device.getId())
                        .name(device.getName())
                        .build())
                .collect(Collectors.toList());

        MetaData metaData = MetaData.builder()
                .currentPage(devicePage.getNumber() + 1)
                .totalPages(devicePage.getTotalPages())
                .size(devicePage.getSize())
                .build();

        // Map<String, Object> data = new HashMap<>();
        // data.put("devices", deviceDTOs);
        // data.put("metadata", metaData);

        // if device not found
        if (devicePage.isEmpty()) {
            return BasicResponse.builder()
                    .content(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No devices found")
                    .messageType(MessageType.ERROR)
                    .metadata(null)
                    .build();
        }
        return BasicResponse.builder()
                .content(deviceDTOs)
                .metadata(metaData)
                .status(HttpStatus.OK)
                .message("Devices retrieved successfully")
                .metadata(metaData)
                .build();
    }

    // Update device type by ID
    public BasicResponse updateDeviceType(Long id, @Valid DeviceTypeRequest request, BindingResult bindingResult) throws BasicException {
        // Validate the request
        Map<String, String> messagesList = BasicValidation.getValidationsErrors(bindingResult);
        if (!messagesList.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .content(null)
                    .message("Device type not be empty")
                    .messagesObject(messagesList)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .redirectUrl(null)
                    .metadata(null)
                    .build());
        }

        // Check if the device type exists
        DeviceType deviceType = deviceTypeRepository.findById(id).orElseThrow(() -> new BasicException(BasicResponse.builder()
                .content(null)
                .message("Device type not found")
                .messageType(MessageType.ERROR)
                .status(HttpStatus.NOT_FOUND)
                .redirectUrl(null)
                .metadata(null)
                .build()));

        // Check if the device type already exists
        try {
            ifExistsByName(request.getName());
        } catch (BasicException e) {
            return e.getResponse();
        }

        // Transform the request to entity
        deviceType = transformResponseDTO(request);
        deviceType.setId(id);
        deviceType.setName(request.getName());

        // Save the device type entity
        deviceType = deviceTypeRepository.save(deviceType);

        // Return a success response
        return BasicResponse.builder()
                .content(deviceType)
                .message("Device type updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .redirectUrl("/device-types")
                .metadata(null)
                .build();
    }
   
}
