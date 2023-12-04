package com.teamproject2.models;
import android.os.Parcel;
import android.os.Parcelable;

public class RecipeItem implements Parcelable {
    private String name;
    private String ingredient;
    private String[] steps;

    public RecipeItem(String name, String ingredients, String[] steps) {
        this.name = name;
        this.ingredient = ingredients;
        this.steps = steps;
    }

    public RecipeItem(String name, String ingredient) {
        this.name = name;
        this.ingredient = ingredient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String[] getSteps() {
        return steps;
    }

    public void setSteps(String[] steps) {
        this.steps = steps;
    }

    protected RecipeItem(Parcel in) {
        name = in.readString();
        ingredient = in.readString();
        steps = in.createStringArray();
    }

    public static final Creator<RecipeItem> CREATOR = new Creator<RecipeItem>() {
        @Override
        public RecipeItem createFromParcel(Parcel in) {
            return new RecipeItem(in);
        }

        @Override
        public RecipeItem[] newArray(int size) {
            return new RecipeItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ingredient);
        dest.writeStringArray(steps);
    }
}

