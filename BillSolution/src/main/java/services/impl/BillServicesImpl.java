package services.impl;

import model.Account;
import model.Bill;
import model.BillStates;
import model.Payment;
import model.PaymentStates;
import services.AccountServices;
import services.BillServices;
import services.PaymentServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class BillServicesImpl implements BillServices {
    AccountServices accountService;

    PaymentServices paymentServices;
    @Override
    public Bill create(Bill bill) throws Exception {
        //assume action load data from DB
        List<Bill> list = getListBill();
        if(list.stream().anyMatch(b -> b.getBillId().equals(bill.getBillId()))){
            return null;
        }
        //assume action add element
        list.add(bill);
        writeObjectBill(list);
        simulatePaymentEquivalent(bill.getBillId(), bill.getAmount(),
                new SimpleDateFormat("dd/MM/yyyy").format(bill.getDueDate()),
                PaymentStates.PENDING.name(), bill.getBillId());
        return bill;
    }

    @Override
    public void update(Bill updateBill) {
        List<Bill> bills = getListBill();
        Bill bill = new Bill();
        boolean isExist = false;
        for(Bill b: bills){
            if(b.getBillId().equals(updateBill.getBillId())){
                bill = b;
                isExist = true;
                break;
            }
        }
        if(isExist){
            bills.remove(bill);
            bills.add(updateBill);
            writeObjectBill(bills);
            String state = BillStates.NOT_PAID.toString().equals(updateBill.getState())
                    ? PaymentStates.PENDING.toString(): PaymentStates.PROCESSED.toString();
            updatePaymentEquivalent(updateBill.getBillId(), updateBill.getAmount(),
                    new SimpleDateFormat("dd/MM/yyyy").format(updateBill.getDueDate()),
                    state, updateBill.getBillId());
        }
    }

    @Override
    public boolean delete(int id) {
        List<Bill> bills = getListBill();
        if(bills.remove(getBillById(id))){
            writeObjectBill(bills);
            return true;
        }
        return false;
    }

    @Override
    public List<Bill> getListBill() {
        File fileBill = new File("bills.txt");
        if(!fileBill.exists()){
            //simulate dataO
            simulateListBill();
        }
        return readObject();
    }

    private void simulateListBill()  {
        Bill bill1 = new Bill(1, "ELECTRIC", (long)200000, "25/10/2020",
                "NOT_PAID", "HCMC");
        Bill bill2 = new Bill(2, "WATER", (long)175000, "30/10/2020",
                "NOT_PAID", "SAVACO HCMC");
        Bill bill3 = new Bill(3, "INTERNET", (long)80000, "30/11/2020",
                "NOT_PAID", "VNPT");
        List<Bill> bills = new ArrayList<>();
        bills.add(bill1);
        bills.add(bill2);
        bills.add(bill3);
        writeObjectBill(bills);
        bills.forEach(b-> {
            simulatePaymentEquivalent(b.getBillId(), b.getAmount(),
                    new SimpleDateFormat("dd/MM/yyyy").format(b.getDueDate()),
                    PaymentStates.PENDING.name(), b.getBillId());
        });

    }

    @Override
    public void writeObjectBill(List<Bill> bills){
        try{
            FileOutputStream file = new FileOutputStream("bills.txt");
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(bills);
            obj.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Bill> readObject(){
        List<Bill> bills = new ArrayList<>();
        try{
            FileInputStream file = new FileInputStream("bills.txt");
            ObjectInputStream input = new ObjectInputStream(file);
            bills = (List) input.readObject();
            input.close();
            file.close();
        } catch(ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return bills;
    }

    private void updatePaymentEquivalent(Integer paymentId, Long amount, String paymentDate,
                                           String state, Integer billId){
        Payment p = new Payment(paymentId, amount, paymentDate,
                state, billId);
        paymentServices = new PaymentServicesImpl();
        paymentServices.update(p);
    }

    private void simulatePaymentEquivalent(Integer paymentId, Long amount, String paymentDate,
                                         String state, Integer billId){
        Payment p = new Payment(paymentId, amount, paymentDate,
                state, billId);
        paymentServices = new PaymentServicesImpl();
        paymentServices.addPayment(p);
    }

    @Override
    public Bill getBillById(int id) {
        Bill bill = new Bill();
        for(Bill b : getListBill()){
            if(b.getBillId()==id){
                return b;
            }
        }
        return bill;
    }

    @Override
    public List<Bill> getBillByProvider(String provider) {
        List<Bill> bills = getListBill();
        return bills.stream().filter(b-> b.getProvider().equals(provider))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bill> getBillByState(String state) {
        List<Bill> bills = getListBill();
        return bills.stream().filter(b-> b.getState().equals(state.toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bill> sortedBillByDueDate() {
        List<Bill> bills = getListBill();
        return bills.stream().filter(b -> b.getState().equals(BillStates.NOT_PAID.toString()))
                .sorted(Comparator.comparing(Bill::getDueDate)).collect(Collectors.toList());
    }

    @Override
    public void paid(int idBill, Account account) {
        Bill bill = getBillById(idBill);
        accountService = new AccountServicesImpl();
        if(bill.getBillId() != null && account.getId() != null){
            long balance = accountService.getCurrenttUser().getBalance();
            if(balance < bill.getAmount()){
                System.out.println("The balance doesn't enough for bill " + idBill
                        +" with balance: " + balance);
            } else if (BillStates.PAID.toString().equals(bill.getState())) {
                System.out.println("The bill " + idBill + " has been paid!");
            } else {
                balance -= bill.getAmount();
                System.out.println("Paid bill " + idBill + " successfully!");

                account.setBalance(balance);
                account = accountService.updateBalance(account);
                System.out.println("Current balance: " + account.getBalance());

                bill.setState(BillStates.PAID.toString());
                update(bill);

                Payment payment = new Payment(bill.getBillId(), bill.getAmount(), bill.getDueDate(),
                        PaymentStates.PROCESSED.toString(), bill.getBillId());
                paymentServices.update(payment);
            }
        } else{
            System.out.println("Not found bill!");
        }
    }

    @Override
    public void strategyPaidByDueDate(int idBill, Date dueDate, Account account) {
        List<Bill> list = getListBill();
        Bill bill = list.stream().filter(b->b.getBillId().equals(idBill))
                .collect(Collectors.toList()).get(0);
        Date today = new Date(System.currentTimeMillis());
        if(dueDate.before(today)){
            System.out.println("The scheduled date has passed so please pay ASAP! System is checking... ");
            schedulePaidByDueDate(idBill, today, account);
        } else {
            System.out.println("Payment for bill " + idBill + " is scheduled on " + dueDate);
            schedulePaidByDueDate(idBill, dueDate, account);
        }
    }

    private void schedulePaidByDueDate(int idBill, Date dueDate, Account account) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                paid(idBill, account);
            }
        };
        Timer timer = new Timer("Schedule");
        timer.schedule(task, dueDate);
    }
}

