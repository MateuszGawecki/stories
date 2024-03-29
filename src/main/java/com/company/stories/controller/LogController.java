package com.company.stories.controller;

import com.company.stories.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/logs")
@Slf4j
@Tag(name = "Logs", description = "Endpoints for managing logs")
public class LogController {
    private final LogService logService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Operation(summary = "Getting all logs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "logId,desc") String[] sort,
            @RequestParam(required = false) String msg,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
            ) throws ParseException {

        try {
            List<Sort.Order> orders = new ArrayList<Sort.Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

            Map<String, Object> response;

            String dateTimeStartStr;
            LocalDateTime startDate = null;
            String dateTimeEdnStr;
            LocalDateTime endDate = null;

            if(start != null){
                dateTimeStartStr = start.replace("T", " ");
                startDate = LocalDateTime.parse(dateTimeStartStr, formatter);
            }

            if(end != null){
                dateTimeEdnStr = end.replace("T", " ");
                endDate = LocalDateTime.parse(dateTimeEdnStr, formatter);
            }

            if(msg != null && startDate != null && endDate != null){
                response = logService.getByMessageAndDateBetween(msg, startDate, endDate, pagingSort);
            } else if(msg != null && startDate != null){
                response = logService.getByMessageAndDateAfter(msg, startDate, pagingSort);
            } else if(msg != null && endDate!= null){
                response = logService.getByMessageAndDateBefore(msg, endDate, pagingSort);
            } else if(startDate != null && endDate !=null){
                response = logService.getByDateBetween(startDate, endDate, pagingSort);
            } else if(msg != null) {
                response = logService.getByMessage(msg, pagingSort);
            } else if(startDate != null) {
                response = logService.getByDateAfter(startDate, pagingSort);
            } else if(endDate != null) {
                response = logService.getByDateBefore(endDate, pagingSort);
            } else {
                response = logService.getAllLogs(pagingSort);
            }


            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
}
