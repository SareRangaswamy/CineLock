package com.CineLock;

import com.CineLock.Entity.Booking;
import com.CineLock.Entity.Seat;
import com.CineLock.Repository.BookingRepository;
import com.CineLock.Repository.SeatRepository;
import com.CineLock.Service.BookingService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testSameSeatBookingAtSameTime() throws InterruptedException {

        Seat seat = seatRepository.findById(9L)
                .orElseThrow();

        seat.setStatus("AVAILABLE");
        seatRepository.save(seat);

        CountDownLatch startSignal = new CountDownLatch(1);

        Runnable rangaTask = () -> {
            try {
                startSignal.await();

                Booking booking = new Booking();
                booking.setCustomerName("Ranga");

                Seat requestSeat = new Seat();
                requestSeat.setId(9L);

                booking.setSeat(requestSeat);

                Booking result = bookingService.createBooking(booking);

                System.out.println(
                        "Ranga SUCCESS: " + result.getBookingStatus()
                );

            } catch (Exception e) {
                System.out.println(
                        "Ranga FAILED: " + e.getMessage()
                );
            }
        };

        Runnable raviTask = () -> {
            try {
                startSignal.await();

                Booking booking = new Booking();
                booking.setCustomerName("Ravi");

                Seat requestSeat = new Seat();
                requestSeat.setId(9L);

                booking.setSeat(requestSeat);

                Booking result = bookingService.createBooking(booking);

                System.out.println(
                        "Ravi SUCCESS: " + result.getBookingStatus()
                );

            } catch (Exception e) {
                System.out.println(
                        "Ravi FAILED: " + e.getMessage()
                );
            }
        };

        Thread t1 = new Thread(rangaTask);
        Thread t2 = new Thread(raviTask);

        t1.start();
        t2.start();

        startSignal.countDown();

        t1.join();
        t2.join();
    }
}