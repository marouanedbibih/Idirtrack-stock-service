package com.idirtrack.stock_service.device;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
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
import com.idirtrack.stock_service.device.https.DeviceRequest;
import com.idirtrack.stock_service.device.https.DeviceUpdateRequest;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;



@RestController
@RequestMapping("/stock-api")


public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // Save Device API
    @PostMapping("/device")
    public ResponseEntity<BasicResponse> createDeviceApi(@RequestBody @Valid DeviceRequest deviceRequest, BindingResult bindingResult) {
        try {
            BasicResponse response = deviceService.createDevice(deviceRequest, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                .data(null)
                .message(e.getMessage())
                .messageType(MessageType.ERROR)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
        }
    }

    // Update Device API
    @PutMapping("/{id}")
    public ResponseEntity<BasicResponse> updateDeviceApi(@PathVariable Long id, @RequestBody @Valid DeviceUpdateRequest deviceUpdateRequest, BindingResult bindingResult) {
        try {
            BasicResponse response = deviceService.updateDevice(id, deviceUpdateRequest, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                .data(null)
                .message(e.getMessage())
                .messageType(MessageType.ERROR)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
        }
    
    }

    // Delete Device API
    @DeleteMapping("/{id}")
    public ResponseEntity<BasicResponse> deleteDeviceApi(@PathVariable Long id) {
        try {
            BasicResponse response = deviceService.deleteDevice(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                .data(null)
                .message(e.getMessage())
                .messageType(MessageType.ERROR)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
        }
    }

    // Get Device by ID API
    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse> getDeviceApi(@PathVariable Long id) {
        try {
            BasicResponse response = deviceService.getDeviceById(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                .data(null)
                .message(e.getMessage())
                .messageType(MessageType.ERROR)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build());
        }
    }

    // Get All Devices API
    
    @GetMapping("/devices")
    public ResponseEntity<BasicResponse> getAllBoitiers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = deviceService.getAllDevices(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // Search Devices API

    @GetMapping("/search")
    public ResponseEntity<BasicResponse> searchDevicesApi(@RequestParam(value = "imei", required = false) String imei,
                                                          @RequestParam(value = "typeDevice", required = false) String typeDevice,
                                                          @RequestParam(value = "status", required = false) String status,
                                                          @RequestParam(value = "date", required = false) String dateString,
                                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        Date date = null;
        if (dateString != null && !dateString.isEmpty()) {
            try {
                date = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime());
            } catch (ParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BasicResponse.builder()
                    .data(null)
                    .message("Invalid date format")
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
            }
        }

        BasicResponse response = deviceService.searchDevices(imei, typeDevice, status, date, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
      

    // Count Devices by Status API
    @GetMapping("/non-install")
    public ResponseEntity<BasicResponse> countNonInstallDevicesApi() {
        BasicResponse response = deviceService.countDevicesNonInstalled();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
