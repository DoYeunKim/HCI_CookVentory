package com.example.ehdus.testscan;

public class Recipe {
    private String name, desc;
    private int pic;

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
