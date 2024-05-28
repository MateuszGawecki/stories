package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name="comment", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(generator="my_seq_comment")
    @SequenceGenerator(name="my_seq_comment",sequenceName="comment_id_seq", allocationSize=1)
    Long comment_id;

    @Column(name = "user_to_book_id")
    Long userBookId;

    String comment;

    @Column(name = "is_public")
    Boolean isPublic;
}
