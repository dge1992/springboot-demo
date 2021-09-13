package com.github.dge1992.fish.domain;

import java.math.BigDecimal;

/**
 * @author dge
 * @version 1.0
 * @date 2021-03-30 11:19
 */
public class Person {

    private String name;

    private Integer age;

    private BigDecimal money;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getMoney() {
        return this.money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", money=" + money +
                '}';
    }
}
