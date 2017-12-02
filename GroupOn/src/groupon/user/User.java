/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.user;

import com.google.gson.Gson;
import groupon.ObserverPattern.Observer;
import groupon.chat.ChatInterface;
import groupon.server.Request;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import groupon.user.Group;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

/**
 *
 * @author sushant oberoi
 */
public class User implements ChatInterface, Observer {

    private Credentials account;
    private Socket client;
    private DataInputStream din;
    private DataOutputStream dout;
    private Request req;
    private List<String> friends;
    private Gson gson;
    private Object BitmapFactory;
    private ArrayList<Group> groups;

    public User() {
        groups = new ArrayList<Group>();
        gson = new Gson();
        friends = new ArrayList<String>();
        account = new Credentials();
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setAccount(Credentials account) {
        this.account = account;
    }

    public User(String username, String email, String password, String profileImage) {
        groups = new ArrayList<Group>();
        gson = new Gson();
        friends = new ArrayList<String>();
        account = new Credentials(username, email, password, profileImage);
    }

    public void sendGroupRequest(String member2, String name) {
        try {
            dout.flush();
            dout.writeUTF("grouprequest " + getCredentials().getUsername() + " " + member2 + " " + name);
        } catch (Exception e) {

        }
    }

    public ArrayList<String> getUserNetwork() {
        ArrayList<String> network = new ArrayList<String>();
        try {
            dout.flush();
            dout.writeUTF("userid " + getCredentials().getUsername());
            String recieve = din.readUTF();
            String[] array = recieve.split(" ");
            int myId = Integer.parseInt(array[1]);
            ArrayList<Integer> ids = UserNetwork.getFriends(myId);
            for (int i = 0; i < ids.size(); i++) {
                dout.flush();
                dout.writeUTF("idname " + ids.get(i));
                String name = din.readUTF();
                System.out.println(name);
                network.add(name);
            }
        } catch (Exception e) {
            System.out.println("ex getusernetwork" + e);
        }
        return network;
    }

    public void addToPriority(String message) {
        int id = getId(getCredentials().getUsername());
        try {
            dout.flush();
            dout.writeUTF("priority " + getCredentials().getUsername() + " " + Integer.toString(id) + " " + message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getPriorityMessages() {
        try {
            dout.flush();
            dout.writeUTF("getPrioMessages " + getCredentials().getUsername());
            return din.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    public int getId(String name) {
        int id = 1;
        try {
            dout.flush();
            dout.writeUTF("userid " + name);
            id = Integer.parseInt(din.readUTF());
        } catch (Exception e) {
            System.out.println(e);
        }
        return id;
    }

    public String getGroupRequests() {
        try {
            dout.flush();
            dout.writeUTF("getgrouprequests " + getCredentials().getUsername());
            return din.readUTF();
        } catch (Exception e) {
        }
        return null;
    }

    public void fillGroups() {
        try {
            dout.flush();
            dout.writeUTF("groups " + getCredentials().getUsername());
            System.out.println("give groups");
            String str = din.readUTF();
            System.out.println("got groups");
            System.out.println(str);
            String[] array = str.split(" ");
            int len = array.length;
            for (int i = 0; i < len; i++) {
                groups.add(new Group(array[i], getCredentials().getUsername()));
            }
        } catch (Exception e) {
        }
    }

    public Credentials getCredentials() {
        return account;
    }

    public void addGroup(Group g) {
        groups.add(g);
        try {
            dout.flush();
            dout.writeUTF("addgroup " + g.getName() + " " + g.getOwner());
        } catch (Exception e) {

        }
    }

    public void recieveImage() {
        try {
            //imageCount++;
            dout.flush();
            dout.writeUTF("sendprofile " + getCredentials().getUsername());
            String path = "H:\\softablitzProject\\GroupOn\\Client\\" + getCredentials().getUsername() + ".jpg";
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }

            byte[] bytes = new byte[16 * 1024];
            System.out.println("image store start");
            int count;
            while ((count = din.read(bytes)) > 0) {
                System.out.println(count);
                System.out.println("image hey");
                out.write(bytes, 0, count);
                break;
            }
            System.out.println("image ended");
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void recieveMedia(String ext) {
        try {
            //imageCount++;
            dout.flush();
            dout.writeUTF("sendmedia " + getCredentials().getUsername());
            String path = "H:\\softablitzProject\\GroupOn\\Client\\Media" + getCredentials().getUsername() + ".jpg";
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }

            byte[] bytes = new byte[16 * 1024];
            System.out.println("image store start");
            int count;
            while ((count = din.read(bytes)) > 0) {
                System.out.println(count);
                System.out.println("image hey");
                out.write(bytes, 0, count);
                break;
            }
            System.out.println("image ended");
            //rename the file here with exact extension
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insertInDB(String table, User user) {
        try {

            System.out.println("now inserting in client");
            dout.writeUTF("insert " + table + " " + user.toString());
            Thread.sleep(2000);
            String status = din.readUTF();
            System.out.println("sending image");
            try {
                dout.flush();
                dout.writeUTF("imagesend " + getCredentials().getUsername());
            } catch (Exception e) {
                System.out.println(e);
            }
            sendImage();
            System.out.println("image sent");
            Thread.sleep(1000);
            if (status.equals("accepted")) {
                JOptionPane.showMessageDialog(null, "Account Successfully created!!");
            } else {
                JOptionPane.showMessageDialog(null, "User already exists");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void acceptRequest(String username, int type) {
        try {
            dout.flush();
            System.out.println("");
            dout.writeUTF("update friends " + username + " " + getCredentials().getUsername() + " " + type);
            if (type == 1) {
                friends.add(username);
            } else {
                friends.remove(username);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getRecommendedUsers() {
        String ans = "";
        try {
            dout.flush();
            dout.writeUTF("getrecomm " + getCredentials().getUsername());
            ans = din.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ans;
    }

    public void sendImage() throws IOException {
        File file = new File(getCredentials().getProfileImage());
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        dout.flush();
        int count;
        while ((count = in.read(bytes)) > 0) {
            dout.flush();
            dout.write(bytes, 0, count);
            if (count < 1000) {
                break;
            }
        }
        in.close();
    }
    
    
    public void sendMedia(String path) throws IOException {
        File file = new File(path);
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        dout.flush();
        int count;
        while ((count = in.read(bytes)) > 0) {
            dout.flush();
            dout.write(bytes, 0, count);
            //if (count < 1000) {
              //  break;
            //}
        }
        in.close();
    }


    public void acceptGroupRequest(String name, String mem1, String mem2, int type) {
        try {
            dout.flush();
            dout.writeUTF("acceptgrouprequest " + name + " " + mem1 + " " + mem2 + " " + type);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void closeSocket() {
        try {
            dout.close();
            client.close();
        } catch (Exception e) {
            // cannot close socket
            System.out.println(e);
        }
    }

    public void offline() {
        try {
            dout.flush();
            System.out.println("offline " + getCredentials().getUsername());
            dout.writeUTF("offline " + getCredentials().getUsername());
            String srt = din.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean confirmConnection() {
        try {
            req = new Request();
            client = req.getClientSocket();
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
            return true;
        } catch (Exception e) {
            System.out.println("Error conf connection");
        }
        return false;
    }

    public void sendGroupMessage(String groupName, String message) {
        try {

            dout.flush();
            dout.writeUTF("groupMessage " + groupName + " " + getCredentials().getUsername() + " " + message);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void sendMessage(String user, String message) {
        try {
            System.out.println("updating database for meessage");
            dout.flush();
            dout.writeUTF("update friends " + getCredentials().getUsername() + " " + user + " " + message);
            System.out.println("database updated");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteUnreadMessages() {
        try {
            dout.flush();
            dout.writeUTF("deleteUnreadMessages " + getCredentials().getUsername());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getUnreadMessages() {
        String ans = "";
        try {
            dout.flush();
            dout.writeUTF("unreadmessages " + getCredentials().getUsername());
            ans = din.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ans;
    }

    public void close() {
        try {
            System.out.println("closing server side socket");
            dout.flush();
            dout.writeUTF("close");
            System.out.println("closing client side socket");
            closeSocket();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String login(String name) {
        try {
            dout.flush();
            dout.writeUTF("login " + "user " + name);
            return din.readUTF();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public String fetchGroups() {
        try {
            dout.flush();
            dout.writeUTF("fetch groups");
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            return din.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String fetchUsers() {
        try {
            System.out.println("fetch start");
            dout.flush();
            dout.writeUTF("fetch user");
            return din.readUTF();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String fetchFriends() {
        try {
            dout.flush();
            dout.writeUTF("friends " + getCredentials().getUsername());
            return din.readUTF();
        } catch (Exception e) {
            System.out.println("friends exception");
        }
        return null;
    }

    public String toString() {
        return getCredentials().getUsername() + " " + getCredentials().getEmail() + " " + getCredentials().getPassword() + " "
                + getCredentials().getProfileImage();
    }

    public User getInfo(String username) {
        try {
            dout.flush();
            dout.writeUTF("info user " + username);
            String str = din.readUTF();
            System.out.println("search " + str);
            String[] array = str.split(" ");
            return new User(array[0], array[1], array[2], array[3]);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public String fetchGroupMembers(String name) {
        String ans = "";
        try {
            dout.flush();
            dout.writeUTF("groupmembers " + name);
            ans = din.readUTF();
            System.out.println("mem " + ans);
        } catch (Exception e) {
            System.out.println(e);
        }
        return ans;
    }

    public void updatePassword(String pass) {
        try {
            account.setPassword(pass);
            dout.flush();
            dout.writeUTF("password " + account.getUsername() + " " + pass);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public boolean sendRequest(String username) {
        try {
            dout.flush();
            dout.writeUTF("request " + getCredentials().getUsername() + " " + username + " ");
            String str = din.readUTF();
            System.out.println("request status " + str);
            if (str.equals(true)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    @Override
    public void update(String groupName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
