package org.solution;

import model.Account;
import model.Bill;
import model.Payment;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import services.AccountServices;
import services.BillServices;
import services.PaymentServices;
import services.impl.AccountServicesImpl;
import services.impl.BillServicesImpl;
import services.impl.PaymentServicesImpl;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Random;
public class MainTest {
    @InjectMocks
    BillServices billService = new BillServicesImpl();
    @InjectMocks
    AccountServices accountService = new AccountServicesImpl();
    @InjectMocks
    PaymentServices paymentServices = new PaymentServicesImpl();
    @Test
    public void specificFunction() throws Exception {
        Main main = new Main();
        main.init();
        Account acc = accountService.getCurrenttUser();
        long amount = acc.getBalance();
        String[] args = {"check_balance"};
        main.specificFunction(args, acc);
        acc = accountService.getCurrenttUser();
        Assert.assertEquals((Long)amount, acc.getBalance());

        args = new String[]{"cash_in", "200000"};
        amount += 200000;
        main.specificFunction(args, acc);
        acc = accountService.getCurrenttUser();
        Assert.assertEquals((Long)amount, acc.getBalance());

        args = new String[]{"list_bill"};
        main.specificFunction(args, acc);
        List<Bill> list = billService.getListBill();
        Assert.assertNotNull(list);

        args = new String[]{"pay", "1", "2"};
        main.specificFunction(args, acc);

        args = new String[]{"due_date"};
        main.specificFunction(args, acc);

        args = new String[]{"SEARCH_BILL_BY_PROVIDER", "VNPT"};
        main.specificFunction(args, acc);

        args = new String[]{"SEARCH_BILL_BY_STATE", "NOT_PAID"};
        main.specificFunction(args, acc);

        args = new String[]{"ADD_BILL"};
        int id = new Random().nextInt();
        StringBuilder data = new StringBuilder();
        data.append(id);
        data.append(", TEST_CREATE, 100000, 30/9/2023, NOT_PAID, ACB");
        System.setIn(new ByteArrayInputStream(data.toString().getBytes()));
        main.specificFunction(args, acc);


        args = new String[]{"LIST_PAYMENT"};
        main.specificFunction(args, acc);
        List<Payment> listPm = paymentServices.getAll();
        Assert.assertNotNull(listPm);

        args = new String[]{"SCHEDULE", "1", "30/09/2023"};
        main.specificFunction(args, acc);
        Thread.sleep(5000);

        args = new String[]{"SCHEDULE", "2", "30/09/2025"};
        main.specificFunction(args, acc);
        Thread.sleep(5000);
    }
}