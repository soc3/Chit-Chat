/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.ObserverPattern;

/**
 *
 * @author sushant oberoi
 */
public interface Subject {

    public void register(Observer o);

    public void unregister(Observer o);

    public void notifyObserver();
}
