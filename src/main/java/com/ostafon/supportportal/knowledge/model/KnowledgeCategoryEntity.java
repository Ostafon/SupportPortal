package com.ostafon.supportportal.knowledge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knowledge_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KnowledgeCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;
}