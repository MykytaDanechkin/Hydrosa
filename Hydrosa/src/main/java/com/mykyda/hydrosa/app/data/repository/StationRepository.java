package com.mykyda.hydrosa.app.data.repository;

import com.mykyda.hydrosa.app.data.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
}
