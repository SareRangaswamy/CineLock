package com.CineLock.Service;

import com.CineLock.Entity.Booking;
import com.CineLock.Entity.Payment;
import com.CineLock.Entity.Seat;
import com.CineLock.Repository.BookingRepository;
import com.CineLock.Repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public Payment createPayment(Payment payment) {
        Long bookingId = payment.getBooking().getId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        payment.setBooking(booking);
        payment.setPaymentStatus("PENDING");

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPaymentSuccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Booking booking = payment.getBooking();

        if (booking == null || booking.getSeat() == null) {
            throw new RuntimeException("Booking or seat not found");
        }

        Seat seat = booking.getSeat();

        if (!"HELD".equalsIgnoreCase(seat.getStatus())) {
            throw new RuntimeException("Seat is not on hold");
        }

        payment.setPaymentStatus("SUCCESS");
        booking.setBookingStatus("CONFIRMED");

        seat.setStatus("BOOKED");
        seat.setHoldExpiresAt(null);

        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment markPaymentFailed(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Booking booking = payment.getBooking();

        if (booking == null || booking.getSeat() == null) {
            throw new RuntimeException("Booking or seat not found");
        }

        Seat seat = booking.getSeat();

        payment.setPaymentStatus("FAILED");
        booking.setBookingStatus("CANCELLED");

        seat.setStatus("AVAILABLE");
        seat.setHoldExpiresAt(null);

        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}