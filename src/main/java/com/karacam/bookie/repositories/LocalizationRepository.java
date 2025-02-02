package com.karacam.bookie.repositories;

import com.karacam.bookie.entities.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizationRepository extends JpaRepository<Localization, String> {
}
