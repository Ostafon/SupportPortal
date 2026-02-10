package com.ostafon.supportportal.knowledgebase.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knowledge_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 999;
}