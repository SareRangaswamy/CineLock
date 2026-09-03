package com.CineLock.Service;

import com.CineLock.Entity.Booking;
import com.CineLock.Entity.Seat;
import com.CineLock.Repository.BookingRepository;
import com.CineLock.Repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public BookingService(BookingRepository bookingRepository,
                          SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public Booking createBooking(Booking booking) {

        Long seatId = booking.getSeat().getId();

        Seat seat = seatRepository.findSeatForUpdate(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!"HELD".equalsIgnoreCase(seat.getStatus())) {
            throw new RuntimeException("Seat must be held before booking");
        }

        booking.setSeat(seat);
        booking.setBookingStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}