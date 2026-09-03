package com.CineLock.Controller;

import com.CineLock.Entity.Payment;
import com.CineLock.Service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@Valid @RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PutMapping("/{id}/success")
    public Payment markPaymentSuccess(@PathVariable Long id) {
        return paymentService.markPaymentSuccess(id);
    }

    @PutMapping("/{id}/failure")
    public Payment markPaymentFailed(@PathVariable Long id) {
        return paymentService.markPaymentFailed(id);
    }
}