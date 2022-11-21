package com.company.stories.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(name = "LogDTO", description = "Representation of log entity")
public class LogDTO {

    @Schema(name = "logId", description = "Unique log id", example = "1")
    @Nullable
    Long logId;

    @Schema(name = "logMessage", description = "Message of log", example = "User modified resource xyz")
    String logMessage;

    @Schema(name = "date", description = "Date of log", example = "2022-11-20 21:04:56.311334")
    LocalDateTime date;
}
