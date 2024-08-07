package com.idirtrack.stock_service.sim;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;
import com.idirtrack.stock_service.stock.Stock;
import com.idirtrack.stock_service.stock.StockRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimService {

        private final SimRepository simRepository;
        private final SimStockRepository simStockRepository;
        private final StockRepository stockRepository;

        @Autowired
        private OperatorRepository operatorRepository;

        /**
         * CREATE NEW SIM
         * 
         * @param simRequest
         * @return
         * @throws BasicException
         */
        public BasicResponse createSim(@Valid SimRequest simRequest) throws BasicException {
                if (simRepository.existsByPhone(simRequest.getPhone())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .messageType(MessageType.ERROR)
                                        .messagesObject(Map.of("Phone", "Phone number already exists"))
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }
                if (simRepository.existsByCcid(simRequest.getCcid())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .messageType(MessageType.ERROR)
                                        .messagesObject(Map.of("CCID", "CCID already exists"))
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }
                Operator operator = operatorRepository.findById(simRequest.getOperatorId())
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .messageType(MessageType.ERROR)
                                                .messagesObject(Map.of("Operator", "Operator not found"))
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                Sim sim = Sim.builder()
                                .pin(simRequest.getPin())
                                .puk(simRequest.getPuk())
                                .ccid(simRequest.getCcid())
                                .operator(operator)
                                .phone(simRequest.getPhone())
                                .createdAt(new Date(System.currentTimeMillis()))
                                .status(SimStatus.NON_INSTALLED)
                                .build();
                sim = simRepository.save(sim);
                updateSimStock(sim);

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
                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM created successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.CREATED)
                                .build();
        }

        private void updateSimStock(Sim sim) {
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

                if (stock != null) {
                        stock.setQuantity(stock.getQuantity() - 1);
                        stockRepository.save(stock);

                        if (stock.getQuantity() <= 0) {
                                simStockRepository.delete(simStock);
                                stockRepository.delete(stock);
                        }
                }
        }

        public BasicResponse getSimById(Long id) throws BasicException {
                Sim sim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .message("SIM not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

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
                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        public BasicResponse getAllSims(int page, int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAll(pageable);

                List<SimDTO> simDTOs = simPage.getContent().stream().map(sim -> SimDTO.builder()
                                .id(sim.getId())
                                .pin(sim.getPin())
                                .puk(sim.getPuk())
                                .ccid(sim.getCcid())
                                .phone(sim.getPhone())
                                .status(sim.getStatus())
                                .createdAt(sim.getCreatedAt())
                                .operatorId(sim.getOperator().getId())
                                .operatorName(sim.getOperator().getName())
                                .build()).collect(Collectors.toList());

                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();

                return BasicResponse.builder()
                                .content(simDTOs)
                                .message("SIMs retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .metadata(metaData)
                                .build();
        }

        public BasicResponse searchSIMsByPhoneAndCCID(String term, int page, int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAllByStatusAndPhoneContainingOrCcidContaining(SimStatus.PENDING,
                                term, pageable);

                if (simPage.isEmpty()) {
                        return BasicResponse.builder()
                                        .content(null)
                                        .message("No SIM cards found with the provided phone number or CCID.")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build();
                }

                List<SimDTO> simDTOs = simPage.getContent().stream().map(sim -> SimDTO.builder()
                                .id(sim.getId())
                                .pin(sim.getPin())
                                .puk(sim.getPuk())
                                .ccid(sim.getCcid())
                                .phone(sim.getPhone())
                                .status(sim.getStatus())
                                .createdAt(sim.getCreatedAt())
                                .updatedAt(sim.getUpdatedAt())
                                .operatorId(sim.getOperator().getId())
                                .operatorName(sim.getOperator().getName())
                                .build()).collect(Collectors.toList());

                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();

                return BasicResponse.builder()
                                .content(simDTOs)
                                .message("SIMs retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .metadata(metaData)
                                .build();
        }

        public BasicResponse updateSim(Long id, SimUpdateRequest simUpdateRequest) throws BasicException {
                Sim existingSim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .message("SIM not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                Operator operator = operatorRepository.findById(simUpdateRequest.getOperatorId())
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .message("Operator not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.BAD_REQUEST)
                                                .build()));

                if (!existingSim.getPhone().equals(simUpdateRequest.getPhone())
                                && simRepository.existsByPhone(simUpdateRequest.getPhone())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("Phone number already exists")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }

                if (!existingSim.getCcid().equals(simUpdateRequest.getCcid())
                                && simRepository.existsByCcid(simUpdateRequest.getCcid())) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("CCID already exists")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.CONFLICT)
                                        .build());
                }

                existingSim.setPin(simUpdateRequest.getPin());
                existingSim.setPuk(simUpdateRequest.getPuk());
                existingSim.setCcid(simUpdateRequest.getCcid());
                existingSim.setOperator(operator);
                existingSim.setPhone(simUpdateRequest.getPhone());
                existingSim.setUpdatedAt(new Date(System.currentTimeMillis()));

                existingSim = simRepository.save(existingSim);

                SimDTO simDTO = SimDTO.builder()
                                .id(existingSim.getId())
                                .pin(existingSim.getPin())
                                .puk(existingSim.getPuk())
                                .ccid(existingSim.getCcid())
                                .phone(existingSim.getPhone())
                                .status(existingSim.getStatus())
                                .createdAt(existingSim.getCreatedAt())
                                .updatedAt(existingSim.getUpdatedAt())
                                .operatorId(existingSim.getOperator().getId())
                                .operatorName(existingSim.getOperator().getName())
                                .build();

                return BasicResponse.builder()
                                .content(simDTO)
                                .message("SIM updated successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        public BasicResponse deleteSim(Long id) throws BasicException {
                Sim sim = simRepository.findById(id)
                                .orElseThrow(() -> new BasicException(BasicResponse.builder()
                                                .content(null)
                                                .message("SIM not found")
                                                .messageType(MessageType.ERROR)
                                                .status(HttpStatus.NOT_FOUND)
                                                .build()));

                updateSimStockOnDelete(sim);
                simRepository.delete(sim);

                return BasicResponse.builder()
                                .content(null)
                                .message("SIM deleted successfully with phone number: " + sim.getPhone())
                                .messageType(MessageType.WARNING)
                                .status(HttpStatus.OK)
                                .build();
        }

        public BasicResponse countNonInstalledSims() {
                long count = simRepository.countByStatus(SimStatus.NON_INSTALLED);
                return BasicResponse.builder()
                                .content(count)
                                .message("Non-installed SIMs count retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        public BasicResponse getAllNonInstalledSims(int page, int size) throws BasicException {
                Pageable pageRequest = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAllByStatus(SimStatus.NON_INSTALLED, pageRequest);

                List<SimBoitierDTO> simDTOs = simPage.getContent().stream()
                                .map(sim -> SimBoitierDTO.builder()
                                                .simMicroserviceId(sim.getId())
                                                .phone(sim.getPhone())
                                                .ccid(sim.getCcid())
                                                .operatorName(sim.getOperator().getName())
                                                .build())
                                .collect(Collectors.toList());

                MetaData metaData = MetaData.builder()
                                .currentPage(simPage.getNumber() + 1)
                                .totalPages(simPage.getTotalPages())
                                .size(simPage.getSize())
                                .build();

                return BasicResponse.builder()
                                .content(simDTOs)
                                .message("SIMs retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .metadata(metaData)
                                .build();
        }

        public BasicResponse searchNonInstalledSims(String query, int page, int size) throws BasicException {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<Sim> simPage = simRepository.findAllByStatusAndPhoneContainingOrCcidContaining(
                                SimStatus.NON_INSTALLED, query, pageable);

                if (simPage.isEmpty()) {
                        throw new BasicException(BasicResponse.builder()
                                        .content(null)
                                        .message("No non-installed SIMs found")
                                        .messageType(MessageType.ERROR)
                                        .status(HttpStatus.NOT_FOUND)
                                        .build());
                }

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

                return BasicResponse.builder()
                                .content(simDTOs)
                                .message("SIMs retrieved successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .metadata(metadata)
                                .build();
        }

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

                sim.setStatus(SimStatus.INSTALLED);
                sim = simRepository.save(sim);

                return BasicResponse.builder()
                                .content(sim)
                                .message("Device status changed to installed successfully")
                                .messageType(MessageType.SUCCESS)
                                .status(HttpStatus.OK)
                                .build();
        }

        /**
         * CHANGE SIM STATUS
         * 
         * This service changes the status of a SIM card
         * First, he check if the status is valid
         * Then, he check if the sim exists, if not, he throws an exception
         * If the sim exists, he changes the status of the sim
         * Finally, he returns a BasicResponse with the sim
         * 
         * @param id
         * @param status
         * @return
         * @throws BasicException
         */

         public BasicResponse changeSimStatus(Long id, String status) throws BasicException {
                // Find the sim
                Sim sim = simRepository.findById(id).orElseThrow(
                        () -> new BasicException(BasicResponse.builder()
                                .content(null)
                                .message("Sim not found")
                                .messageType(MessageType.ERROR)
                                .status(HttpStatus.NOT_FOUND)
                                .build()));
            
                // Check if the status is valid by checking the enum
                try {
                    SimStatus simStatus = SimStatus.valueOf(status.toUpperCase());
                    sim.setStatus(simStatus);
                    sim = simRepository.save(sim);
                    return BasicResponse.builder()
                            .message("Sim status changed successfully")
                            .messageType(MessageType.SUCCESS)
                            .status(HttpStatus.OK)
                            .content(sim) // include the updated sim in the response content
                            .build();
                } catch (IllegalArgumentException e) {
                    throw new BasicException(BasicResponse.builder()
                            .content(null)
                            .message("Invalid status")
                            .messageType(MessageType.ERROR)
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
                }
            }
            
}