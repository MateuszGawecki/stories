package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="role", schema = "public")
public class Role {

    @Id
    @GeneratedValue(generator="my_seq_role")
    @SequenceGenerator(name="my_seq_role",sequenceName="role_id_seq", allocationSize=1)
    Long role_id;

    @EqualsAndHashCode.Include
    String name;
}
