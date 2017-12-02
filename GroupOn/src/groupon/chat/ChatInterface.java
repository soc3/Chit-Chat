/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.chat;

/**
 *
 * @author sushant oberoi
 */
public interface ChatInterface {
    public void acceptGroupRequest(String name, String mem1, String mem2, int type);
    public void acceptRequest(String username, int type); // type -> acccept or delete
    public boolean sendRequest(String username); 
    public void sendMessage(String sendTo, String message);
    public void sendGroupMessage(String groupName, String message);
}
