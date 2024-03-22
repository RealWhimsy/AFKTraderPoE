package de.realwhimsy.afktraderpoe.datamodel;

public class Transaction {

    private String buyer;
    private Price price;
    private String league;
    private Item item;

    public Transaction(String buyer, Price price, String league, Item item) {
        this.buyer = buyer;
        this.price = price;
        this.league = league;
        this.item = item;
    }

    public String getBuyer() {
        return buyer;
    }

    public Price getPrice() {
        return price;
    }

    public String getLeague() {
        return league;
    }

    public Item getItem() {
        return item;
    }
}
