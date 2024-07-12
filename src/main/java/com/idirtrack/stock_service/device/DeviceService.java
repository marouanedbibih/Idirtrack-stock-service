package com.idirtrack.stock_service.device;

import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceRequeste;



import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;


    // Save device
    public BasicResponse createDevice(@Valid DeviceRequeste deviceRequest, BindingResult bindingResult) throws BasicException{

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

        // Check if the device already exists
        try {
            ifExists(deviceRequest.getIMEI());
        } catch (BasicException e) {
            throw e;
        }
        

        // Transform the request to entity
        DeviceDTO device = transformRequestDTO(deviceRequest);

        // Save the device entity
        deviceRepository.save(transformResponseDTO(device));
        // Return a success response
        return BasicResponse.builder()
                .data(device)
                .message("Device created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl(null)
                .build();
    }

    // Transform  DTO to entity
    public Device transformResponseDTO(DeviceDTO deviceDTO) {
        DeviceType deviceType = deviceTypeRepository.findByName(deviceDTO.getDeviceType());
        return Device.builder()
        //convert to long
                .IMEI(deviceDTO.getIMEI().longValue())
                .status(DeviceStatus.valueOf(deviceDTO.getStatus()))
                .createdAt(new Date())
                .deviceType(deviceType)
                .build();
    }

    //transform request to DTO
    public DeviceDTO transformRequestDTO(DeviceRequeste deviceRequest) {
        return DeviceDTO.builder()
                .IMEI(deviceRequest.getIMEI())
                .status(deviceRequest.getStatus())
                .deviceType(deviceRequest.getTypeDevice())
                .build();
    }

   // Check if device with the given IMEI already exists
public void ifExists(Number n) throws BasicException {
    if (deviceRepository.existsByImei(n)) { // Pass long IMEI
        Map<String, String> messagesList = new HashMap<>();
        messagesList.put("IMEI", "IMEI already exists");
        throw new BasicException(BasicResponse.builder()
                .data(null)
                .message("IMEI already exists")
                .messagesList(messagesList)
                .build());
    }
}
    //get device by id
    public BasicResponse getDeviceById(Long id) throws BasicException {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new BasicException(BasicResponse.builder()
                    .data(null)
                    .message("Device not found")
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }
        return BasicResponse.builder()
                .data(device)
                .message("Device found")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }
}
