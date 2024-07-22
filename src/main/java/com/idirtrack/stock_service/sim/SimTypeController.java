package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.sim.https.SimTypeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/stock-api/sim-type")
public class SimTypeController {

    @Autowired
    private SimTypeService simTypeService;

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createSimType(@Valid @RequestBody SimTypeRequest simTypeRequest, BindingResult bindingResult) throws BasicException {
        BasicResponse response = simTypeService.createSimType(simTypeRequest, bindingResult);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
