package com.teamproject2.models;

public class CVS_item {
    private String brandName;
    private String productName;
    private String categoryName;
    private String price;
    private String eventName;
    private String imageURL;

    public CVS_item(String brandName, String productName, String categoryName, String price, String eventName, String imageURL) {
        this.brandName = brandName;
        this.productName = productName;
        this.categoryName = categoryName;
        this.price = price;
        this.eventName = eventName;
        this.imageURL = imageURL;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPrice() {
        return price;
    }

    public String getEventName() {
        return eventName;
    }
    public String getImageURL() {
        return imageURL;
    }
}
