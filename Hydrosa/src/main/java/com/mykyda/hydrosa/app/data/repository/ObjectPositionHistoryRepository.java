package com.mykyda.hydrosa.app.data.repository;

import com.mykyda.hydrosa.app.data.entity.ObjectPositionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectPositionHistoryRepository extends JpaRepository<ObjectPositionHistory, UUID> {
    List<ObjectPositionHistory> findAllByTrackedObjectIdOrderByDetectedAtAsc(UUID trackedObjectId);
}