package org.example;

import model.Account;
import model.Bill;
import model.Payment;
import services.AccountServices;
import services.BillServices;
import services.PaymentServices;
import services.impl.AccountServicesImpl;
import services.impl.BillServicesImpl;
import services.impl.PaymentServicesImpl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    static BillServices billService;
    static AccountServices accountService;
    static PaymentServices paymentServices;
    protected static void cashIn(long amount) throws IOException {
        Account account = accountService.addAmount(amount);
        System.out.println("Balance: " + account.getBalance());
    }

    protected static void pay(int idBill, Account acc){
        billService.paid(idBill, acc);
    }

    protected static void getListBill(){
        List<Bill> list = billService.getListBill();
        list.forEach(b -> System.out.println(b.toString()));
    }

    protected static void searchBillByProvider(String provider){
        List<Bill>list = billService.getBillByProvider(provider);
        list.forEach(b -> System.out.println(b.toString()));
    }

    protected static void addBill(Bill bill) throws Exception {
        billService.create(bill);
        System.out.println("Add bill successful!");
    }

    protected static void getSortedBillByDueDate(){
        List<Bill>list = billService.sortedBillByDueDate();
        list.forEach(b -> System.out.println(b.toString()));
    }

    protected static void getTransaction(){
        List<Payment> list = paymentServices.getAll();
        list.forEach(p-> System.out.println(p.toString()));
    }

    protected static void schedule(int idBill, Date dueDate, Account acc){
        List<Bill> list = billService.getListBill();
        Bill bill = list.stream().filter(b->b.getBillId().equals(idBill))
                .collect(Collectors.toList()).get(0);
        if(dueDate.getTime() < bill.getDueDate().getTime()){
            billService.strategyPaidByDueDate(idBill, dueDate, acc);
            System.out.println("Payment for bill " + idBill + " is scheduled on " + dueDate);
        } else{
            System.out.println("Scheduled date is invalid!");
        }
    }

    protected static void specificFunction(String[] args, Account myself) throws Exception {
        String keyword = args[0];
        billService = new BillServicesImpl();
        switch(keyword.toUpperCase(Locale.ROOT)){
            case "CASH_IN":{
                Long balance = Long.valueOf(args[1]);
                cashIn(balance);
                break;
            }
            case "LIST_BILL":{
                getListBill();
                break;
            }
            case "PAY":{
                List<String> tmp = Arrays.asList(args);
                List<Integer> billIds = new ArrayList<>();
                for(int i = 1; i< tmp.size(); i++){
                    billIds.add(Integer.valueOf(tmp.get(i)));
                }
                billIds.forEach(billId -> pay(billId, myself));
                break;
            }
            case "DUE_DATE":{
                getSortedBillByDueDate();
                break;
            }

            case "SEARCH_BILL_BY_PROVIDER":{
                String provider = args[1];
                searchBillByProvider(provider);
                break;
            }

            case "ADD_BILL":{
                //simulate input bill information:
                // 4, "TEST_CREATE", 100000l,"30/9/2020", "NOT_PAID", "ACB"
                System.out.println("Enter bill information " +
                        "(eg: 4, TEST_CREATE, 100000, 30/9/2020, NOT_PAID, ACB): ");
                Scanner scanner = new Scanner(System.in);
                String info = scanner.nextLine();
                String[] arguments = info.split(", ");
                Bill newBill = new Bill(Integer.valueOf(arguments[0]),arguments[1],
                        Long.valueOf(arguments[2]), arguments[3], arguments[4], arguments[5]);
                addBill(newBill);
                getListBill();
                break;
            }

            case "LIST_PAYMENT":{
                paymentServices = new PaymentServicesImpl();
                getTransaction();
                break;
            }

            case "SCHEDULE":{
                List<String> tmp = Arrays.asList(args);
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                int idBill = Integer.valueOf(tmp.get(1));
                Date dueDate = format.parse(tmp.get(2));
                schedule(idBill, dueDate, myself);
                break;
            }

            case "EXIT":{
                System.exit(0);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        accountService = new AccountServicesImpl();
        Account myself = accountService.getCurrenttUser();
        while(true){
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] data = input.split(" ");
            specificFunction(data, myself);
        }
    }
}