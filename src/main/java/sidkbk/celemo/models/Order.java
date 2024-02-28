package sidkbk.celemo.models;

import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @DBRef
    private User sellerAccount;
    @DBRef
    private User buyerAccount;
    @DBRef
    private Auction auction;
    private String productTitle;
    private double endPrice;

    private Date createdAt = new Date();

    public Order() {
    }


    public User getSellerAccount() {
        return sellerAccount;
    }
    public void setSellerAccount(User sellerAccount) {
        this.sellerAccount = sellerAccount;
    }
    public User getBuyerAccount() {
        return buyerAccount;
    }
    public void setBuyerAccount(User buyerAccount) {
        this.buyerAccount = buyerAccount;
    }
    public Auction getAuction() {
        return auction;
    }
    public void setAuction(Auction auction) {
        this.auction = auction;
    }
    public double getEndPrice() {
        return endPrice;
    }
    public void setEndPrice(double endPrice) {
        this.endPrice = endPrice;
    }

    public String getId() {
        return id;
    }
}
