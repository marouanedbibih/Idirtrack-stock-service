package com.idirtrack.stock_service.device;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceRequeste;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/stock-api/device")

public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createDevice(@Valid @RequestBody DeviceRequeste deviceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
            BasicResponse response = BasicResponse.builder()
                    .messagesList(errors)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            BasicResponse response = deviceService.createDevice(deviceRequest, bindingResult);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getResponse());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse> getDevice(@PathVariable Long id) {
        try {
            BasicResponse response = deviceService.getDeviceById(id);
            return ResponseEntity.status(response.getStatus().value()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .data(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
}

