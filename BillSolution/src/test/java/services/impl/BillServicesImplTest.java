package services.impl;

import model.Account;
import model.Bill;
import model.BillStates;
import model.Payment;
import model.PaymentStates;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import services.AccountServices;
import services.BillServices;
import services.PaymentServices;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BillServicesImplTest {

    @InjectMocks
    BillServices service = new BillServicesImpl();
    @InjectMocks
    AccountServices accountServices = new AccountServicesImpl();
    @InjectMocks
    PaymentServices paymentServices = new PaymentServicesImpl();

    @BeforeEach
    public void setUp() {
        File file = new File("bills.txt");
        file.delete();
    }

    @Test
    public void testCreate() throws Exception {
        Bill bill = service.create(new Bill(99, "UTEST_CREATE", 100000l,
                new Date(System.currentTimeMillis()),
                BillStates.NOT_PAID.name(), "ACB"));
        List<Bill> list = service.readObject();
        Bill afterCreate = list.get(list.size()-1);
        Assert.assertEquals(bill.getBillId(), afterCreate.getBillId());
        Assert.assertEquals(bill.getState(), afterCreate.getState());
        Assert.assertEquals(bill.getAmount(), afterCreate.getAmount());
    }

    @Test
    public void testUpdate() {
        List<Bill> list = service.getListBill();
        Bill updateBill = list.get(0);
        updateBill.setProvider("TEST");
        updateBill.setDueDate(new Date(System.currentTimeMillis()));
        service.update(updateBill);

        List<Bill> lstAfter = service.readObject();
        Bill billAfter = lstAfter.stream().filter(b->
                b.getBillId().equals(updateBill.getBillId())).collect(Collectors.toList()).get(0);
        Assert.assertEquals("TEST", billAfter.getProvider());
        Assert.assertEquals(new Date(System.currentTimeMillis()).toString(),
                billAfter.getDueDate().toString());
    }

    @Test
    public void testDelete() {
        List<Bill> lstBefore = service.getListBill();
        Bill billBefore = lstBefore.stream().filter(b->
                b.getBillId().equals(1)).collect(Collectors.toList()).get(0);

        service.delete(1);
        List<Bill> lstAfter = service.readObject();
        Assert.assertFalse(lstAfter.contains(billBefore));
    }

    @Test
    public void testGetListBill() {
        List<Bill> lst = service.getListBill();
        Assert.assertNotEquals(0, lst.size());
    }

    @Test
    public void testGetBillById() {
        List<Bill> list = service.getListBill();
        Bill billInList = list.stream().filter(b->
                b.getBillId().equals(2)).collect(Collectors.toList()).get(0);
        Bill billMethod = service.getBillById(2);
        Assert.assertEquals(billInList.getBillId(), billMethod.getBillId());
        Assert.assertEquals(billInList.getState(), billMethod.getState());
        Assert.assertEquals(billInList.getProvider(), billMethod.getProvider());
        Assert.assertEquals(billInList.getDueDate(), billMethod.getDueDate());
        Assert.assertEquals(billInList.getAmount(), billMethod.getAmount());
    }

    @Test
    public void testGetBillByProvider() {
        String provider = "HCMC";
        List<Bill> getFromMethodLst = service.getBillByProvider(provider);
        Assert.assertNotEquals(0, getFromMethodLst.size());
    }

    @Test
    public void testSortedBillByDueDate() {
        List<Bill> bills = service.sortedBillByDueDate();
        Assert.assertEquals(-1, bills.get(0).getDueDate().compareTo(bills.get(1).getDueDate()));
    }

    @Test
    public void testPaid() throws Exception {
        Bill bill = service.create(new Bill(99, "UTEST_CREATE", 100000l,
                new Date(System.currentTimeMillis()),
                BillStates.NOT_PAID.name(), "ACB"));


        Account acc = accountServices.getCurrenttUser();
        acc.setBalance(100010l);
        //update balance to make sure the transaction occurs
        accountServices.updateBalance(acc);
        service.paid(bill.getBillId(), acc);

        //check balance after paid
        acc = accountServices.getCurrenttUser();
        Assert.assertEquals(Long.valueOf(10), acc.getBalance());

        //check payment after paid
        PaymentServices paymentServices = new PaymentServicesImpl();
        List<Payment> payments = paymentServices.getAll();
        Payment pm = payments.stream().filter(
                p->p.getBillId().equals(bill.getBillId())).collect(Collectors.toList()).get(0);
        Assert.assertEquals(PaymentStates.PROCESSED.toString(), pm.getState());

        //check bill after paid
        List<Bill> list = service.readObject();
        Bill billAfter = list.stream().filter(
                b->b.getBillId().equals(bill.getBillId())).collect(Collectors.toList()).get(0);
        Assert.assertEquals(BillStates.PAID.toString(), billAfter.getState());

    }

    @Test
    public void testStrategyPaidByDueDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 3);
        Bill bill =  service.create(new Bill(99, "UTEST_CREATE", 100000l,
                calendar.getTime(),
                BillStates.NOT_PAID.name(), "ACB"));
        Account acc = accountServices.getCurrenttUser();
        acc.setBalance(100010l);
        //update balance to make sure the transaction occurs
        accountServices.updateBalance(acc);

        calendar.add(Calendar.DATE, -4);
        Date rescheduleDate = calendar.getTime();
        service.strategyPaidByDueDate(bill.getBillId(), rescheduleDate, acc);

        Thread.sleep(6000);
        acc = accountServices.getCurrenttUser();
        Assert.assertEquals(Long.valueOf(10), acc.getBalance());

        Bill billAfter = service.getBillById(99);
        Assert.assertEquals(BillStates.PAID.toString(), billAfter.getState());

        Payment pm = paymentServices.readObject().stream()
                .filter(p->p.getBillId().equals(99))
                .collect(Collectors.toList()).get(0);
        Assert.assertEquals(PaymentStates.PROCESSED.toString(),
                pm.getState());
    }

    @Test
    public void testStrategyPaidByDueDate_NotPaidImmediately() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        calendar.setTime(today);
        calendar.add(Calendar.DATE, 10);
        Bill bill =  service.create(new Bill(999, "UTEST_CREATE", 100000l,
                calendar.getTime(),
                BillStates.NOT_PAID.name(), "ACB"));
        Account acc = accountServices.getCurrenttUser();
        acc.setBalance(100010L);
        //update balance to make sure the transaction occurs
        accountServices.updateBalance(acc);

        calendar.add(Calendar.DATE, -5);
        Date rescheduleDate = calendar.getTime();
        service.strategyPaidByDueDate(bill.getBillId(), rescheduleDate, acc);

        Thread.sleep(4000);
        acc = accountServices.getCurrenttUser();
        Assert.assertEquals(Long.valueOf(100010), acc.getBalance());

        Bill billAfter = service.getBillById(999);
        Assert.assertEquals(BillStates.NOT_PAID.toString(), billAfter.getState());

        Payment pm = paymentServices.readObject().stream()
                .filter(p->p.getBillId().equals(999))
                .collect(Collectors.toList()).get(0);
        Assert.assertEquals(PaymentStates.PENDING.toString(),
                pm.getState());

    }
}