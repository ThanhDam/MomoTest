package org.solution;

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

public class Main {
    static BillServices billService;
    static AccountServices accountService;
    static PaymentServices paymentServices;

    public static void init(){
        accountService = new AccountServicesImpl();
        billService = new BillServicesImpl();
        paymentServices = new PaymentServicesImpl();
    }

    protected static void getBalance(Account acc){
        System.out.println("The current balance: " + acc.getBalance());
    }
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

    protected static void searchBillByState(String state){
        List<Bill>list = billService.getBillByState(state);
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
        billService.strategyPaidByDueDate(idBill, dueDate, acc);
    }

    public static void specificFunction(String[] args, Account myself) throws Exception {
        String keyword = args[0];
        switch(keyword.toUpperCase(Locale.ROOT)){
            case "CHECK_BALANCE":{
                getBalance(myself);
                break;
            }
            case "CASH_IN":{
                long amount = Long.parseLong(args[1]);
                cashIn(amount);
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

            case "SEARCH_BILL_BY_STATE":{
                String state = args[1];
                searchBillByState(state);
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
                getTransaction();
                break;
            }

            case "SCHEDULE":{
                List<String> tmp = Arrays.asList(args);
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                int idBill = Integer.parseInt(tmp.get(1));
                Date dueDate = format.parse(tmp.get(2));
                schedule(idBill, dueDate, myself);
                break;
            }

            case "EXIT":{
                System.exit(38);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        init();
        Account myself = accountService.getCurrenttUser();
        while(true){
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] data = input.split("\\s");
            specificFunction(data, myself);
        }
    }
}