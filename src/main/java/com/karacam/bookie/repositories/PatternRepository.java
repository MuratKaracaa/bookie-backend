package com.karacam.bookie.repositories;

import com.karacam.bookie.entities.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternRepository extends JpaRepository<Pattern, String> {
}
