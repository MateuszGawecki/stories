package com.company.stories.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
