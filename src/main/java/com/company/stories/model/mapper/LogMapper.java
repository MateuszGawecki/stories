package com.company.stories.model.mapper;

import com.company.stories.model.dto.LogDTO;
import com.company.stories.model.entity.Log;
import org.springframework.stereotype.Component;

@Component
public abstract class LogMapper {

    public static LogDTO toLogDTO(Log log){
        return LogDTO.builder()
                .logId(log.getLogId())
                .logMessage(log.getLogMessage())
                .date(log.getDate())
                .build();
    }
}
