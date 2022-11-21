package com.company.stories.service;

import com.company.stories.model.dto.LogDTO;
import com.company.stories.model.dto.UserDTO;
import com.company.stories.model.entity.Log;
import com.company.stories.model.entity.User;
import com.company.stories.model.mapper.LogMapper;
import com.company.stories.model.mapper.UserMapper;
import com.company.stories.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;

        saveLog("Starting application");
    }

    public void saveLog(String message){
        Log log = Log.builder()
                .logMessage(message)
                .date(LocalDateTime.now())
                .build();

        logRepository.save(log);
    }

    public Map<String, Object> getAllLogs(Pageable pageable) {
        Page<Log> page = logRepository.findAll(pageable);

        return getPageOfLogs(page);
    }

    public Map<String, Object> getByMessage(String msg, Pageable pageable) {
        Page<Log> page = logRepository.findByLogMessageContainingIgnoreCase(msg, pageable);

        return getPageOfLogs(page);
    }

    public Map<String, Object> getByDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Log> page = logRepository.findByDateBetween(start, end, pageable);

        return getPageOfLogs(page);
    }

    public Map<String, Object> getByMessageAndDateBetween(String msg, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Log> page = logRepository.findByLogMessageContainingIgnoreCaseAndDateBetween(msg, start, end, pageable);

        return getPageOfLogs(page);
    }

    private Map<String, Object> getPageOfLogs(Page<Log> page) {
        List<LogDTO> logDTOs = page.getContent().stream()
                .map(LogMapper::toLogDTO)
                .collect(Collectors.toList());

        Map<String, Object> logsPage = new HashMap<>();
        logsPage.put("logDTOs", logDTOs);
        logsPage.put("currentPage", page.getNumber());
        logsPage.put("totalItems", page.getTotalElements());
        logsPage.put("totalPages", page.getTotalPages());

        return logsPage;
    }
}
