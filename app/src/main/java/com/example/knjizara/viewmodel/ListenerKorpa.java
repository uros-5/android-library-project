package com.example.knjizara.viewmodel;

import android.content.Context;

public class ListenerKorpa {
    public int counter = 0;
    public KorpaSP korpaSP;
    public ListenerKorpa (Context context) {
        korpaSP = new KorpaSP(context);
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
    public int getCounter() {
        return counter;
    }
    public int getSizeOfSP() {
        return korpaSP.getSizeOfKorpa();
    }
    public boolean compare () {
        if(getCounter()==getSizeOfSP()) {
            return true;
        }
        else {
            return false;
        }
    }
}
