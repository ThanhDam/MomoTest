package services.impl;

import model.Bill;
import model.BillStates;
import model.Payment;
import model.PaymentStates;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.BillServices;
import services.PaymentServices;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PaymentServicesImplTest {

    PaymentServices service;

    @BeforeEach
    public void setUp(){
        service = new PaymentServicesImpl();
    }

    @Test
    public void addPayment() throws Exception {
        //wait for file payments.txt created
        Thread.sleep(5000);
        //for case the bill is not exist
        Payment pm = new Payment(999, 10000L, new Date(System.currentTimeMillis()),
                PaymentStates.PENDING.name(), 999);
        Payment result = service.addPayment(pm);

        Assert.assertNull(result);

        //for case the payment is already exist
        pm = new Payment(1, 10000L, new Date(System.currentTimeMillis()),
                PaymentStates.PENDING.name(), 1);
        result = service.addPayment(pm);

        Assert.assertNull(result);

        //for happy case
        BillServices billServices = new BillServicesImpl();
        int id = new Random().nextInt();
        Bill bill = billServices.create(new Bill(id, "UTEST_CREATE_PM", 100000l,
                new Date(System.currentTimeMillis()),
                BillStates.NOT_PAID.name(), "ACB"));
        pm = new Payment(id, 10000L, new Date(System.currentTimeMillis()),
                PaymentStates.PENDING.name(), id);
        service.addPayment(pm);
        List<Payment> list = service.readObject();
        result = list.stream().filter(p->p.getPaymentId().equals(id))
                .collect(Collectors.toList()).get(0);
        Assert.assertEquals(pm.getBillId(), result.getBillId());
    }

    @Test
    public void getAll() {
        List<Payment> list = service.getAll();
        Assert.assertNotEquals(0, list.size());
    }

    @Test
    public void update() throws InterruptedException {
        List<Payment> list = service.readObject();
        Payment pmBefore = list.stream().filter(p->p.getPaymentId().equals(1))
                .collect(Collectors.toList()).get(0);
        long amount = pmBefore.getAmount() + 100;
        Payment pm = new Payment(1, amount, new Date(System.currentTimeMillis()),
                PaymentStates.PENDING.name(), 1);
        service.update(pm);
        Thread.sleep(5000);
        list = service.readObject();
        Payment pmAfter = list.stream().filter(p->p.getPaymentId().equals(1))
                .collect(Collectors.toList()).get(0);
        Assert.assertNotEquals(pmBefore.getPaymentDate(), pmAfter.getPaymentDate());
        Assert.assertNotEquals(pmBefore.getAmount(), pmAfter.getAmount());

    }
}