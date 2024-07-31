package com.idirtrack.stock_service.sim;

import java.util.Map;

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
import com.idirtrack.stock_service.basics.BasicValidation;
import com.idirtrack.stock_service.basics.MessageType;
import com.idirtrack.stock_service.sim.https.SimRequest;
import com.idirtrack.stock_service.sim.https.SimUpdateRequest;

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
    public ResponseEntity<BasicResponse> getSimById(@PathVariable Long id) {
        try {
            BasicResponse response = simService.getSimById(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder().content(null)
                    .message(e.getMessage()).messageType(MessageType.ERROR).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PostMapping("/")
    public ResponseEntity<BasicResponse> createSim(@Valid @RequestBody SimRequest simRequest,
            BindingResult bindingResult) throws BasicException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
            BasicResponse response = BasicResponse.builder()
                    .content(null)
                    .message("Validation Error")
                    .messagesObject(errors)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST).build();
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        try {
            BasicResponse response = simService.createSim(simRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder().content(null)
                    .message(e.getMessage()).messageType(MessageType.ERROR).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PutMapping("/{id}/")
    public ResponseEntity<BasicResponse> updateSim(@PathVariable Long id,
            @Valid @RequestBody SimUpdateRequest simUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BasicValidation.getValidationsErrors(bindingResult);
            BasicResponse response = BasicResponse.builder()
                    .content(null)
                    .message("Validation Error")
                    .messagesObject(errors)
                    .messageType(MessageType.ERROR)
                    .status(HttpStatus.BAD_REQUEST).build();
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        try {
            BasicResponse response = simService.updateSim(id, simUpdateRequest);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder().content(null)
                    .message(e.getMessage()).messageType(MessageType.ERROR).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }

    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<BasicResponse> deleteSim(@PathVariable Long id) {
        try {
            BasicResponse response = simService.deleteSim(id);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (BasicException e) {
            return ResponseEntity.status(e.getResponse().getStatus()).body(e.getResponse());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.builder().content(null)
                    .message(e.getMessage()).messageType(MessageType.ERROR).status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @GetMapping("/non-installed-sims/")
    public ResponseEntity<BasicResponse> getNonInstalledSimsApi(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        BasicResponse response = simService.getAllNonInstalledSims(page, size);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/non-installed-sims/search/")
    public ResponseEntity<BasicResponse> searchNonInstalledSimsApi(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            BasicResponse response = simService.searchNonInstalledSims(query, page, size);
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

    @GetMapping("/search")
    public ResponseEntity<BasicResponse> searchSIMsByPhoneAndCCID(@RequestParam(value = "term", required = true) String term,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {
        try {
            if (term == null || term.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BasicResponse.builder()
                        .content(null)
                        .message("Search term cannot be empty")
                        .messageType(MessageType.ERROR)
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }
            BasicResponse response = simService.searchSIMsByPhoneAndCCID(term, page, size);
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
