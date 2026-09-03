package com.CineLock.Service;

import com.CineLock.Entity.Seat;
import com.CineLock.Entity.Show;
import com.CineLock.Repository.SeatRepository;
import com.CineLock.Repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final RedisSeatHoldService redisSeatHoldService;

    public SeatService(SeatRepository seatRepository,
                       ShowRepository showRepository,
                       RedisSeatHoldService redisSeatHoldService) {
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.redisSeatHoldService = redisSeatHoldService;
    }

    public Seat createSeat(Seat seat) {

        Long showId = seat.getShow().getId();

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        seat.setShow(show);

        if (seat.getStatus() == null) {
            seat.setStatus("AVAILABLE");
        }

        return seatRepository.save(seat);
    }

    public Seat holdSeat(Long seatId, String username) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!"AVAILABLE".equalsIgnoreCase(seat.getStatus())) {
            throw new RuntimeException("Seat is not available");
        }

        seat.setStatus("HELD");
        seat.setHoldExpiresAt(
                LocalDateTime.now().plusMinutes(2)
        );

        // Redis lo temporary hold store chestam
        redisSeatHoldService.holdSeat(
                seatId,
                username
        );

        return seatRepository.save(seat);
    }

    public Seat confirmSeat(Long seatId) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!"HELD".equalsIgnoreCase(seat.getStatus())) {
            throw new RuntimeException("Seat is not on hold");
        }

        seat.setStatus("BOOKED");
        seat.setHoldExpiresAt(null);

        // Redis hold remove
        redisSeatHoldService.releaseSeat(seatId);

        return seatRepository.save(seat);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }
}