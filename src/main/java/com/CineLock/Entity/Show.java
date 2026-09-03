package com.CineLock.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theatreName;

    private LocalDateTime showTime;

    private Double ticketPrice;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;
}