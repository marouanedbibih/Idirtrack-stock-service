package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.sim.SimStatus;
import com.idirtrack.stock_service.sim.SimService;
import com.idirtrack.stock_service.sim.SimDTO;
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sims")
@CrossOrigin(origins = "http://localhost:3000")
public class SimController {

    @Autowired
    private SimService simService;

    @GetMapping
    public ResponseEntity<List<SimDTO>> getAllSims() {
        List<SimDTO> sims = simService.getAllSims();
        return new ResponseEntity<>(sims, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimDTO> getSimById(@PathVariable Long id) {
        SimDTO sim = simService.getSimById(id);
        return new ResponseEntity<>(sim, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SimDTO> createSim(@Valid @RequestBody SimRequest simRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SimDTO createdSim = simService.createSim(simRequest);
        return new ResponseEntity<>(createdSim, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SimDTO> updateSim(@PathVariable Long id, @Valid @RequestBody SimUpdateRequest simUpdateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SimDTO updatedSim = simService.updateSim(id, simUpdateRequest);
        return new ResponseEntity<>(updatedSim, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SimDTO> updateSimStatus(@PathVariable Long id, @RequestParam SimStatus status) {
        SimDTO updatedSim = simService.updateSimStatus(id, status);
        return new ResponseEntity<>(updatedSim, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSim(@PathVariable Long id) {
        simService.deleteSim(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SimDTO>> searchSims(@RequestParam String query) {
        List<SimDTO> sims = simService.searchSims(query);
        return new ResponseEntity<>(sims, HttpStatus.OK);
    }

    @GetMapping("/searchByDate")
    public ResponseEntity<List<SimDTO>> searchSimsByDate(@RequestParam String startDate, @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<SimDTO> sims = simService.searchSimsByDateRange(start, end);
        return new ResponseEntity<>(sims, HttpStatus.OK);
    }
}
