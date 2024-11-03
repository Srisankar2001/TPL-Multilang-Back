package com.example.multi_lang.Repository;

import com.example.multi_lang.Entity.Language;
import com.example.multi_lang.Entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation,Integer> {
    Optional<Translation> findByTableNameAndColumnNameAndRowIndexAndLanguage(String tableName, String columnName, int rowIndex, Language language);
}
