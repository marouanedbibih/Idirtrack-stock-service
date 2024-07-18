package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;
import com.idirtrack.stock_service.stock.Stock;
import com.idirtrack.stock_service.stock.StockRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimService {

    private final SimRepository simRepository;
    private final SimTypeRepository simTypeRepository;
    private final SimStockRepository simStockRepository;
    private final StockRepository stockRepository;

    // Save SIM
    public BasicResponse createSim(@Valid SimRequest simRequest, BindingResult bindingResult) throws BasicException {

        // Validate the request
        Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid fields")
                    .messageType(MessageType.ERROR)
                    .data(errors)
                    .build());
        }

        // Check if the SIM already exists
        if (simRepository.existsByCcid(simRequest.getCcid())) {
            Map<String, String> messagesList = new HashMap<>();
            messagesList.put("CCID", "CCID already exists");
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("CCID already exists")
                    .messageType(MessageType.ERROR)
                    .data(messagesList)
                    .build());
        }

        // Check if the SIM type exists by name
        SimType simType = simTypeRepository.findByType(simRequest.getSimType())
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("SIM type not found")
                        .messageType(MessageType.ERROR)
                        .data(null)
                        .build()));

        // Transform the request to entity
        Sim sim = Sim.builder()
                .pin(simRequest.getPin())
                .puk(simRequest.getPuk())
                .ccid(simRequest.getCcid())
                .simType(simType)
                .phoneNumber(simRequest.getPhoneNumber())
                .addDate(new Date(System.currentTimeMillis()))
                .status(SimStatus.PENDING)
                .build();

        // Save the SIM entity
        simRepository.save(sim);

        // Update SIM stock
        updateSimStock(sim);

        // Transform the entity to DTO
        SimDTO simDTO = transformEntityToDTO(sim);

        // Return a success response
        return BasicResponse.builder()
                .data(simDTO)
                .message("SIM created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl(null)
                .build();
    }

    // Update SIM stock
    public void updateSimStock(Sim sim) {
        List<Stock> stocks = stockRepository.findByDateEntree(sim.getAddDate());
        Stock stock = null;
        SimStock simStock = null;

        for (Stock s : stocks) {
            simStock = simStockRepository.findByStockAndSimType(s, sim.getSimType());
            if (simStock != null) {
                stock = s;
                break;
            }
        }

        if (stock == null) {
            stock = Stock.builder()
                    .dateEntree(sim.getAddDate())
                    .quantity(1)
                    .build();
            stock = stockRepository.save(stock);

            simStock = SimStock.builder()
                    .simType(sim.getSimType())
                    .stock(stock)
                    .build();
            simStockRepository.save(simStock);
        } else {
            stock.setQuantity(stock.getQuantity() + 1);
            stockRepository.save(stock);
        }
    }

    // Update SIM
    public BasicResponse updateSim(Long id, @Valid SimUpdateRequest simUpdateRequest, BindingResult bindingResult) throws BasicException {

        // Validate the request
        Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
        if (!errors.isEmpty()) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Invalid fields")
                    .messageType(MessageType.ERROR)
                    .data(errors)
                    .build());
        }

        Sim existingSim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));

        // Check if the SIM type exists by name
        SimType simType = simTypeRepository.findByType(simUpdateRequest.getSimType())
                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("SIM type not found")
                        .messageType(MessageType.ERROR)
                        .data(null)
                        .build()));

        // Update the existing SIM entity
        existingSim.setPin(simUpdateRequest.getPin());
        existingSim.setPuk(simUpdateRequest.getPuk());
        existingSim.setCcid(simUpdateRequest.getCcid());
        existingSim.setSimType(simType);
        existingSim.setStatus(SimStatus.valueOf(simUpdateRequest.getStatus()));
        existingSim.setPhoneNumber(simUpdateRequest.getPhoneNumber());

        simRepository.save(existingSim);

        return BasicResponse.builder()
                .data(transformEntityToDTO(existingSim))
                .message("SIM updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Update SIM status
    public BasicResponse updateSimStatus(Long id, SimStatus status) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));

        sim.setStatus(status);
        simRepository.save(sim);
        return BasicResponse.builder()
                .data(transformEntityToDTO(sim))
                .message("SIM status updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Delete SIM
    public BasicResponse deleteSim(Long id) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));

        // Update SIM stock on delete
        updateSimStockOnDelete(sim);

        simRepository.delete(sim);

        return BasicResponse.builder()
                .message("SIM deleted successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Update SIM stock on delete
    private void updateSimStockOnDelete(Sim sim) {
        List<Stock> stocks = stockRepository.findByDateEntree(sim.getAddDate());
        Stock stock = null;
        SimStock simStock = null;

        for (Stock s : stocks) {
            simStock = simStockRepository.findByStockAndSimType(s, sim.getSimType());
            if (simStock != null) {
                stock = s;
                break;
            }
        }

        if (stock != null) {
            stock.setQuantity(stock.getQuantity() - 1);
            stockRepository.save(stock);

            if (stock.getQuantity() <= 0) {
                simStockRepository.delete(simStock);
                stockRepository.delete(stock);
            }
        }
    }

    // Get SIM by ID
    public BasicResponse getSimById(Long id) throws BasicException {
        Sim sim = simRepository.findById(id).orElseThrow(() ->
                new BasicException(BasicResponse.builder()
                        .data(null)
                        .message("SIM not found")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        return BasicResponse.builder()
                .data(transformEntityToDTO(sim))
                .message("SIM retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Get all SIMs with pagination
    public BasicResponse getAllSims(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Sim> simPage = simRepository.findAll(pageable);
        List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Search SIMs with pagination
    public BasicResponse searchSims(String query, String operatorType, String status, Date date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Sim> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("pin"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("puk"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("ccid"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("phoneNumber"), "%" + query + "%")
                ));
            }
            if (operatorType != null && !operatorType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("simType").get("type"), operatorType.toUpperCase()));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), SimStatus.valueOf(status)));
            }
            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("addDate"), date));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Sim> simPage = simRepository.findAll(specification, pageable);
        if (simPage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No SIMs found")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Search SIMs by date range
    public BasicResponse searchSimsByDateRange(Date startDate, Date endDate) {
        List<Sim> sims = simRepository.findByAddDateBetween(startDate, endDate);
        if (sims.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No SIMs found in the specified date range")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<SimDTO> simDTOs = sims.stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        return BasicResponse.builder()
                .data(simDTOs)
                .status(HttpStatus.OK)
                .message("SIMs retrieved successfully")
                .build();
    }

    // Count all non-installed SIMs
    public BasicResponse countNonInstalledSims() {
        long count = simRepository.countByStatus(SimStatus.PENDING);
        return BasicResponse.builder()
                .data(count)
                .status(HttpStatus.OK)
                .message("Non-installed SIMs count retrieved successfully")
                .build();
    }

    // Get all non-installed SIMs with pagination
    public BasicResponse getAllNonInstalledSims(int page, int size) {
        Pageable pageRequest = PageRequest.of(page - 1, size);
        Page<Sim> simPage = simRepository.findAllByStatus(SimStatus.PENDING, pageRequest);

        List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
                .map(sim -> SimBoitierDTO.builder()
                        .simMicroserviceId(sim.getId())
                        .phoneNumber(sim.getPhoneNumber())
                        .ccid(sim.getCcid())
                        .build())
                .collect(Collectors.toList());

        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);

        if (simPage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No non-installed SIMs found")
                    .messageType(MessageType.ERROR)
                    .build();
        }
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("Non-installed SIMs retrieved successfully")
                .build();
    }

    // Search non-installed SIMs by phone number or CCID with pagination
    public BasicResponse searchNonInstalledSims(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Sim> simPage = simRepository.findAllByStatusAndPhoneNumberContainingOrCcidContaining(SimStatus.PENDING, query, pageable);

        if (simPage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No non-installed SIMs found")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
                .map(sim -> SimBoitierDTO.builder()
                        .simMicroserviceId(sim.getId())
                        .phoneNumber(sim.getPhoneNumber())
                        .ccid(sim.getCcid())
                        .build())
                .collect(Collectors.toList());

        MetaData metaData = MetaData.builder()
                .currentPage(simPage.getNumber() + 1)
                .totalPages(simPage.getTotalPages())
                .size(simPage.getSize())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("sims", simDTOs);
        data.put("metadata", metaData);

        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("Non-installed SIMs retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .build();
    }

    // Transform entity to DTO
    private SimDTO transformEntityToDTO(Sim sim) {
        return SimDTO.builder()
                .id(sim.getId())
                .pin(sim.getPin())
                .puk(sim.getPuk())
                .ccid(sim.getCcid())
                .simType(sim.getSimType().getType())
                .status(sim.getStatus())
                .phoneNumber(sim.getPhoneNumber())
                .addDate(sim.getAddDate())
                .build();
    }
}
