package com.mykyda.hydrosa.app.data.repository;

import com.mykyda.hydrosa.app.data.entity.Signal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignalRepository extends JpaRepository<Signal, UUID> {

    List<Signal> findAllByStationId(Long stationId);


    List<Signal> findAllByStationIdAndReceivedAtAfter(Long stationId,LocalDateTime time);

    List<Signal> findAllByProcessedFalse();

    @Modifying
    @Query("UPDATE Signal s SET s.processed = true WHERE s.id IN :ids")
    void markProcessed(@Param("ids") List<UUID> ids);


}
