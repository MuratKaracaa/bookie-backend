package com.karacam.bookie.repositories;

import com.karacam.bookie.entities.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<AppConfig, String> {
}
