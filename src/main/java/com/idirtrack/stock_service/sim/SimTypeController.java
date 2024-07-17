package com.idirtrack.stock_service.sim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.sim.https.SimTypeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/stock-api")
public class SimTypeController {

    @Autowired
    private SimTypeService simTypeService;

    @PostMapping("/sim-type")
    public ResponseEntity<BasicResponse> createSimType(@Valid @RequestBody SimTypeRequest request, BindingResult bindingResult) {
        try {
            BasicResponse response = simTypeService.createSimType(request, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getResponse());
        }
    }
}
