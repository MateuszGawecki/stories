package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name="user_to_book", schema = "public")
public class UserBook {

    @Id
    @GeneratedValue(generator="my_seq_user_to_book")
    @SequenceGenerator(name="my_seq_user_to_book",sequenceName="user_to_book_id_seq", allocationSize=1)
    Long user_to_book_id;

    @Column(name = "user_id")
    Long userId;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "book_id", referencedColumnName = "book_id")
    Book book;

    @Nullable
    @Column(name = "user_rating")
    Integer userRating;

    @OneToMany(orphanRemoval = true,
            cascade = {
            CascadeType.ALL
    })
    @JoinColumn(name = "user_to_book_id", referencedColumnName = "user_to_book_id")
    List<Comment> comments;
}
