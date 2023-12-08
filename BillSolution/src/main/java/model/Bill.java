package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bill implements Serializable {
    private Integer billId;
    private String type;
    private Long amount;
    private Date dueDate;
    private String state;
    private String provider;

    public Bill() {
    }

    public Bill(Integer billId, String type, Long amount, Date dueDate, String state, String provider) {
        this.billId = billId;
        this.type = type;
        this.amount = amount;
        this.dueDate = dueDate;
        this.state = state;
        this.provider = provider;
    }

    public Bill(Integer billId, String type, Long amount, String dueDate, String state, String provider) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        this.billId = billId;
        this.type = type;
        this.amount = amount;
        try{
            this.dueDate = format.parse(dueDate);
        } catch (ParseException ex){
            ex.printStackTrace();
        }
        this.state = state;
        this.provider = provider;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return "Bill{" +
                "billId=" + billId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", dueDate=" + format.format(dueDate) +
                ", state='" + state + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}

