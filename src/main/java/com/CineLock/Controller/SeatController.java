package com.CineLock.Controller;

import com.CineLock.Entity.Seat;
import com.CineLock.Service.SeatService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public Seat createSeat(@Valid @RequestBody Seat seat) {
        return seatService.createSeat(seat);
    }

    @GetMapping
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    @PutMapping("/{id}/hold")
    public Seat holdSeat(@PathVariable Long id,
                         Authentication authentication) {

        return seatService.holdSeat(
                id,
                authentication.getName()
        );
    }

    @PutMapping("/{id}/confirm")
    public Seat confirmSeat(@PathVariable Long id) {
        return seatService.confirmSeat(id);
    }
}