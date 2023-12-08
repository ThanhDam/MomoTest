package services.impl;

import model.Bill;
import model.Payment;
import services.BillServices;
import services.PaymentServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentServicesImpl implements PaymentServices {
    BillServices billServices;
    @Override
    public Payment addPayment(Payment payment) {
        List<Payment> list = new ArrayList<>();
        List<Bill> listBill = new ArrayList<>();
        if(new File("payments.txt").exists()){
            list = getAll();
        }
        if(new File("bills.txt").exists()){
            billServices = new BillServicesImpl();
            listBill = billServices.getListBill();
        }
        if(list.stream().anyMatch(p->p.getPaymentId().equals(payment.getPaymentId()))
         || listBill.stream().noneMatch(b->b.getBillId().equals(payment.getBillId()))){
            return null;
        }
        list.add(payment);
        writeObject(list);
        return payment;
    }

    @Override
    public void writeObject(List<Payment> payments){
        try{
            FileOutputStream file = new FileOutputStream("payments.txt");
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(payments);
            obj.close();
            file.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Payment> readObject(){
        List<Payment> list = new ArrayList<>();
        try{
            FileInputStream file = new FileInputStream("payments.txt");
            ObjectInputStream input = new ObjectInputStream(file);
            list = (ArrayList) input.readObject();
            input.close();
            file.close();
        } catch(ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> list = new ArrayList<>();
        if(new File("payments.txt").exists()){
            list = readObject();
        } else{
            System.out.println("Not found payment");
        }
        return list;
    }

    @Override
    public void update(Payment payment) {
        List<Payment> list = getAll();
        Payment pm = list.stream().filter(p-> p.getPaymentId().equals(payment.getBillId()))
                .collect(Collectors.toList()).get(0);
        list.remove(pm);
        list.add(payment);
        writeObject(list);
    }

}
