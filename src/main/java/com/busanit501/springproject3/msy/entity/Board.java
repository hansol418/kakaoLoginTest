package com.busanit501.springproject3.msy.entity;
//
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String writer;
    private String boardContent;
    private String filename;
    private String filepath;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
