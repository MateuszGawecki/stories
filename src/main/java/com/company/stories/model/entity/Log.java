package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="application_log", schema = "public")
public class Log {
    @Id
    @GeneratedValue(generator="my_seq_log")
    @SequenceGenerator(name="my_seq_log",sequenceName="log_id_seq", allocationSize=1)
    @Column(name = "log_Id")
    Long logId;

    @Column(name = "log_message")
    String logMessage;

    LocalDateTime date;
}
