package com.example.multi_lang.Repository;

import com.example.multi_lang.Entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Integer> {
    Optional<Language> findByCode(String code);
}
