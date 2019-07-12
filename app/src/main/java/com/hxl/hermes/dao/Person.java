package com.hxl.hermes.dao;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class Person {

    private String name;
    private String pass;

    public Person(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
