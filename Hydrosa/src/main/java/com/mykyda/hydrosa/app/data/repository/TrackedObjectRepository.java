package com.mykyda.hydrosa.app.data.repository;

import com.mykyda.hydrosa.app.data.entity.TrackStatus;
import com.mykyda.hydrosa.app.data.entity.TrackedObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrackedObjectRepository extends JpaRepository<TrackedObject, UUID> {
    List<TrackedObject> findByStatusNot(TrackStatus status);

    void deleteAll();

    List<TrackedObject> findAll();
}
