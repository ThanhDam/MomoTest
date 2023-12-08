package services.impl;

import model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import services.AccountServices;

import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServicesImplTest {
    AccountServices service = new AccountServicesImpl();

    @BeforeEach
    public void clear(){
        File file = new File("currentUser.txt");
        file.delete();
    }


    @Test
    @Order(2)
    public void addAmount() throws InterruptedException {
        Account acc = service.getCurrenttUser();
        long balance = acc.getBalance();
        acc = service.addAmount(10000);
        Thread.sleep(2000);
        Assert.assertEquals(balance + 10000, (long)acc.getBalance());
    }

    @Test
    @Order(3)
    public void updateBalance() {
        Account accOld = new Account(1, "Nguyen Van A", 0l);
        Assert.assertEquals(1, (int)accOld.getId());
        Assert.assertEquals(0, (long)accOld.getBalance());

        accOld.setBalance((long)500000);
        Account afterUpdate = service.updateBalance(accOld);
        Assert.assertEquals(500000, (long)afterUpdate.getBalance());
    }


    @Test
    @Order(1)
    public void getCurrentUser() throws InterruptedException {
        Account acc = service.getCurrenttUser();
        Thread.sleep(2000);
        Assert.assertNotNull(acc);
    }


}