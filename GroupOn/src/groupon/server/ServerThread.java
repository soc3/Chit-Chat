/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.server;

import groupon.ObserverPattern.Observer;
import groupon.database.Database;
import groupon.database.JsonParsing;
import groupon.user.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import groupon.ObserverPattern.Subject;
import groupon.user.GraphUse;
import groupon.user.UserNetwork;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author sushant oberoi
 */
class ServerThread implements Runnable, Subject {

    private ServerSocket server;
    private ArrayList<Observer> observers;
    private volatile String groupName;
    private int port;
    private int imageCount = 0;
    private DatabaseProtocols db;
    private JsonParsing parse;
    private static Map<String, Pair<DataInputStream, DataOutputStream>> map;
    private static Map<String, Pair<DataInputStream, DataOutputStream>> notifyUser;
    private Map<String, Integer> userId;
    private int filesize;
    static int userCount;
    private String path;
    private volatile int flag = 2;
    private ArrayList<String> onlineUsers;
    private ArrayList<DataOutputStream> onlineOut;

    public ServerThread(int port) {
        notifyUser = new HashMap<String, Pair<DataInputStream, DataOutputStream>>();
        UserNetwork.initialize();
        userId = new HashMap<String, Integer>();
        onlineOut = new ArrayList<DataOutputStream>();
        observers = new ArrayList<Observer>();
        onlineUsers = new ArrayList<String>();
        map = new HashMap<String, Pair<DataInputStream, DataOutputStream>>();
        this.port = port;
        db = new Database();
        parse = new JsonParsing();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            while (!Server.isStopped) {
                if (flag == 0) {
                    continue;
                }
                flag = 0;
                Thread th;
                th = new Thread(new Runnable() {
                    private Socket clientSocket;
                    private Socket chatSocket;
                    private Socket workSocket;

                    private DataInputStream din, chatIn, workIn;
                    private DataOutputStream dout, chatOut, workOut;

                    @Override
                    public void run() {
                        try {
                            System.out.println("listen1");
                            System.out.println("waiting for client");
                            clientSocket = server.accept();
                            System.out.println("conn1");
                            System.out.println("listen2");
                            chatSocket = server.accept();
                            System.out.println("conn2");

                            din = new DataInputStream(clientSocket.getInputStream());
                            dout = new DataOutputStream(clientSocket.getOutputStream());

                            chatIn = new DataInputStream(chatSocket.getInputStream());
                            chatOut = new DataOutputStream(chatSocket.getOutputStream());

                            chatOut.flush();
                            chatOut.writeUTF("accepted");
                            System.out.println("accepted");
                            String name = chatIn.readUTF();
                            System.out.println("registering " + name);
                            map.put(name, new Pair(chatIn, chatOut));
                            System.out.println("listen3");
                            workSocket = server.accept();
                            userCount++;
                            if (!userId.containsKey(name)) {
                                userId.put(name, userCount);
                            }
                            flag = 1;
                            System.out.println("conn3");
                            onlineUsers.add(name);
                            String users = "";
                            for (int i = 0; i < onlineUsers.size(); i++) {
                                users += (onlineUsers.get(i) + " ");
                            }
                            workIn = new DataInputStream(workSocket.getInputStream());
                            workOut = new DataOutputStream(workSocket.getOutputStream());
                            notifyUser.put(name, new Pair(workIn, workOut));
                            onlineOut.add(workOut);
                            for (int i = 0; i < onlineOut.size(); i++) {
                                onlineOut.get(i).flush();
                                onlineOut.get(i).writeUTF("useradd " + users);
                            }
                            listen();
                        } catch (IOException ex) {
                        }
                    }

                    private synchronized void listen() throws IOException {

                        while (true) {
                            try {
                                System.out.println("reading 2");
                                //System.out.println(din.available());

                                String request = din.readUTF();
                                System.out.println("request from client");
                                System.out.println(request);
                                if (request.startsWith("insert")) {
                                    System.out.println("insert request");
                                    insertInDB(request);
                                } else if (request.startsWith("userid ")) {
                                    String[] array = request.split(" ");
                                    dout.flush();
                                    dout.writeUTF(Integer.toString(userId.get(array[1])));
                                } else if (request.startsWith("fetch groups")) {
                                    dout.flush();
                                    dout.writeUTF(db.fetchGroups());
                                } else if (request.startsWith("idname ")) {
                                    String[] array = request.split(" ");
                                    int myId = Integer.parseInt(array[1]);
                                    for (Map.Entry<String, Integer> e : userId.entrySet()) {
                                        String key = e.getKey();
                                        Integer value = e.getValue();
                                        if (value.equals(myId)) {
                                            dout.flush();
                                            dout.writeUTF(key);
                                            break;
                                        }
                                    }
                                } else if (request.startsWith("imagesend")) {
                                    recieveImage(request.split(" ")[1]);
                                } else if (request.startsWith("close")) {
                                    closeSocket();
                                    break;
                                } else if (request.startsWith("getgrouprequests")) {
                                    String[] array = request.split(" ");
                                    dout.writeUTF(db.getGroupRequests(array[1]));
                                } else if (request.startsWith("grouprequest")) {
                                    String[] array = request.split(" ");
                                    db.sendGroupRequest(array[3], array[1], array[2], (short) 0);
                                    DataOutputStream outSocket = notifyUser.get(array[2]).getValue();
                                    outSocket.writeUTF("groupinvite " + array[1]);
                                } else if (request.startsWith("groups")) {
                                    String[] array = request.split(" ");
                                    System.out.println(array[1]);
                                    dout.writeUTF(db.fetchGroups(array[1]));
                                } else if (request.startsWith("login")) {
                                    login(request);
                                } else if (request.startsWith("fetch")) {
                                    String[] array = request.split(" ");
                                    System.out.println(array[1]);
                                    System.out.println("table " + array[1]);
                                    String send = db.fetch(array[1]);
                                    System.out.println("send " + send);
                                    dout.flush();
                                    dout.writeUTF(send);
                                } else if (request.startsWith("info")) {
                                    String[] array = request.split(" ");
                                    String str = db.getUserInfo(array[2]);
                                    System.out.println(str);
                                    dout.flush();
                                    dout.writeUTF(str);
                                } else if (request.startsWith("getPrioMessages")) {
                                    String[] array = request.split(" ");
                                    dout.flush();
                                    dout.writeUTF(db.getPriorityMessages(array[1]));
                                } else if (request.startsWith("priority")) {
                                    String[] array = request.split(" ");
                                    String msg = "";
                                    for (int i = 3; i < array.length; i++) {
                                        msg += (array[i]);
                                    }
                                    db.addToPriority(array[1], array[2], msg);
                                } else if (request.startsWith("request")) {
                                    String[] array = request.split(" ");
                                    boolean status = db.insert(array[1], array[2], (short) 0, null);
                                    dout.flush();
                                    String str;
                                    if (status) {
                                        str = "true";
                                    } else {
                                        str = "false";
                                    }
                                    dout.writeUTF(str);
                                } else if (request.startsWith("password")) {
                                    String[] array = request.split(" ");
                                    db.update("user", array[1], array[2]);
                                } else if (request.startsWith("addgroup")) {
                                    String[] array = request.split(" ");
                                    db.addGroup("groups", array[1], array[2]);
                                } else if (request.startsWith("groupmembers")) {
                                    System.out.println("server side");
                                    String[] array = request.split(" ");
                                    String ans = db.fetchGroupMembers(array[1]);
                                    System.out.println(ans);
                                    dout.writeUTF(ans);
                                } else if (request.startsWith("groupMessage")) {
                                    String[] array = request.split(" ");
                                    int len = array.length;
                                    String message = "";
                                    for (int i = 3; i < len; i++) {
                                        message += (array[i] + " ");
                                    }
                                    db.updateGroupMessages(array[1], array[2], message);
                                    String members = db.fetchGroupMembers(array[1]);
                                    String[] array2 = members.split(" ");
                                    len = array2.length;
                                    for (int i = 0; i < len; i++) {
                                        System.out.println("group member " + array2[i]);
                                        DataOutputStream chatOut2 = null;
                                        System.out.println("start");
                                        String name = "group@";
                                        for (Map.Entry<String, Pair<DataInputStream, DataOutputStream>> entry : map.entrySet()) {
                                            System.out.println(entry.getKey() + "//" + entry.getValue().getKey() + "/" + entry.getValue().getValue());
                                            if (entry.getKey().equals(array2[i]) && (!entry.getKey().equals(array[2]))) {
                                                chatOut2 = entry.getValue().getValue();
                                                name += array2[i];
                                                System.out.println("chatting with: " + chatOut2);
                                                break;
                                            }
                                        }
                                        System.out.println("hey " + chatOut2);
                                        if (chatOut2 != null) {
                                            chatOut2.writeUTF("group@"+message + "#"+array[1] +"#"+array[2]);
                                        } else {
                                            db.updateUnreadMessages(name, message);
                                        }
                                        
                                    }
                                } else if (request.startsWith("deleteUnreadMessages")) {
                                    String name = request.split(" ")[1];
                                    db.deleteUnreadMessages(name);
                                } else if (request.startsWith("unreadmessages")) {
                                    String[] array = request.split(" ");
                                    dout.flush();
                                    dout.writeUTF(db.fetchUnreadMessages(array[1]));
                                } else if (request.startsWith("offline")) {
                                    System.out.println("request tt " + request);
                                    String[] array = request.split(" ");
                                    for (int i = 0; i < onlineOut.size(); i++) {
                                        System.out.println("offline server " + array[1]);
                                        onlineOut.get(i).flush();
                                        onlineOut.get(i).writeUTF("userdel " + array[1]);
                                    }
                                    dout.flush();
                                    dout.writeUTF("xbxnx");
                                } else if (request.startsWith("getrecomm")) {

                                    String user = request.split(" ")[1];
                                    System.out.println(user);

                                    String ansmy = dfs(user);
                                    dout.flush();
                                    dout.writeUTF(ansmy);
                                } else if (request.startsWith("sendprofile")) {
                                    String username = request.split(" ")[1];
                                    sendImage(username);
                                } else if (request.startsWith("friend")) {
                                    String array[] = request.split(" ");
                                    String str = db.fetchFriends("friends", array[1]);
                                    dout.flush();
                                    dout.writeUTF(str);
                                } else if (request.startsWith("acceptgrouprequest")) {
                                    try {
                                        String[] array = request.split(" ");
                                        db.acceptGroupRequest(array[1], array[2], array[3], Integer.parseInt(array[4]));
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                } else if (request.startsWith("update")) {
                                    String array[] = request.split(" ");
                                    short type;
                                    array[4].trim();
                                    System.out.println("type " + array[4]);
                                    if (array[4].equals("0")) {
                                        type = 0;
                                        System.out.println(array[1] + " " + array[2] + " " + array[3] + " " + type);
                                        db.update(array[1], array[2], array[3], type);
                                        db.update(array[1], array[3], array[2], type);
                                    } else if (array[4].equals("1")) {
                                        type = 1;
                                        System.out.println("11111");
                                        System.out.println(array[1] + " " + array[2] + " " + array[3] + " " + type);
                                        db.update(array[1], array[2], array[3], type);
                                        db.update(array[1], array[3], array[2], type);
                                    } else {
                                        System.out.println("emmemee");
                                        int len = array.length;
                                        String msg = "";

                                        int i = 4;
                                        while (i < len) {
                                            msg += array[i];
                                            msg += " ";
                                            i++;
                                        }
                                        System.out.println("recive " + msg);
                                        System.out.println("check " + array[1] + " " + array[2] + " " + array[3] + " " + msg);
                                        System.out.println("message " + msg);
                                        db.update(array[1], array[2], array[3], msg);
                                        System.out.println("sender " + array[2]);
                                        array[3].trim();
                                        DataOutputStream chatOut2 = null;
                                        System.out.println("start");
                                        System.out.println("message " + msg);
                                        String sender = array[2];
                                        for (Map.Entry<String, Pair<DataInputStream, DataOutputStream>> entry : map.entrySet()) {
                                            System.out.println(entry.getKey() + "//" + entry.getValue().getKey() + "/" + entry.getValue().getValue());
                                            if (entry.getKey().equals(array[3])) {
                                                chatOut2 = entry.getValue().getValue();
                                                System.out.println("chatting with: " + chatOut2);
                                            }
                                        }
                                        System.out.println("message " + msg);
                                        System.out.println("hey " + chatOut2);

                                        if (chatOut2 != null) {
                                            System.out.println("message " + msg);
                                            msg += ("#" + sender);
                                            chatOut2.writeUTF(msg); // colon changed
                                        } else {
                                            System.out.println(array[3] + " " + msg);
                                            msg += ("#" + sender);
                                            db.updateUnreadMessages(array[3], msg);
                                        }
                                    }
                                }
                                System.out.println("ended2");

                            } catch (Exception ex) {
                                System.out.println(ex + "chatout2");
                            }
                        }
                    }

                    public void sendImage(String username) throws IOException {
                        File file = new File("H:\\softablitzProject\\server\\Userimages\\"+username+".jpg");
                        System.out.println("H:\\softablitzProject\\server\\Userimages\\"+username+".jpg");
                        // Get the size of the file
                        long length = file.length();
                        byte[] bytes = new byte[16 * 1024];
                        InputStream in = new FileInputStream(file);
                        dout.flush();
                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            System.out.println("count " + count);
                            dout.flush();
                            dout.write(bytes, 0, count);
                            if (count < 1000) {
                                break;
                            }
                        }
                        System.out.println("ended2222");
                        in.close();
                    }

                    String answerf = "";
                    Map<String, Integer> visitedf;

                    private void dfs2(String username) {
                        visitedf.put(username, 1);
                        answerf += (username + " ");
                        String friends = db.fetchFriends("friends", username);
                        String[] array = friends.split(" ");
                        for (int i = 0; i < array.length; i++) {
                            if (i % 3 == 0 && !visitedf.containsKey(array[i])) {
                                dfs2(array[i]);
                            }
                        }
                    }

                    private String dfs(String username) {
                        visitedf = new HashMap<String, Integer>();
                        answerf = "";
                        String anshain = username + " ";
                        String fet = db.fetchFriends("friends", username);
                        String[] array = fet.split(" ");
                        for (int i = 0; i < array.length; i++) {
                            if (i % 3 == 0) {
                                anshain += (array[i] + " ");
                            }
                        }
                        dfs2(username);
                        answerf = modify(answerf, anshain);
                        return answerf;
                    }

                    private String modify(String total, String part) {
                        String[] array = total.split(" ");
                        String ans = "";
                        for (int i = 0; i < array.length; i++) {
                            if (!part.contains(array[i])) {
                                ans += (array[i] + " ");
                            }
                        }
                        return ans;
                    }

                    private void login(String request) {
                        try {
                            String words[] = request.split(" ");
                            System.out.println(words[1] + words[2]);
                            String rst = db.AlreadyExists(words[1], words[2]);
                            System.out.println("rst " + rst);
                            dout.flush();
                            if (rst == null) {
                                dout.writeUTF(null);
                            } else {
                                dout.writeUTF(rst);
                            }
                        } catch (Exception e) {

                        }
                    }

                    private void recieveImage(String username) {
                        try {
                            //imageCount++;
                            path = "H:\\softablitzProject\\server\\Userimages\\" + username + ".jpg";

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
                                if (count < 1000) {
                                    break;
                                }
                            }
                            System.out.println("image ended");
                            out.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    private void closeSocket() {
                        try {
                            chatIn.close();
                            chatOut.close();
                            chatSocket.close();
                            din.close();
                            dout.close();
                            clientSocket.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    private void insertInDB(String request) {
                        try {
                            System.out.println("inserting server");
                            String words[] = request.split(" ");
                            User user = new User(words[2], words[3], words[4], path);
                            boolean flag = db.insert(words[1], user);
                            if (!flag) {
                                dout.flush();
                                dout.writeUTF("false");
                            } else {
                                dout.flush();
                                dout.writeUTF("accepted");
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                });
                th.start();
            }
            server.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void register(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        int observerIdx = observers.indexOf(o);

        System.out.println("Observer " + (observerIdx + 1) + " deleted");
        observers.remove(o);
    }

    @Override
    public void notifyObserver() {
        for (Observer o : observers) {
            o.update(groupName);
        }
    }

    public void setGroupName(String name) {
        this.groupName = name;
        notifyObserver();
    }

}
