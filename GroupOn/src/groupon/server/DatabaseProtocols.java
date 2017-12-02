/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.server;

import groupon.user.User;
import java.sql.ResultSet;

/**
 *
 * @author sushant oberoi
 */
public interface DatabaseProtocols {
    public String fetchGroups();
    public void deleteUnreadMessages(String name);
    public void updateUnreadMessages(String name, String message);
    public String fetchUnreadMessages(String name);
    public void acceptGroupRequest(String name, String mem1, String mem2, int type);
    public String getPriorityMessages(String username);
    public void addToPriority(String username, String userid, String message);
    public void update(String table, String user1, String user2, short flag);
    public void updateGroupMessages(String name, String member, String message);
    public String fetchGroupMembers(String groupName);
    public void update(String table, String user1, String user2, String message);
    public String AlreadyExists(String table, String username);
    public String getGroupRequests(String username);
    public boolean insert(String table, User user);
    public void sendGroupRequest(String name, String mem1, String mem2, short req);
    public String fetch(String table);
    public String fetchFriends(String table, String user);
    public String getUserInfo(String username);
    public boolean insert(String user1, String user2, short flag, String message);
    public void update(String table, String username, String newPass);
    public void addGroup(String table, String name, String owner);
    public String fetchGroups(String username);
   // public boolean insert(String table, Group group);
   // public String fetchOwners(String username);
   // public String fetchGroups(String username);
}
