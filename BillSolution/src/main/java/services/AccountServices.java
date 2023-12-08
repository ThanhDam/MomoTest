package services;

import model.Account;

public interface AccountServices {
    Account addAmount(long amount);
    Account updateBalance(Account account);
    Account getCurrenttUser();
}
