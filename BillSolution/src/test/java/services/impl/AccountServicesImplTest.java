package services.impl;

import model.Account;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import services.AccountServices;

import java.io.File;


public class AccountServicesImplTest {
    AccountServices service = new AccountServicesImpl();

    @BeforeEach
    public void clear(){
        File file = new File("currentUser.txt");
        file.delete();
    }


    @Test
    public void addAmount() throws InterruptedException {
        Account acc = service.getCurrenttUser();
        long balance = acc.getBalance();
        acc = service.addAmount(10000);
        Thread.sleep(2000);
        Assert.assertEquals(balance + 10000, (long)acc.getBalance());
    }

    @Test
    public void updateBalance() {
        Account accOld = new Account(1, "Nguyen Van A", 0l);
        Assert.assertEquals(1, (int)accOld.getId());
        Assert.assertEquals(0, (long)accOld.getBalance());

        accOld.setBalance((long)500000);
        Account afterUpdate = service.updateBalance(accOld);
        Assert.assertEquals(500000, (long)afterUpdate.getBalance());
    }


    @Test
    public void getCurrentUser() throws InterruptedException {
        clear();
        Account acc = service.getCurrenttUser();
        Thread.sleep(2000);
        Assert.assertNotNull(acc);
    }


}