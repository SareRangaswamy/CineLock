package com.CineLock.Scheduler;

import com.CineLock.Entity.Seat;
import com.CineLock.Repository.SeatRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatHoldScheduler {

    private final SeatRepository seatRepository;

    public SeatHoldScheduler(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void releaseExpiredSeats() {

        List<Seat> expiredSeats =
                seatRepository.findByStatusAndHoldExpiresAtBefore(
                        "HELD",
                        LocalDateTime.now()
                );

        for (Seat seat : expiredSeats) {
            seat.setStatus("AVAILABLE");
            seat.setHoldExpiresAt(null);
        }

        seatRepository.saveAll(expiredSeats);
    }
}