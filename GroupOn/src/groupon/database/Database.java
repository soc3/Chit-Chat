/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.database;

import groupon.server.DatabaseProtocols;
import groupon.user.User;
import groupon.user.Utilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author sushant oberoi
 */
public class Database implements DatabaseProtocols {

    private Connection connection;
    private PreparedStatement stmt;

    public Database() {
        try {
            Class.forName(Utilities.JDBC_DRIVER);
            connection = DriverManager.getConnection(Utilities.JDBC_PATH, "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot connect to Database");
        }
    }
    public String fetchGroupMembers(String groupName){
        System.out.println(groupName);
        String query = "SELECT * FROM grouprequests where Name=? and Request=?";
        System.out.println(query);
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, groupName);
            stmt.setInt(2, 1);
            ResultSet rst = stmt.executeQuery();
            String ans = "";
            while (rst.next()) {
                String rst1 = rst.getString(2), rst2 = rst.getString(3);
                ans += (rst1 + " " + rst2 + " ");
            }
            return ans;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void addToPriority(String username, String userid, String message){
        try{
            String query = "insert into prioritymessages" +  " values(?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setInt(2, Integer.parseInt(userid));
            stmt.setString(3, message);
            stmt.executeUpdate();
            System.out.println("Record inserted");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public String getPriorityMessages(String username){
        String ans = "";
        try{
            String query = "select * from prioritymessages where username=?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rst = stmt.executeQuery();
            ans = "";
            while(rst.next()){
                ans += (rst.getString(3) + "delimiter ");
            }
            System.out.println("Record inserted");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return ans;
    }
    public String AlreadyExists(String table, String username) {
        String query = "select * from " + table;
        try {
            stmt = connection.prepareStatement(query);
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                if (rst.getString(1).equals(username)) {
                    return rst.getString(3) + " " + rst.getString(2) + " " + rst.getString(4);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error............");
        }
        return null;
    }

    public boolean insert(String table, User user) {
        try {
            if (AlreadyExists(table, user.getCredentials().getUsername()) != null) {
                return false;
            }
            String query = "insert into " + table + " values(?,?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user.getCredentials().getUsername());
            stmt.setString(2, user.getCredentials().getEmail());
            stmt.setString(3, user.getCredentials().getPassword());
            stmt.setString(4, user.getCredentials().getProfileImage());
            stmt.executeUpdate();
            System.out.println("Record inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot insert user into table");
        }
        return true;
    }
    /* public String fetchOwners(String username){
     System.out.println(username);
     String query = "SELECT * FROM groups where Owner =?";
     System.out.println(query);
     try {
     stmt = connection.prepareStatement(query);
     stmt.setString(1, username);
     ResultSet rst = stmt.executeQuery();
     String ans = "";
     while (rst.next()) {
     String rst1 = rst.getString(1);
     ans += (rst1);
     ans += " ";
     }
     System.out.println(ans);
     return ans;
     } catch (SQLException ex) {
     Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
     }
     return null;
     }
     public String fetchGroups(String username){
     System.out.println(username);
     String query = "SELECT * FROM groups where Owner =?";
     System.out.println(query);
     try {
     stmt = connection.prepareStatement(query);
     stmt.setString(1, username);
     ResultSet rst = stmt.executeQuery();
     String ans = "";
     while (rst.next()) {
     String rst1 = rst.getString(1), rst2 = rst.getString(2);
     String rst3 = rst.getString(3);
     System.out.println("rst3 " + rst3.length());
     if(rst3.equals(" ") || rst3 == null) continue;
     int rst4 = rst.getInt(4); 
     System.out.println(rst1 + " " + rst2 + " " + rst3 + " " + username);
     ans += (rst1 + " " + rst3 + " " + rst4);
     ans += " ";
     }
     System.out.println("ans s"+ ans);
     return ans;
     } catch (SQLException ex) {
     Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
     }
     return null;
     }
     public boolean insert(String table, Group group){
     try {
     if (AlreadyExists(table, group.getName()) != null) {
     return false;
     }
     String query = "insert into " + table + " values(?,?,?,?)";
     System.out.println(query);
     stmt = connection.prepareStatement(query);
     stmt.setString(1, group.getName());
     stmt.setString(2, group.getOwner());
     System.out.println(group.getName()+ " " + group.getOwner());
     stmt.setString(3, " ");
     stmt.setInt(4, 0);
     stmt.executeUpdate();
     System.out.println("Record inserted");
     } catch (Exception e) {
     JOptionPane.showMessageDialog(null, "Cannot insert group into table");
     }
     return true;
     }*/

    public ResultSet AlreadyExists(String table, String user1, String user2) {
        String query = "select * from " + table;
        try {
            stmt = connection.prepareStatement(query);
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                if (rst.getString(1).equals(user1) && rst.getString(2).equals(user2)) {
                    return rst;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error............");
        }
        return null;
    }

    public void addGroup(String table, String name, String owner) {
        try {
            String query = "insert into " + table + " values(?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, owner);
            stmt.executeUpdate();
            System.out.println("Record inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot insert user into table");
        }
    }
    public void sendGroupRequest(String name, String mem1, String mem2, short req){
        try {
            String query = "insert into grouprequests " + " values(?,?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, mem1);
            stmt.setString(3, mem2);
            stmt.setInt(4, req);
            stmt.executeUpdate();
            System.out.println("Record inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot insert user into table");
        }
    }
    public String getGroupRequests(String username){
        System.out.println(username);
        String query = "SELECT * FROM grouprequests WHERE Member2=? OR Request=?";
        System.out.println(query);
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setInt(2, 1);
            ResultSet rst = stmt.executeQuery();
            String ans = "";
            while (rst.next()) {
                String rst1 = rst.getString(1), rst2 = rst.getString(2);
                String rst3 = rst.getString(3);
                int rst4 = rst.getInt(4);
                String grp = "group";
                grp += rst1;
                if(rst3.equals(username))
                    ans += (rst2 + " " + grp + " " + rst4 + " ");
                else
                    ans += (rst3 + " " + grp + " " + rst4 + " ");
            }
            return ans;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String fetchGroups(String username) {
        System.out.println(username);
        String query = "SELECT * FROM groups WHERE Owner=? ";
        System.out.println(query);
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rst = stmt.executeQuery();
            String ans = "";
            while (rst.next()) {
                ans += (rst.getString(1) + " ");
                System.out.println(ans);
            }
            return ans;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void update(String table, String username, String newPass) {
        System.out.println("updating password");
        try {
            String query = "update " + table + " set password=? where username=?";
            System.out.println(query);
            stmt = connection.prepareStatement(query);
            stmt.setString(1, newPass);//1 specifies the first parameter in the query i.e. name  
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateGroupMessages(String name, String member, String message){
        try {
            String query = "insert into groupmessages " + " values(?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, member);
            stmt.setString(3, message);
            stmt.executeUpdate();
            System.out.println("Record inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot insert user into table");
        }
    }
    public String fetchUnreadMessages(String name){
        System.out.println(name);
        String query = "SELECT * FROM unreadmessages WHERE Username=? ";
        System.out.println(query);
        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rst = stmt.executeQuery();
            String ans = "";
            while (rst.next()) {
                ans += (rst.getString(2) + "@");
                System.out.println(ans);
            }
            return ans;
        } catch (Exception ex) {
            System.out.println(ex);
         }
        return null;
    }
    public void updateUnreadMessages(String name, String message){
        try {
            String query = "insert into unreadmessages values(?,?) ";
            System.out.println(query);
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);//1 specifies the first parameter in the query i.e. name  
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteUnreadMessages(String name){
        try {
            String query = "delete from unreadmessages " + " where Username=?";
            System.out.println(query);
            stmt = connection.prepareStatement(query);
            stmt.setString(1, name);//1 specifies the first parameter in the query i.e. name  
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchFriends(String table, String username) {
        System.out.println(username);
        String query = "SELECT * FROM friends WHERE Request=? OR Friend =?";
        System.out.println(query);
        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, 1);
            stmt.setString(2, username);
            ResultSet rst = stmt.executeQuery();
            String ans = "";
            while (rst.next()) {
                String rst1 = rst.getString(1), rst2 = rst.getString(2);
                int rst3 = rst.getInt(3);
                System.out.println(rst1 + " " + rst2 + " " + rst3 + " " + username);
                if (rst3 == 1) {
                    if ((!rst1.equals(username)) && rst2.equals(username)) {
                        ans += rst1 + " " + "friend " + rst3;
                        ans += " ";
                    } else if ((!rst2.equals(username)) && rst1.equals(username)) {
                        ans += rst2 + " " + "friend " + rst3;
                        ans += " ";
                    } else if (rst2.equals(username)) {
                        ans += rst1 + " " + "friend " + rst3;
                        ans += " ";
                    }
                } else if (rst2.equals(username)) {
                    ans += rst1 + " " + "friend " + rst3;
                    ans += " ";
                }
            }
            return ans;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void update(String table, String user1, String user2, short flag) {
        System.out.println("hey1");
        try {
            String query = "update " + table + " set Request=? where Username=? AND Friend =?";
            System.out.println(query);
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, flag);//1 specifies the first parameter in the query i.e. name  
            stmt.setString(2, user2);
            stmt.setString(3, user1);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String fetchGroups(){
        String ans = "";
        try{
            String query = "select * from groups";
            stmt = connection.prepareStatement(query);
            ResultSet rst = stmt.executeQuery();
            while(rst.next()){
                ans += ("group@" + rst.getString(2) + "@" + rst.getString(1) + "@ ");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return ans;
    }
    public void acceptGroupRequest(String name, String mem1, String mem2, int type){
        try{
            String query = "update grouprequests" + " set Request=? where Member1=? AND Member2 =?";
            System.out.println(query);
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, type);//1 specifies the first parameter in the query i.e. name  
            stmt.setString(2, mem1);
            stmt.setString(3, mem2);
            stmt.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void update(String table, String user1, String user2, String message) {
        System.out.println("hey2");
        try {
            String query = "update " + table + " set Message=? where Username=? AND Friend =?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, message);//1 specifies the first parameter in the query i.e. name  
            stmt.setString(2, user1);
            stmt.setString(3, user2);

            int i = stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean insert(String user1, String user2, short flag, String message) {
        String table = "friends";
        try {
            if (AlreadyExists(table, user1, user2) != null) {
                return false;
            }
            String query = "insert into " + table + " values(?,?,?,?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setShort(3, flag);
            stmt.setString(4, message);
            stmt.executeUpdate();
            System.out.println("Record inserted");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot insert user into table");
        }
        return true;
    }

    public String fetch(String table) {
        String query = "select * from " + table;
        ResultSet rst = null;
        try {
            stmt = connection.prepareStatement(query);
            rst = stmt.executeQuery();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error............");
        }
        String send = "";
        try {
            while (rst.next()) {
                String entity = rst.getString(1);
                send += entity;
                send += " ";
            }
        } catch (Exception e) {

        }
        return send;
    }

    public String getUserInfo(String username) {
        String query = "select * from user";
        try {
            stmt = connection.prepareStatement(query);
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                if (rst.getString(1).equals(username)) {
                    return rst.getString(1) + " " + rst.getString(2) + " " + rst.getString(3) + " " + rst.getString(4);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error............");
        }
        return null;
    }

}
