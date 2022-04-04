package com.example.karveliasae;

public class Cigarettes {
    private String name;
    private String ws_price;
    private String re_price;
    private int qu;

    public Cigarettes(){

    }
    public Cigarettes(String name,String ws_price,String re_price,int qu){
        this.name=name;
        this.ws_price=ws_price;
        this.re_price=re_price;
        this.qu=qu;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getWs_price(){
        return ws_price;
    }
    public void setWs_price(String ws_price){
        this.ws_price=ws_price;
    }
    public String getRe_price(){
        return re_price;
    }
    public void setRe_price(String re_price){
        this.re_price=re_price;
    }
    public int getQu(){
        return qu;
    }
    public void setQu(int qu){
        this.qu=qu;
    }



}