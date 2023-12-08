package model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment implements Serializable {
    private Integer paymentId;
    private Long amount;
    private Date paymentDate;
    private String state;
    private Integer billId;

    public Payment() {
    }

    public Payment(Integer paymentId, Long amount, Date paymentDate, String state, Integer billId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.state = state;
        this.billId = billId;
    }

    public Payment(Integer paymentId, Long amount, String paymentDate, String state, Integer billId) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        this.paymentId = paymentId;
        this.amount = amount;
        try{
            this.paymentDate = format.parse(paymentDate);
        } catch (ParseException ex){
            ex.printStackTrace();
        }
        this.state = state;
        this.billId = billId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    @Override
    public String toString() {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentDate=" + format.format(paymentDate) +
                ", state='" + state + '\'' +
                ", billId=" + billId +
                '}';
    }
}
