package com.idirtrack.stock_service.sim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.sim.https.SimTypeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/stock-api/operators")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createSimType(@Valid @RequestBody SimTypeRequest simTypeRequest, BindingResult bindingResult) {
        try {
            BasicResponse response = operatorService.createSimType(simTypeRequest, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<BasicResponse> createSimTypes(@Valid @RequestBody List<SimTypeRequest> simTypeRequests, BindingResult bindingResult) {
        try {
            BasicResponse response = operatorService.createSimTypes(simTypeRequests, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasicResponse> updateSimType(@PathVariable Long id, @Valid @RequestBody SimTypeRequest simTypeRequest, BindingResult bindingResult) {
        try {
            BasicResponse response = operatorService.updateSimType(id, simTypeRequest, bindingResult);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @DeleteMapping
    public ResponseEntity<BasicResponse> deleteSimTypes(@RequestParam List<Long> ids) {
        try {
            BasicResponse response = operatorService.deleteSimTypes(ids);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @GetMapping
    public ResponseEntity<BasicResponse> getAllSimTypes() {
        try {
            BasicResponse response = operatorService.getAllSimTypes();
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse> getSimTypeById(@PathVariable Long id) {
        try {
            BasicResponse response = operatorService.getSimTypeById(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder()
                    .content(null)
                    .message(e.getMessage())
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
}
