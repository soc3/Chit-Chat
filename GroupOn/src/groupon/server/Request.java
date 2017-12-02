/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.server;

import groupon.user.Utilities;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author sushant oberoi
 */
public class Request {
    private Socket client;
    
    public Socket getClientSocket(){
        try{
            System.out.println("getting socket");
            client = new Socket(Utilities.IP, Utilities.PORT);
            return client;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"cannot connect to server");
        }
        return null;
    }
}
