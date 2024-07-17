package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.sim.https.*;
import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sims")
@CrossOrigin(origins = "http://localhost:3000")
public class SimController {

    @Autowired
    private SimService simService;

    @GetMapping
    public ResponseEntity<BasicResponse> getAllSims(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = simService.getAllSims(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse> getSimById(@PathVariable Long id) throws BasicException {
        BasicResponse response = simService.getSimById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<BasicResponse> createSim(@Valid @RequestBody SimRequest simRequest, BindingResult bindingResult) throws BasicException {
        BasicResponse response = simService.createSim(simRequest, bindingResult);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasicResponse> updateSim(@PathVariable Long id, @Valid @RequestBody SimUpdateRequest simUpdateRequest, BindingResult bindingResult) throws BasicException {
        BasicResponse response = simService.updateSim(id, simUpdateRequest, bindingResult);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BasicResponse> updateSimStatus(@PathVariable Long id, @RequestParam SimStatus status) throws BasicException {
        BasicResponse response = simService.updateSimStatus(id, status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BasicResponse> deleteSim(@PathVariable Long id) throws BasicException {
        BasicResponse response = simService.deleteSim(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<BasicResponse> searchSims(@RequestParam String query,
                                                    @RequestParam(required = false) String operatorType,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(required = false) LocalDateTime date,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        BasicResponse response = simService.searchSims(query, operatorType, status, date, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/searchByDate")
    public ResponseEntity<BasicResponse> searchSimsByDate(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        BasicResponse response = simService.searchSimsByDateRange(start, end);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
