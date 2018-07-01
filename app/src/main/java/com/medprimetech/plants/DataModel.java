package com.medprimetech.plants;


import android.graphics.Bitmap;

public class DataModel {


    String name;
    String author;
    double id_;
    Bitmap image;
    String imageURL;

    public DataModel(String name, String author, double id_, Bitmap image) {
        this.name = name;
        this.author = author;
        this.id_ = id_;
        this.image=image;
    }

    public DataModel(String name, String author, double id_, String imageURL) {
        this.name = name;
        this.author = author;
        this.id_ = id_;
        this.imageURL=imageURL;
    }


    public String getName() {
        return name;
    }


    public String getAuthor() {
        return author;
    }

    public Bitmap getImage() {
        return image;
    }

    public double getId() {
        return id_;
    }
}