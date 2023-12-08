package services.impl;

import model.Account;
import services.AccountServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AccountServicesImpl implements AccountServices {

    public Account addAmount(long amount) {
        Account acc = getCurrenttUser();
        if (acc != null) {
            long balance = acc.getBalance();
            acc.setBalance(amount + balance);
        }
        writeObject(acc);
        return acc;

    }

    @Override
    public Account updateBalance(Account account) {
        Account acc = getCurrenttUser();
        if (acc != null) {
            acc.setBalance(account.getBalance());
        }
        writeObject(acc);
        return acc;
    }

    @Override
    public Account getCurrenttUser() {
        Account account1 = null;
        if (!new File("currentUser.txt").exists()) {
            Account myself = new Account(1, "A", 0l);
            writeObject(myself);
        }
        try {
            FileInputStream file = new FileInputStream("currentUser.txt");
            ObjectInputStream input = new ObjectInputStream(file);
            account1 = (Account) input.readObject();
            input.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return account1;
    }

    private void writeObject(Account myself){
        try {
            FileOutputStream file = new FileOutputStream("currentUser.txt");
            ObjectOutputStream obj = new ObjectOutputStream(file);
            obj.writeObject(myself);
            obj.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
