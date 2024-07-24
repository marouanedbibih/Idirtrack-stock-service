package com.idirtrack.stock_service.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.device.https.DeviceTypeRequest;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/stock-api/device-types")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    // Save device type
    @PostMapping("/")
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

    //delete device type
    @DeleteMapping("/{id}")
    public ResponseEntity<BasicResponse> deleteDeviceType(@PathVariable Long id) {
        try {
            BasicResponse response = deviceTypeService.deleteDeviceType(id);
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
    //get all device types by pagination
    @GetMapping("/")
     public ResponseEntity<BasicResponse> getAllDeviceType(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = deviceTypeService.getAllDeviceTypes(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //update device type
    @PutMapping("/{id}")
    public ResponseEntity<BasicResponse> updateDeviceType(@PathVariable Long id, @Valid @RequestBody DeviceTypeRequest request, BindingResult bindingResult) {
        try {
            BasicResponse response = deviceTypeService.updateDeviceType(id, request, bindingResult);
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
