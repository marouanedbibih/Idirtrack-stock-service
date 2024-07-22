package com.idirtrack.stock_service.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceTypeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/stock-api")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    // Save device type
    @PostMapping("/type")
    public ResponseEntity<BasicResponse> createDeviceType(@Valid @RequestBody DeviceTypeRequest request, BindingResult bindingResult) {
        try {
            BasicResponse response = deviceTypeService.createDeviceType(request, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .metadata(null)
                    .build());
        }
    }
}
