package com.mykyda.hydrosasim.app.data.repository;

import com.mykyda.hydrosasim.app.data.entity.WaterObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WaterObjectRepository extends JpaRepository<WaterObject, UUID> {
}
