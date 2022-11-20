package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="user", schema = "public")
public class User {

    @Id
    @GeneratedValue(generator="my_seq_user")
    @SequenceGenerator(name="my_seq_user",sequenceName="user_id_seq", allocationSize=1)
    @Column(name = "user_id")
    Long userId;

    String name;

    String surname;

    @EqualsAndHashCode.Include
    String email;

    @EqualsAndHashCode.Include
    String password;

    String image_path;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_to_role",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id")
    })
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_to_user",
            joinColumns = { @JoinColumn(name = "id_user1")},
            inverseJoinColumns = {@JoinColumn(name = "id_user2")
    })
    private Set<User> friends = new HashSet<>();
}
