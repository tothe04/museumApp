package com.example.museum;

public class ExhibitItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private String available;

    public ExhibitItem() {
    }

    public ExhibitItem(String name, String info, String price, String available) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.available = available;
    }

    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String getPrice() {
        return price;
    }
    public String getAvailable() {
        return available;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String _getId() {
        return this.id;
    }

}
