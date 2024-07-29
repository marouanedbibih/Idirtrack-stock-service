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

import org.springframework.beans.factory.annotation.Autowired;
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
        // private final SimTypeRepository simTypeRepository;
        private final SimStockRepository simStockRepository;
        private final StockRepository stockRepository;

        @Autowired
        private OperatorRepository operatorRepository;

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
        public BasicResponse createSim(@Valid SimRequest simRequest)
                        throws BasicException {
                // this.handleBindingErrors(bindingResult);

                // Check if phone number is already in use
                if (simRepository.existsByPhone(simRequest.getPhone())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .messageType(MessageType.ERROR)
                                        .messagesObject(Map.of("Phone", "Phone number already exists"))
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }
                // Check if CCID is already in use
                if (simRepository.existsByCcid(simRequest.getCcid())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .messageType(MessageType.ERROR)
                                        .messagesObject(Map.of("CCID", "CCID already exists"))
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }
                // Check if Operator exists
                Operator operator = operatorRepository.findById(simRequest.getOperatorId())
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .messageType(MessageType.ERROR)
                                                .messagesObject(Map.of("Operator", "Operator not found"))
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                // Save SIM
                Sim sim = Sim.builder()
                                .pin(simRequest.getPin())
                                .puk(simRequest.getPuk())
                                .ccid(simRequest.getCcid())
                                .operator(operator)
                                .phone(simRequest.getPhone())
                                .createdAt(new Date(System.currentTimeMillis()))
                                .status(SimStatus.PENDING)
                                .build();
                sim = simRepository.save(sim);
                // Update SIM stock
                updateSimStock(sim);
                // Build sim dto
                SimDTO simDTO = SimDTO.builder()
                                .id(sim.getId())
                                .pin(sim.getPin())
                                .puk(sim.getPuk())
                                .ccid(sim.getCcid())
                                .phone(sim.getPhone())
                                .status(sim.getStatus())
                                .createdAt(sim.getCreatedAt())
                                .operatorId(sim.getOperator().getId())
                                .operatorName(sim.getOperator().getName())
                                .build();
                // Return response
                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM created successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.CREATED)
                                .build();

                // if (simRepository.existsByPhoneNumber(simRequest.getPhoneNumber())
                // || simRepository.existsByCcid(simRequest.getCcid())) {
                // Map<String, String> messagesList = new HashMap<>();
                // if (simRepository.existsByCcid(simRequest.getCcid())) {
                // messagesList.put("CCID", "CCID already exists");
                // }
                // if (simRepository.existsByPhoneNumber(simRequest.getPhoneNumber())) {
                // messagesList.put("PhoneNumber", "Phone number already exists");
                // }
                // throw new BasicException(new BasicResponse(null, "Phone number or CCID
                // already exists",
                // messagesList, MessageType.ERROR, null, HttpStatus.CONFLICT, null));
                // }

                // SimType simType = simTypeRepository.findByType(simRequest.getSimType())
                // .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM type not
                // found",
                // null, MessageType.ERROR, null, HttpStatus.BAD_REQUEST, null)));

                // Sim sim = Sim.builder()
                // .pin(simRequest.getPin())
                // .puk(simRequest.getPuk())
                // .ccid(simRequest.getCcid())
                // .simType(simType)
                // .phoneNumber(simRequest.getPhoneNumber())
                // .addDate(new Date(System.currentTimeMillis()))
                // .status(SimStatus.PENDING)
                // .build();
                // System.out.println("SimService Date:" + sim.getAddDate());

                // simRepository.save(sim);
                // updateSimStock(sim);

                // return new BasicResponse(transformEntityToDTO(sim), "SIM created
                // successfully", null,
                // MessageType.SUCCESS, null, HttpStatus.CREATED, null);
        }

        // /**
        // * Update SIM stock service
        // *
        // * @param sim
        // * @return void
        // */
        private void updateSimStock(Sim sim) {
                List<Stock> stocks = stockRepository.findByDateEntree(sim.getCreatedAt());
                Stock stock = null;
                SimStock simStock = null;

                for (Stock s : stocks) {
                        // simStock = simStockRepository.findByStockAndSimType(s,
                        // sim.getOperator().getName());
                        simStock = simStockRepository.findByStockAndOperator(s, sim.getOperator());
                        if (simStock != null) {
                                stock = s;
                                break;
                        }
                }

                if (stock == null) {
                        stock = Stock.builder()
                                        .dateEntree(sim.getCreatedAt())
                                        .quantity(1)
                                        .build();
                        stock = stockRepository.save(stock);

                        simStock = SimStock.builder()
                                        .operator(sim.getOperator())
                                        .stock(stock)
                                        .build();
                        simStockRepository.save(simStock);
                } else {
                        stock.setQuantity(stock.getQuantity() + 1);
                        stockRepository.save(stock);
                }
        }

        // /**
        // * Update SIM infos service
        // *
        // * @param id
        // * @param simUpdateRequest
        // * @param bindingResult
        // * @return
        // * @throws BasicException
        // */
        public BasicResponse updateSim(Long id, SimUpdateRequest simUpdateRequest)
                        throws BasicException {

                // Check if SIM exists
                Sim existingSim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM not found", null,
                                                MessageType.ERROR, null, HttpStatus.NOT_FOUND, null)));

                // Check if the operator exists
                Operator operator = operatorRepository.findById(simUpdateRequest.getOperatorId())
                                .orElseThrow(() -> new BasicException(new BasicResponse(null, "SIM type not found",
                                                null, MessageType.ERROR, null, HttpStatus.BAD_REQUEST, null)));

                // Check if the phone number exixsts and is different from the current one
                if (!existingSim.getPhone().equals(simUpdateRequest.getPhone())
                                && simRepository.existsByPhone(simUpdateRequest.getPhone())) {
                        throw new BasicException(new BasicResponse(null, "Phone number already exists", null,
                                        MessageType.ERROR, null, HttpStatus.CONFLICT, null));
                }

                // Check if the CCID exists and is different from the current one
                if (!existingSim.getCcid().equals(simUpdateRequest.getCcid())
                                && simRepository.existsByCcid(simUpdateRequest.getCcid())) {
                        throw new BasicException(new BasicResponse(null, "CCID already exists", null,
                                        MessageType.ERROR,
                                        null, HttpStatus.CONFLICT, null));
                }

                // Update SIM
                existingSim.setPin(simUpdateRequest.getPin());
                existingSim.setPuk(simUpdateRequest.getPuk());
                existingSim.setCcid(simUpdateRequest.getCcid());
                existingSim.setOperator(operator);
                existingSim.setPhone(simUpdateRequest.getPhone());
                existingSim.setUpdatedAt(new Date(System.currentTimeMillis()));

                existingSim = simRepository.save(existingSim);
                // Build SIM DTO
                SimDTO simDTO = SimDTO.builder()
                                .id(existingSim.getId())
                                .pin(existingSim.getPin())
                                .puk(existingSim.getPuk())
                                .ccid(existingSim.getCcid())
                                .phone(existingSim.getPhone())
                                .status(existingSim.getStatus())
                                .createdAt(existingSim.getCreatedAt())
                                .operatorId(existingSim.getOperator().getId())
                                .operatorName(existingSim.getOperator().getName())
                                .build();
                // Return response
                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM updated successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        // /**
        // * Update SIM status service
        // *
        // * @param id
        // * @param status
        // * @return
        // * @throws BasicException
        // */
        // public BasicResponse updateSimStatus(Long id, SimStatus status) throws
        // BasicException {
        // Sim sim = simRepository.findById(id).orElseThrow(() -> new BasicException(new
        // BasicResponse(null,
        // "SIM not found", null, MessageType.ERROR, null, HttpStatus.NOT_FOUND,
        // null)));

        // sim.setStatus(status);
        // simRepository.save(sim);
        // return new BasicResponse(transformEntityToDTO(sim), "SIM status updated
        // successfully", null,
        // MessageType.SUCCESS, null, HttpStatus.OK, null);
        // }

        // /**
        // * Delete SIM service
        // *
        // * @param id
        // * @return
        // * @throws BasicException
        // */
        public BasicResponse deleteSim(Long id) throws BasicException {
                Sim sim = simRepository.findById(id).orElseThrow(() -> new BasicException(new BasicResponse(null,
                                "SIM not found", null, MessageType.ERROR, null, HttpStatus.NOT_FOUND,
                                null)));
                String phone = sim.getPhone();

                updateSimStockOnDelete(sim);
                simRepository.delete(sim);

                return BasicResponse.builder()
                                .content(null)
                                .message("SIM deleted successfully with phone number: " + phone)
                                .messageType(MessageType.WARNING)
                                .status(HttpStatus.OK)
                                .build();
        }

        // /**
        // * Update SIM stock on delete service
        // *
        // * @param sim
        // * @return void
        // */
        private void updateSimStockOnDelete(Sim sim) {
                List<Stock> stocks = stockRepository.findByDateEntree(sim.getCreatedAt());
                Stock stock = null;
                SimStock simStock = null;

                for (Stock s : stocks) {
                        simStock = simStockRepository.findByStockAndOperator(s, sim.getOperator());
                        if (simStock != null) {
                                stock = s;
                                break;
                        }

                }
        }

        // if (stock != null) {
        // stock.setQuantity(stock.getQuantity() - 1);
        // stockRepository.save(stock);

        // if (stock.getQuantity() <= 0) {
        // simStockRepository.delete(simStock);
        // stockRepository.delete(stock);
        // }
        // }
        // }

        // /**
        // * Get SIM by ID service
        // *
        // * @param id
        // * @return
        // * @throws BasicException
        // */
        public BasicResponse getSimById(Long id) throws BasicException {
                // Get SIM
                Sim sim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .message("SIM not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));
                // Build SIM DTO
                SimDTO simDTO = SimDTO.builder()
                                .id(sim.getId())
                                .pin(sim.getPin())
                                .puk(sim.getPuk())
                                .ccid(sim.getCcid())
                                .phone(sim.getPhone())
                                .status(sim.getStatus())
                                .createdAt(sim.getCreatedAt())
                                .operatorId(sim.getOperator().getId())
                                .operatorName(sim.getOperator().getName())
                                .build();
                // Return response
                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        // /**
        // * Get all SIMs with pagination service
        // *
        // * @param page
        // * @param size
        // * @return
        // */
        public BasicResponse getAllSims(int page, int size) {
                // Create page request
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAll(pageable);

                // Get SIMs
                List<SimDTO> simDTOs = simPage.getContent().stream().map(sim -> {
                        return SimDTO.builder()
                                        .id(sim.getId())
                                        .pin(sim.getPin())
                                        .puk(sim.getPuk())
                                        .ccid(sim.getCcid())
                                        .phone(sim.getPhone())
                                        .status(sim.getStatus())
                                        .createdAt(sim.getCreatedAt())
                                        .operatorId(sim.getOperator().getId())
                                        .operatorName(sim.getOperator().getName())
                                        .build();
                }).collect(Collectors.toList());

                // Build metadata
                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();
                // Build response
                return BasicResponse.builder()
                                .content(simDTOs)
                                .message("SIMs retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .metadata(metaData)
                                .build();

        }

        // /**
        // * Search SIMs with pagination
        // *
        // * @param query
        // * @param operatorType
        // * @param status
        // * @param date
        // * @param page
        // * @param size
        // * @return
        // */
        // public BasicResponse searchSims(String query, String operatorType, String
        // status, Date date, int page,
        // int size) {
        // Pageable pageable = PageRequest.of(page - 1, size);
        // Specification<Sim> specification = (root, criteriaQuery, criteriaBuilder) ->
        // {
        // List<Predicate> predicates = new ArrayList<>();
        // if (query != null && !query.isEmpty()) {
        // predicates.add(criteriaBuilder.or(
        // criteriaBuilder.like(root.get("pin"), "%" + query + "%"),
        // criteriaBuilder.like(root.get("puk"), "%" + query + "%"),
        // criteriaBuilder.like(root.get("ccid"), "%" + query + "%"),
        // criteriaBuilder.like(root.get("phoneNumber"), "%" + query + "%")));
        // }
        // if (operatorType != null && !operatorType.isEmpty()) {
        // predicates.add(criteriaBuilder.equal(root.get("simType").get("type"),
        // operatorType.toUpperCase()));
        // }
        // if (status != null && !status.isEmpty()) {
        // predicates.add(criteriaBuilder.equal(root.get("status"),
        // SimStatus.valueOf(status)));
        // }
        // if (date != null) {
        // predicates.add(criteriaBuilder.equal(root.get("addDate"), date));
        // }
        // return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        // };

        // Page<Sim> simPage = simRepository.findAll(specification, pageable);
        // if (simPage.isEmpty()) {
        // return new BasicResponse(null, "No SIMs found", null, MessageType.ERROR,
        // null,
        // HttpStatus.NOT_FOUND, null);
        // }

        // List<SimDTO> simDTOs =
        // simPage.getContent().stream().map(this::transformEntityToDTO)
        // .collect(Collectors.toList());
        // MetaData metaData = MetaData.builder()
        // .currentPage(simPage.getNumber() + 1)
        // .totalPages(simPage.getTotalPages())
        // .size(simPage.getSize())
        // .build();
        // Map<String, Object> data = new HashMap<>();
        // data.put("sims", simDTOs);
        // data.put("metadata", metaData);
        // return new BasicResponse(data, "SIMs retrieved successfully", null,
        // MessageType.SUCCESS, null,
        // HttpStatus.OK, metaData);
        // }

        // /**
        // * Search SIMs by date range
        // *
        // * @param startDate
        // * @param endDate
        // * @return
        // */
        // public BasicResponse searchSimsByDateRange(Date startDate, Date endDate) {
        // List<Sim> sims = simRepository.findByAddDateBetween(startDate, endDate);
        // if (sims.isEmpty()) {
        // return new BasicResponse(null, "No SIMs found in the specified date range",
        // null,
        // MessageType.ERROR, null, HttpStatus.NOT_FOUND, null);
        // }

        // List<SimDTO> simDTOs =
        // sims.stream().map(this::transformEntityToDTO).collect(Collectors.toList());
        // return new BasicResponse(simDTOs, "SIMs retrieved successfully", null,
        // MessageType.SUCCESS, null,
        // HttpStatus.OK, null);
        // }

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
        public BasicResponse getAllPendingSims(int page, int size) throws BasicException {
                // Create pagination request
                Pageable pageRequest = PageRequest.of(page - 1, size);

                // Find list of sims with status Pending
                Page<Sim> simPage = simRepository.findAllByStatus(SimStatus.PENDING,
                                pageRequest);

                // Return error if no SIMs found
                if (simPage.isEmpty()) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("No non-installed SIMs found")
                                        .messageType(MessageType.INFO)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build());
                }

                // Build list of SimBoitierDTO
                List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
                                .map(sim -> SimBoitierDTO.builder()
                                                .simMicroserviceId(sim.getId())
                                                .phone(sim.getPhone())
                                                .ccid(sim.getCcid())
                                                .operatorName(sim.getOperator().getName())
                                                .build())
                                .collect(Collectors.toList());

                // Build metadata content current page, total pages and size
                MetaData metadata = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();

                // Return response
                return BasicResponse.builder()
                                .content(simDTOs)
                                .status(HttpStatus.OK)
                                .metadata(metadata)
                                .build();
        }

        /**
        * Search non-installed SIMs by phone number or CCID
        *
        * @param query
        * @param page
        * @param size
        * @return
        */
        public BasicResponse searchPendingSims(String query, int page, int size) throws BasicException {
                // Create pagination
                Pageable pageable = PageRequest.of(page - 1, size);

                // Find SIMs with status Pending and phone number or CCID containing query
                Page<Sim> simPage = simRepository.findAllByStatusAndPhoneContainingOrCcidContaining(
                                SimStatus.PENDING, query, pageable);

                // Return error if no SIMs found
                if (simPage.isEmpty()) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("No non-installed SIMs found")
                                        .messageType(MessageType.INFO)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build());
                }

                // Build list of SimBoitierDTO
                List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
                                .map(sim -> SimBoitierDTO.builder()
                                                .simMicroserviceId(sim.getId())
                                                .phone(sim.getPhone())
                                                .ccid(sim.getCcid())
                                                .operatorName(sim.getOperator().getName())
                                                .build())
                                .collect(Collectors.toList());

                MetaData metadata = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();
                // Return response
                return BasicResponse.builder()
                                .content(simDTOs)
                                .status(HttpStatus.OK)
                                .metadata(metadata)
                                .build();

        }

        /**
         * Change SIM status to installed service
         *
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