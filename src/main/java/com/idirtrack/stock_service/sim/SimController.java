package com.idirtrack.stock_service.sim;

import java.sql.Date;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;

import org.springframework.http.HttpStatus;


import jakarta.validation.Valid;


@RestController
@RequestMapping("/stock-api/sim")
public class SimController {

    @Autowired
    private SimService simService;

    @GetMapping("/")
    public ResponseEntity<BasicResponse> getAllSims(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = simService.getAllSims(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}/")
    public ResponseEntity<BasicResponse> getSimById(@PathVariable Long id) throws BasicException {
        BasicResponse response = simService.getSimById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/")

    public ResponseEntity<BasicResponse> createSim(@Valid @RequestBody SimRequest simRequest,
            BindingResult bindingResult) throws BasicException {
        BasicResponse response = simService.createSim(simRequest, bindingResult);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}/")
    public ResponseEntity<BasicResponse> updateSim(@PathVariable Long id,
            @Valid @RequestBody SimUpdateRequest simUpdateRequest, BindingResult bindingResult) throws BasicException {
        BasicResponse response = simService.updateSim(id, simUpdateRequest, bindingResult);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}/status/")
    public ResponseEntity<BasicResponse> updateSimStatus(@PathVariable Long id, @RequestParam SimStatus status)
            throws BasicException {
        BasicResponse response = simService.updateSimStatus(id, status);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<BasicResponse> deleteSim(@PathVariable Long id) throws BasicException {
        BasicResponse response = simService.deleteSim(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<BasicResponse> searchSims(@RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "operatorType", required = false) String operatorType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "date", required = false) Date date,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        BasicResponse response = simService.searchSims(query, operatorType, status, date, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/search-by-date")
    public ResponseEntity<BasicResponse> searchSimsByDateRange(@RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate) {
        BasicResponse response = simService.searchSimsByDateRange(startDate, endDate);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/count-non-installed-sims")
    public ResponseEntity<BasicResponse> countNonInstalledSimsApi() {
        BasicResponse response = simService.countNonInstalledSims();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/non-installed-sims/")

    public ResponseEntity<BasicResponse> getNonInstalledSimsApi(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = simService.getAllNonInstalledSims(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Search non-installed SIMs by phone number or CCID
    @GetMapping("/non-installed-sims/search/")

    public ResponseEntity<BasicResponse> searchNonInstalledSimsApi(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        BasicResponse response = simService.searchNonInstalledSims(query, page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Change the status of SIM to installed
    @PutMapping("/status/installed/{id}/")
    public ResponseEntity<BasicResponse> changeSimStatusToInstalledApi(@PathVariable Long id) {
        try {
            BasicResponse response = simService.changeSimStatusInstalled(id);
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
