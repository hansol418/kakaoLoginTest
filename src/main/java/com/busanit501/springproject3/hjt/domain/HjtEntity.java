package com.busanit501.springproject3.hjt.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tool")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HjtEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String tool_name;

    @Lob
    @Column(columnDefinition = "longtext")
    private String description;

    @Column
    private String img_text;
}
