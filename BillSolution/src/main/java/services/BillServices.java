package services;

import model.Account;
import model.Bill;

import java.util.Date;
import java.util.List;

public interface BillServices {

    void writeObjectBill(List<Bill> bills);
    List<Bill> readObject();
    Bill create(Bill bill) throws Exception;
    void update(Bill updateBill);
    boolean delete(int id);

    List<Bill> getListBill();

    Bill getBillById(int id);

    List<Bill> getBillByProvider(String provider);

    List<Bill> getBillByState(String state);

    List<Bill> sortedBillByDueDate();

    void paid(int idBill, Account acc);

    void strategyPaidByDueDate(int idBill, Date dueDate, Account account);
}
