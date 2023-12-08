package services;

import model.Payment;

import java.util.List;

public interface PaymentServices {
    Payment addPayment(Payment payment);
    List<Payment> getAll();

    void update(Payment payment);

    void writeObject(List<Payment> payments);

    List<Payment> readObject();
}
