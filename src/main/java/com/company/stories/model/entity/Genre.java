package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="genre", schema = "public")
public class Genre {

    @Id
    @GeneratedValue(generator="my_seq_genre")
    @SequenceGenerator(name="my_seq_genre",sequenceName="genre_id_seq", allocationSize=1)
    Long genre_id;

    String name;
}
