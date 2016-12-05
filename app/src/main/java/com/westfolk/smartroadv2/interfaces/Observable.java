package com.westfolk.smartroadv2.interfaces;

/**
 * Created by thibault on 05/12/2016.
 */
public interface Observable {

    public abstract void notifyChanges();

    public abstract void addObserver(Observer obs);
}
