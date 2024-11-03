package com.example.multi_lang.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "translation")
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String tableName;
    @Column(nullable = false)
    private String columnName;
    @Column(nullable = false)
    private int rowIndex;
    @ManyToOne
    @JoinColumn(name = "language_id",nullable = false)
    private Language language;
    private String value;
}
