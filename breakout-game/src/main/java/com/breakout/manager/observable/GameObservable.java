package com.breakout.manager.observable;

import com.breakout.manager.observer.GameObserver;

import java.util.ArrayList;
import java.util.List;

public interface GameObservable {
    List<GameObserver> observers = new ArrayList<>();

    default void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    default void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    default void notifyObservers() {
        for (GameObserver observer : observers) {
            observer.onGameDataChanged();
        }
    }
}
