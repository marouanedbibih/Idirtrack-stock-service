package com.idirtrack.stock_service.sim;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;

@Service
public class SimService {

    @Autowired
    private SimRepository simRepository;

    public List<SimDTO> getAllSims() {
        return simRepository.findAll().stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    public SimDTO getSimById(Long id) {
        Sim sim = simRepository.findById(id).orElseThrow(() -> new RuntimeException("SIM not found"));
        return transformToDTO(sim);
    }

    public SimDTO createSim(SimRequest simRequest) {
        Sim sim = transformToEntity(simRequest);
        Sim savedSim = simRepository.save(sim);
        return transformToDTO(savedSim);
    }

    public SimDTO updateSim(Long id, SimUpdateRequest simUpdateRequest) {
        Sim existingSim = simRepository.findById(id).orElseThrow(() -> new RuntimeException("SIM not found"));
        existingSim.setPin(simUpdateRequest.getPin());
        existingSim.setPuk(simUpdateRequest.getPuk());
        existingSim.setCcid(simUpdateRequest.getCcid());
        existingSim.setOperatorType(simUpdateRequest.getOperatorType().toUpperCase());
        existingSim.setStatus(simUpdateRequest.getStatus());
        existingSim.setPhoneNumber(simUpdateRequest.getPhoneNumber());
        existingSim.setAddDate(simUpdateRequest.getAddDate());
        Sim updatedSim = simRepository.save(existingSim);
        return transformToDTO(updatedSim);
    }

    public SimDTO updateSimStatus(Long id, SimStatus status) {
        Sim sim = simRepository.findById(id).orElseThrow(() -> new RuntimeException("SIM not found"));
        sim.setStatus(status);
        Sim updatedSim = simRepository.save(sim);
        return transformToDTO(updatedSim);
    }

    public void deleteSim(Long id) {
        simRepository.deleteById(id);
    }

    public List<SimDTO> searchSims(String query) {
        return simRepository.findByAnyFieldContaining(query).stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    public List<SimDTO> searchSimsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return simRepository.findByAddDateBetween(startDate, endDate).stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    private Sim transformToEntity(SimRequest simRequest) {
        return Sim.builder()
                .pin(simRequest.getPin())
                .puk(simRequest.getPuk())
                .ccid(simRequest.getCcid())
                .operatorType(simRequest.getOperatorType().toUpperCase())
                .phoneNumber(simRequest.getPhoneNumber())
                .addDate(simRequest.getAddDate())
                .status(SimStatus.PENDING)
                .build();
    }

    private SimDTO transformToDTO(Sim sim) {
        return SimDTO.builder()
                .id(sim.getId())
                .pin(sim.getPin())
                .puk(sim.getPuk())
                .ccid(sim.getCcid())
                .operatorType(sim.getOperatorType())
                .status(sim.getStatus())
                .phoneNumber(sim.getPhoneNumber())
                .addDate(sim.getAddDate())
                .build();
    }
}