package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="author", schema = "public")
public class Author {

    @Id
    @GeneratedValue(generator="my_seq_author")
    @SequenceGenerator(name="my_seq_author",sequenceName="author_id_seq", allocationSize=1)
    Long author_id;

    String name;

    String surname;
}
