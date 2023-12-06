package com.teamproject2.models;

public class Recipe {
    private String recipeCode;
    private String recipeName;
    private String summary;
    private String typeCode;
    private String type;
    private String classCode;
    private String recipeClass;
    private String time;
    private String kcal;
    private String amount;
    private String difficulty;
    private String ingredientCategory;
    private String price;
    private String imageUrl;
    private String citeUrl;

    // Default constructor
    public Recipe() {
    }

    // Parameterized constructor
    public Recipe(String recipeCode, String recipeName, String summary, String typeCode, String type,
                  String classCode, String recipeClass, String time, String kcal, String amount,
                  String difficulty, String ingredientCategory, String price, String imageUrl,
                  String citeUrl) {
        this.recipeCode = recipeCode;
        this.recipeName = recipeName;
        this.summary = summary;
        this.typeCode = typeCode;
        this.type = type;
        this.classCode = classCode;
        this.recipeClass = recipeClass;
        this.time = time;
        this.kcal = kcal;
        this.amount = amount;
        this.difficulty = difficulty;
        this.ingredientCategory = ingredientCategory;
        this.price = price;
        this.imageUrl = imageUrl;
        this.citeUrl = citeUrl;
    }

    // Getter and setter methods for each attribute

    public String getRecipeCode() {
        return recipeCode;
    }

    public void setRecipeCode(String recipeCode) {
        this.recipeCode = recipeCode;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getRecipeClass() {
        return recipeClass;
    }

    public void setRecipeClass(String recipeClass) {
        this.recipeClass = recipeClass;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getIngredientCategory() {
        return ingredientCategory;
    }

    public void setIngredientCategory(String ingredientCategory) {
        this.ingredientCategory = ingredientCategory;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCiteUrl() {
        return citeUrl;
    }

    public void setCiteUrl(String citeUrl) {
        this.citeUrl = citeUrl;
    }
}
