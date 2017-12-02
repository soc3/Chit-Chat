/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author sushant oberoi
 */
import javax.swing.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import java.awt.*;
public class SearchBar {

    JFrame frame = new JFrame("");
    AutoCompleteDecorator decorator;
    JComboBox combobox;

    public SearchBar() {
        combobox = new JComboBox(arr);
        AutoCompleteDecorator.decorate(combobox);
        
    }
    
    public JComboBox getComboBox(){
        return combobox;
    }
}