package com.example.ehdus.testscan;

import org.json.JSONObject;

class Ingredient {
    private String name, desc;
    private int pic;

    Ingredient(String in1, String in2, int in3) {
        name = in1;
        desc = in2;
        pic = in3;
    }

    Ingredient(JSONObject input) {
        //TODO: populate ingredient object
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getPic() {
        return pic;
    }
}
