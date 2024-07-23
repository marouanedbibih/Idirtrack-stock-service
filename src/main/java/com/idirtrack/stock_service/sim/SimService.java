package com.idirtrack.stock_service.sim;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.device.Device;
import com.idirtrack.stock_service.device.DeviceStatus;
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

        /**
         * Handle binding errors
         * 
         * @param bindingResult
         * @throws BasicException
         * @return void
         */
        private void handleBindingErrors(BindingResult bindingResult) throws BasicException {
                if (bindingResult.hasErrors()) {
                        Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
                        throw new BasicException(new BasicResponse(null, "Validation error", errors, MessageType.ERROR,
                                        null, HttpStatus.BAD_REQUEST, null));
                }
        }

        /**
         * Create SIM service
         * 
         * @param simRequest
         * @param bindingResult
         * @return
         * @throws BasicException
         */
        public BasicResponse createSim(@Valid SimRequest simRequest, BindingResult bindingResult)
                        throws BasicException {
                handleBindingErrors(bindingResult);

                if (simRepository.existsByPhoneNumber(simRequest.getPhoneNumber())
                                || simRepository.existsByCcid(simRequest.getCcid())) {
                        Map<String, String> messagesList = new HashMap<>();
                        if (simRepository.existsByCcid(simRequest.getCcid())) {
                                messagesList.put("CCID", "CCID already exists");
                        }
                        if (simRepository.existsByPhoneNumber(simRequest.getPhoneNumber())) {
                                messagesList.put("PhoneNumber", "Phone number already exists");
                        }
                        throw new BasicException(new BasicResponse(null, "Phone number or CCID already exists",
                                        messagesList, MessageType.ERROR, null, HttpStatus.CONFLICT, null));
                }

                SimType simType = simTypeRepository.findByType(simRequest.getSimType())
                                .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM type not found",
                                                null, MessageType.ERROR, null, HttpStatus.BAD_REQUEST, null)));

                Sim sim = Sim.builder()
                                .pin(simRequest.getPin())
                                .puk(simRequest.getPuk())
                                .ccid(simRequest.getCcid())
                                .simType(simType)
                                .phoneNumber(simRequest.getPhoneNumber())
                                .addDate(new Date(System.currentTimeMillis()))
                                .status(SimStatus.PENDING)
                                .build();

                simRepository.save(sim);
                updateSimStock(sim);

                return new BasicResponse(transformEntityToDTO(sim), "SIM created successfully", null,
                                MessageType.SUCCESS, null, HttpStatus.CREATED, null);
        }

        /**
         * Update SIM stock service
         * 
         * @param sim
         * @return void
         */
        private void updateSimStock(Sim sim) {
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

        /**
         * Update SIM infos service
         * 
         * @param id
         * @param simUpdateRequest
         * @param bindingResult
         * @return
         * @throws BasicException
         */
        public BasicResponse updateSim(Long id, @Valid SimUpdateRequest simUpdateRequest, BindingResult bindingResult)
                        throws BasicException {
                handleBindingErrors(bindingResult);

                Sim existingSim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM not found", null,
                                                MessageType.ERROR, null, HttpStatus.NOT_FOUND, null)));

                SimType simType = simTypeRepository.findByType(simUpdateRequest.getSimType())
                                .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM type not found",
                                                null, MessageType.ERROR, null, HttpStatus.BAD_REQUEST, null)));

                if (!existingSim.getPhoneNumber().equals(simUpdateRequest.getPhoneNumber())
                                && simRepository.existsByPhoneNumber(simUpdateRequest.getPhoneNumber())) {
                        throw new BasicException(new BasicResponse(null, "Phone number already exists", null,
                                        MessageType.ERROR, null, HttpStatus.CONFLICT, null));
                }

                if (!existingSim.getCcid().equals(simUpdateRequest.getCcid())
                                && simRepository.existsByCcid(simUpdateRequest.getCcid())) {
                        throw new BasicException(new BasicResponse(null, "CCID already exists", null, MessageType.ERROR,
                                        null, HttpStatus.CONFLICT, null));
                }

                existingSim.setPin(simUpdateRequest.getPin());
                existingSim.setPuk(simUpdateRequest.getPuk());
                existingSim.setCcid(simUpdateRequest.getCcid());
                existingSim.setSimType(simType);
                existingSim.setStatus(SimStatus.valueOf(simUpdateRequest.getStatus()));
                existingSim.setPhoneNumber(simUpdateRequest.getPhoneNumber());

                simRepository.save(existingSim);
                return new BasicResponse(transformEntityToDTO(existingSim), "SIM updated successfully", null,
                                MessageType.SUCCESS, null, HttpStatus.OK, null);
        }

        /**
         * Update SIM status service
         * 
         * @param id
         * @param status
         * @return
         * @throws BasicException
         */
        public BasicResponse updateSimStatus(Long id, SimStatus status) throws BasicException {
                Sim sim = simRepository.findById(id).orElseThrow(() -> new BasicException(new BasicResponse(null,
                                "SIM not found", null, MessageType.ERROR, null, HttpStatus.NOT_FOUND, null)));

                sim.setStatus(status);
                simRepository.save(sim);
                return new BasicResponse(transformEntityToDTO(sim), "SIM status updated successfully", null,
                                MessageType.SUCCESS, null, HttpStatus.OK, null);
        }

        /**
         * Delete SIM service
         * 
         * @param id
         * @return
         * @throws BasicException
         */
        public BasicResponse deleteSim(Long id) throws BasicException {
                Sim sim = simRepository.findById(id).orElseThrow(() -> new BasicException(new BasicResponse(null,
                                "SIM not found", null, MessageType.ERROR, null, HttpStatus.NOT_FOUND, null)));

                updateSimStockOnDelete(sim);
                simRepository.delete(sim);

                return new BasicResponse(null, "SIM deleted successfully", null, MessageType.SUCCESS, null,
                                HttpStatus.OK, null);
        }

        /**
         * Update SIM stock on delete service
         * 
         * @param sim
         * @return void
         */
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

        /**
         * Get SIM by ID service
         * 
         * @param id
         * @return
         * @throws BasicException
         */
        public BasicResponse getSimById(Long id) throws BasicException {
                Sim sim = simRepository.findById(id).orElseThrow(() -> new BasicException(new BasicResponse(null,
                                "SIM not found", null, MessageType.ERROR, null, HttpStatus.NOT_FOUND, null)));
                return new BasicResponse(transformEntityToDTO(sim), "SIM retrieved successfully", null,
                                MessageType.SUCCESS, null, HttpStatus.OK, null);
        }

        /**
         * Get all SIMs with pagination service
         * 
         * @param page
         * @param size
         * @return
         */
        public BasicResponse getAllSims(int page, int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAll(pageable);
                List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO)
                                .collect(Collectors.toList());
                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();
                Map<String, Object> data = new HashMap<>();
                data.put("sims", simDTOs);
                data.put("metadata", metaData);
                return new BasicResponse(data, "SIMs retrieved successfully", null, MessageType.SUCCESS, null,
                                HttpStatus.OK, metaData);
        }

        /**
         * Search SIMs with pagination
         * 
         * @param query
         * @param operatorType
         * @param status
         * @param date
         * @param page
         * @param size
         * @return
         */
        public BasicResponse searchSims(String query, String operatorType, String status, Date date, int page,
                        int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Specification<Sim> specification = (root, criteriaQuery, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (query != null && !query.isEmpty()) {
                                predicates.add(criteriaBuilder.or(
                                                criteriaBuilder.like(root.get("pin"), "%" + query + "%"),
                                                criteriaBuilder.like(root.get("puk"), "%" + query + "%"),
                                                criteriaBuilder.like(root.get("ccid"), "%" + query + "%"),
                                                criteriaBuilder.like(root.get("phoneNumber"), "%" + query + "%")));
                        }
                        if (operatorType != null && !operatorType.isEmpty()) {
                                predicates.add(criteriaBuilder.equal(root.get("simType").get("type"),
                                                operatorType.toUpperCase()));
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
                        return new BasicResponse(null, "No SIMs found", null, MessageType.ERROR, null,
                                        HttpStatus.NOT_FOUND, null);
                }

                List<SimDTO> simDTOs = simPage.getContent().stream().map(this::transformEntityToDTO)
                                .collect(Collectors.toList());
                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();
                Map<String, Object> data = new HashMap<>();
                data.put("sims", simDTOs);
                data.put("metadata", metaData);
                return new BasicResponse(data, "SIMs retrieved successfully", null, MessageType.SUCCESS, null,
                                HttpStatus.OK, metaData);
        }

        /**
         * Search SIMs by date range
         * 
         * @param startDate
         * @param endDate
         * @return
         */
        public BasicResponse searchSimsByDateRange(Date startDate, Date endDate) {
                List<Sim> sims = simRepository.findByAddDateBetween(startDate, endDate);
                if (sims.isEmpty()) {
                        return new BasicResponse(null, "No SIMs found in the specified date range", null,
                                        MessageType.ERROR, null, HttpStatus.NOT_FOUND, null);
                }

                List<SimDTO> simDTOs = sims.stream().map(this::transformEntityToDTO).collect(Collectors.toList());
                return new BasicResponse(simDTOs, "SIMs retrieved successfully", null, MessageType.SUCCESS, null,
                                HttpStatus.OK, null);
        }

        /**
         * Count non-installed SIMs service
         * 
         * @return BasicResponse
         */
        public BasicResponse countNonInstalledSims() {
                long count = simRepository.countByStatus(SimStatus.PENDING);
                return new BasicResponse(count, "Non-installed SIMs count retrieved successfully", null,
                                MessageType.SUCCESS, null, HttpStatus.OK, null);
        }

        /**
         * Get all non-installed SIMs with pagination service
         * 
         * @param page
         * @param size
         * @return
         */
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

                return new BasicResponse(data, "Non-installed SIMs retrieved successfully", null, MessageType.SUCCESS,
                                null, HttpStatus.OK, metaData);
        }

        /**
         * Search non-installed SIMs by phone number or CCID
         * 
         * @param query
         * @param page
         * @param size
         * @return
         */
        public BasicResponse searchNonInstalledSims(String query, int page, int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAllByStatusAndPhoneNumberContainingOrCcidContaining(
                                SimStatus.PENDING, query, pageable);

                if (simPage.isEmpty()) {
                        return new BasicResponse(null, "No non-installed SIMs found", null, MessageType.ERROR, null,
                                        HttpStatus.NOT_FOUND, null);
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

                return new BasicResponse(data, "Non-installed SIMs retrieved successfully", null, MessageType.SUCCESS,
                                null, HttpStatus.OK, metaData);
        }

        /**
         * Transform SIM entity to DTO
         * @param sim
         * @return
         */
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

        /**
         * Change SIM status to installed service
         * @param id
         * @return
         * @throws BasicException
         */
        public BasicResponse changeSimStatusInstalled(Long id) throws BasicException {
                Sim sim = simRepository.findById(id).orElse(null);
                if (sim == null) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("Sim not found")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build());
                }

                sim.setStatus(SimStatus.ONLINE);
                sim = simRepository.save(sim);

                return BasicResponse.builder()
                                .content(sim)
                                .message("Device status changed to installed successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }
}
