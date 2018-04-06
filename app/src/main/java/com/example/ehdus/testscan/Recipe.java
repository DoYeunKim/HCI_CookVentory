package com.example.ehdus.testscan;

public class Recipe {
    private String name, desc;
    private int pic;


    private String id;
    private String title;
    private Integer rating;

    public Recipe(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    Recipe(String in1, String in2, int in3) {
        name = in1;
        desc = in2;
        pic = in3;
    }

    public String getName() {
        return name;
    }

    public void setName(String in) {
        name = in;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String in) {
        desc = in;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int in) {
        pic = in;
    }
}
