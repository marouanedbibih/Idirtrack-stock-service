package com.idirtrack.stock_service.device;

import com.idirtrack.stock_service.basics.BasicResponse;
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.BasicException;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.basics.MetaData;
import com.idirtrack.stock_service.device.https.DeviceRequest;
import com.idirtrack.stock_service.device.https.DeviceUpdateRequest;
import com.idirtrack.stock_service.stock.Stock;
import com.idirtrack.stock_service.stock.StockRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;


@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final DeviceStockRepository deviceStockRepository;
    private final StockRepository stockRepository;

    // Save device
    public BasicResponse createDevice(@Valid DeviceRequest deviceRequest, BindingResult bindingResult) throws BasicException {

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

        // Check if the device already exists
       try {
            ifExists(deviceRequest.getImei());
        } catch (BasicException e) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("IMEI already exists")
                    .messageType(MessageType.ERROR)
                    .data(null)
                    .build());
        }

        //check if device type exists
        if (!deviceTypeRepository.existsByName(deviceRequest.getTypeDevice())) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Device type not found")
                    .messageType(MessageType.ERROR)
                    .data(null)
                    .build());
        }

        // Transform the request to entity
        DeviceDTO deviceDTO = transformRequestDTO(deviceRequest);

        // Save the device entity
        Device device  = deviceRepository.save(transformResponseDTO(deviceDTO));



        //check if device stock exists and update it (add quantity)
        //check with device type and date
        updateDeviceStock(device);
                

        


        
        // Return a success response
        return BasicResponse.builder()
                .data(device)
                .message("Device created successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.CREATED)
                .redirectUrl(null)
                .build();
    }

    // Update device
     // Update device
     public BasicResponse updateDevice(Long id, @Valid DeviceUpdateRequest deviceUpdateRequest, BindingResult bindingResult) throws BasicException {

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
        Device existingDevice = deviceRepository.findById(id).orElseThrow(() ->
        new BasicException(BasicResponse.builder()
                .data(null)
                .message("Device not found")
                .messageType(MessageType.ERROR)
                .status(HttpStatus.NOT_FOUND)
                .build()));

        // Check if device type exists
        if (!deviceTypeRepository.existsByName(deviceUpdateRequest.getTypeDevice())) {
            throw new BasicException(BasicResponse.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Device type not found")
                    .messageType(MessageType.ERROR)
                    .data(null)
                    .build());
        }

        existingDevice.setImei(deviceUpdateRequest.getImei());
        existingDevice.setDeviceType(deviceTypeRepository.findByName(deviceUpdateRequest.getTypeDevice()));
        existingDevice.setStatus(DeviceStatus.valueOf(deviceUpdateRequest.getStatus()));
        existingDevice.setRemarque(deviceUpdateRequest.getRemarque());
        existingDevice.setUpdatedAt( new Date(System.currentTimeMillis()));

        deviceRepository.save(existingDevice);

        return BasicResponse.builder()
                .data(existingDevice)
                .message("Device updated successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
}
    // Delete device
    public BasicResponse deleteDevice(Long id) throws BasicException {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new BasicException(BasicResponse.builder()
                    .data(null)
                    .message("Device not found")
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }

        // Update device stock on delete
        updateDeviceStockOnDelete(device);

        deviceRepository.delete(device);

        return BasicResponse.builder()
                .message("Device deleted successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

    // Get device by ID
    public BasicResponse getDeviceById(Long id) throws BasicException {
       Device device = deviceRepository.findById(id).orElse(null);
        if (device == null) {
            throw new BasicException(BasicResponse.builder()
                    .data(null)
                    .message("Device not found")
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.NOT_FOUND)
                    .build());
        }

        return BasicResponse.builder()
                .data(device)
                .message("Device retrieved successfully")
                .messageType(MessageType.SUCCESS)
                .status(HttpStatus.OK)
                .build();
    }

     // Get all devices with pagination
    public BasicResponse getAllDevices(int page, int size) {
        // Create pagination
        Pageable pageRequest = PageRequest.of(page - 1, size);

        // Retrieve all devices from the database
        Page<Device> devicePage = deviceRepository.findAll(pageRequest);

        // Create a list of DTOs for devices
        List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
                .map(device -> DeviceDTO.builder().deviceType(device.getDeviceType().getName())
                        .id(device.getId())
                        .IMEI(device.getImei())
                        .remarque(device.getRemarque())
                        .status(device.getStatus())
                        .build())
                        
                .collect(Collectors.toList());

        MetaData metaData = MetaData.builder()
                .currentPage(devicePage.getNumber() + 1)
                .totalPages(devicePage.getTotalPages())
                .size(devicePage.getSize())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("devices", deviceDTOs);
        data.put("metadata", metaData);
    //if device not found
        if (devicePage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No devices found")
                    .messageType(MessageType.ERROR)
                    .build();
        }
        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("Devices retrieved successfully")
                .build();
    }

    // Search devices by name with pagination

    // Transform DTO to entity
    public Device transformResponseDTO(DeviceDTO deviceDTO) {
        
        DeviceType deviceType = deviceTypeRepository.findByName(deviceDTO.getDeviceType());
        return Device.builder()
                .imei(deviceDTO.getIMEI())
                .createdAt(new Date(System.currentTimeMillis()))
                .status(DeviceStatus.NON_INSTALLED)
                .deviceType(deviceType)
                .remarque(deviceDTO.getRemarque())
                .build();
    }

    // Transform requestUpdate to DTO
    public DeviceDTO transformRequestUpdateDTO(DeviceUpdateRequest deviceUpdateRequest) {
        return DeviceDTO.builder()
                .IMEI(deviceUpdateRequest.getImei())
                .deviceType(deviceUpdateRequest.getTypeDevice())
                .remarque(deviceUpdateRequest.getRemarque())
                .status(DeviceStatus.valueOf(deviceUpdateRequest.getStatus()))
                .build();
    }
  
    // Update device stock
    public void updateDeviceStock(Device device) {
    
        List<Stock> stocks = stockRepository.findByDateEntree(device.getCreatedAt());
        Stock stock = null;
        DeviceStock deviceStock = null;
    
        for (Stock s : stocks) {
            deviceStock = deviceStockRepository.findByStockAndDeviceType(s, device.getDeviceType());
            if (deviceStock != null) {
                stock = s;
                break;
            }
        }
    
        if (stock == null) {
            stock = Stock.builder()
                    .dateEntree(device.getCreatedAt())
                    .quantity(1)
                    .build();
            stock = stockRepository.save(stock);
    
            deviceStock = DeviceStock.builder()
                    .deviceType(device.getDeviceType())
                    .stock(stock)
                    .build();
            deviceStockRepository.save(deviceStock);
        } else {
            stock.setQuantity(stock.getQuantity() + 1);
            stockRepository.save(stock);
        }
    }

    // Update device stock on delete
    private void updateDeviceStockOnDelete(Device device) {

        List<Stock> stocks = stockRepository.findByDateEntree(device.getCreatedAt());
        Stock stock = null;
        DeviceStock deviceStock = null;

        for (Stock s : stocks) {
            deviceStock = deviceStockRepository.findByStockAndDeviceType(s, device.getDeviceType());
            if (deviceStock != null) {
                stock = s;
                break;
            }
        }

        if (stock != null) {
            stock.setQuantity(stock.getQuantity() - 1);
            stockRepository.save(stock);

            if (stock.getQuantity() <= 0) {
                deviceStockRepository.delete(deviceStock);
                stockRepository.delete(stock);
            }
        }
    }
    

    // Transform request to DTO
    public DeviceDTO transformRequestDTO(DeviceRequest deviceRequest) {
        return DeviceDTO.builder()
                .IMEI(deviceRequest.getImei())
                .deviceType(deviceRequest.getTypeDevice())
                .remarque(deviceRequest.getRemarque())
                .build();
    }

    // Check if device with the given IMEI already exists
    public void ifExists(String n) throws BasicException {
        if (deviceRepository.existsByImei(n)) {
            Map<String, String> messagesList = new HashMap<>();
            messagesList.put("IMEI", "IMEI already exists");
            throw new BasicException(BasicResponse.builder()
                    .data(null)
                    .message("IMEI already exists")
                    .messagesList(messagesList)
                    .build());
        }
    }

    //search device with pagination
    
    // Search devices with pagination
    public BasicResponse searchDevices(String imei, String typeDevice, String status, Date date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<Device> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (imei != null && !imei.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("imei"), imei));
            }

            if (typeDevice != null && !typeDevice.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("deviceType").get("name"), typeDevice));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), DeviceStatus.valueOf(status)));
            }

            if (date != null) {
                predicates.add(criteriaBuilder.equal(root.get("createdAt"), date));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Device> devicePage = deviceRepository.findAll(specification, pageable);
        if (devicePage.isEmpty()) {
            return BasicResponse.builder()
                    .data(null)
                    .status(HttpStatus.NOT_FOUND)
                    .message("No devices found")
                    .messageType(MessageType.ERROR)
                    .build();
        }

        List<DeviceDTO> deviceDTOs = devicePage.getContent().stream()
                .map(device -> DeviceDTO.builder()
                        .id(device.getId())
                        .IMEI(device.getImei())
                        .deviceType(device.getDeviceType().getName())
                        .remarque(device.getRemarque())
                        .status(device.getStatus())
                        .build())
                .collect(Collectors.toList());

        MetaData metaData = MetaData.builder()
                .currentPage(devicePage.getNumber() + 1)
                .totalPages(devicePage.getTotalPages())
                .size(devicePage.getSize())
                .build();

        Map<String, Object> data = new HashMap<>();
        data.put("devices", deviceDTOs);
        data.put("metadata", metaData);

        return BasicResponse.builder()
                .data(data)
                .status(HttpStatus.OK)
                .message("Devices retrieved successfully")
                .build();
    }

    //count all devices have status non installed
    public BasicResponse countDevicesNonInstalled() {
        long count = deviceRepository.countByStatus(DeviceStatus.NON_INSTALLED);
        return BasicResponse.builder()
                .data(count)
                .status(HttpStatus.OK)
                .message("Devices count retrieved successfully")
                .build();
    }
}

