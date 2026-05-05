package com.mykyda.hydrosa.app.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "signal")
public class Signal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double azimuth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Station station;

//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "located_entity_id", nullable = true)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private LocatedEntity locatedEntity;

    @CreationTimestamp
    private LocalDateTime receivedAt;

    private double strength;

    @Column(nullable = false)
    @Builder.Default
    private boolean processed = false;
}
