package com.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Rotor {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String wiring;
    Character notch;

    public Rotor(String wiring, Character notch){
        this.wiring = wiring;
        this.notch = notch;
    }

    public int forward(int input){
        return this.alphabet.indexOf((this.wiring.charAt(input)));
    }

    public int backward(int input){
        return this.wiring.indexOf(this.alphabet.charAt(input));
    }

    public void rotate(int amount){
        this.alphabet = this.alphabet.substring(amount) + this.alphabet.substring(0, amount);
        this.wiring = this.wiring.substring(amount) + this.wiring.substring(0, amount);
    }

    public void show(){
        System.out.println(this.alphabet);
        System.out.println(this.wiring);
    }

    public void rotateTo(Character c){
        this.rotate(this.alphabet.indexOf(c));
    }

    public void setAlphabet(String newAlphabet){
        this.wiring = newAlphabet;
    }
}
