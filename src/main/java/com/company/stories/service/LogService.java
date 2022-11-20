package com.company.stories.service;

import com.company.stories.model.entity.Log;
import com.company.stories.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
