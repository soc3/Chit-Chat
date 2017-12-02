/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.user;

import groupon.ObserverPattern.Observer;
import groupon.chat.ChatInterface;
import groupon.database.JsonParsing;
import groupon.jtableComponents.ButtonRenderer;
import groupon.server.Request;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.FileUserDictionary;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author sushant oberoi
 */
public class Login extends javax.swing.JFrame implements Observer {

    /**
     * Creates new form Login
     */
    private String imagePath, chatType, mediaPath;
    private int flag = 0; // user closed or not
    private int chatters = 0;
    private JsonParsing parse;
    private User user;
    private ChatInterface doChat;
    private static Login loginObject;
    private DefaultTableModel friendsModel, usersModel;
    private Socket chatSocket, workSocket;
    private DataInputStream chatIn, workIn;
    private DataOutputStream chatOut, workOut;
    private Request req2;
    private RSyntaxTextArea syntaxTextArea;
    private JTextArea textArea;
    private JPanel cp;
    private JButton b1, b2;
    private JComboBox jc;

    public Login() {

        initComponents();

        cp = new JPanel();

        RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea(20, 60);
        syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea = syntaxTextArea;
        b1 = new JButton();
        b2 = new JButton();
        jc = new JComboBox();

        b1.setLocation(800, 820);
        b2.setLocation(840, 860);
        jc.setLocation(880, 900);

        b1.setSize(100, 100);
        b2.setSize(100, 100);
        b1.setText("SEND");
        b2.setText("UPLOAD MEDIA");

        JScrollPane sp = new JScrollPane(textArea);
        cp.add(sp);
        cp.add(b1);
        cp.add(b2);
        cp.add(jc);

        b1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (chatType.equals("friend")) {
                        String message = textArea.getText();
                        String sendTo = chat1.getText();
                        if (message == "") {
                            JOptionPane.showMessageDialog(null, "first enter a message!!");
                            return;
                        }
                        message = formatMessage(user.getCredentials().getUsername(), message);
                        String newMsg = message;
                        System.out.println("new message " + newMsg);
                        message = message1.getText() + "\n" + message;
                        message1.setText(message);
                        doChat.sendMessage(sendTo, newMsg);
                        int cnt = 6000;
                        int i = message.length() - 1;
                        String ans = "";
                        while (i >= 0 && cnt > 0) {
                            ans += message.charAt(i);
                            i--;
                            cnt--;
                        }
                        message = reverseString(message);
                        storeInfile(user.getCredentials().getUsername(), sendTo, message);
                        storeInfile(sendTo, user.getCredentials().getUsername(), message);
                    } else {
                        String groupName = chatType.substring(5);
                        System.out.println("groupname " + groupName);
                        String message = textArea.getText();
                        String sendTo = chat1.getText();
                        if (message == "") {
                            JOptionPane.showMessageDialog(null, "first enter a message!!");
                            return;
                        }
                        message = formatMessage(user.getCredentials().getUsername(), message);
                        String newMsg = message;
                        message = message1.getText() + "\n" + message;
                        message1.setText(message);
                        int cnt = 0;
                        String temp = "";
                        int i = message.length() - 1;
                        while (i >= 0 && cnt < 6000) {
                            temp += message.charAt(i);
                            i--;
                            cnt++;
                        }
                //temp = reverseString(temp);
                        //temp = formatMessage(groupName, temp);
                        System.out.println("temp " + temp);

                        System.out.println("message " + message);
                        doChat.sendGroupMessage(groupName, temp);
                        temp = reverseString(temp);
              //  storeInfile(user.getCredentials().getUsername(), groupName, temp);
                        //  storeInfile(groupName, user.getCredentials().getUsername(), temp);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        });

        b2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser openFile = new JFileChooser();
                openFile.showOpenDialog(null);
                imagePath = null;
                File image = null;
                image = openFile.getSelectedFile();
                if (image == null) {
                    ;
                } else {
                    mediaPath = image.getPath();
                    if (mediaPath.endsWith(".mp3")) {

                    } else if (mediaPath.endsWith(".mp4")) {

                    } else if (mediaPath.endsWith(".pdf")) {

                    }

                }
            }

        });
        String[] curr = {"JAVA", "C", "CSS", "HTML"};
        jc.setModel(new DefaultComboBoxModel<String>(curr));

        jc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                String lang = (String) jc.getSelectedItem();
                if (lang.equals("JAVA")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                } else if (lang.equals("C")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
                } else if (lang.equals("CSHARP")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
                } else if (lang.equals("CSS")) {
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
                }
                textArea = syntaxTextArea;
            }

        });

        // cp.add(new JButton());
        // jFrame1.add(cp);
        jFrame1.setContentPane(cp);
        jFrame1.setTitle("LONG POST TEXT EDITOR");
        //jFrame1.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jFrame1.pack();
        jFrame1.setLocationRelativeTo(null);
        //FILE LOCATION OF DICTIONARY
        String userDictionaryPath = "/dictionary/";

//SET DICTIONARY PROVIDER FROM DICTIONARY PATH
        SpellChecker.setUserDictionaryProvider(new FileUserDictionary(userDictionaryPath));

//REGISTER DICTIONARY
        SpellChecker.registerDictionaries(getClass().getResource(userDictionaryPath), "en");

        SpellChecker.register(msg1);
        SpellChecker.register(msg2);
        SpellChecker.register(message1);
        SpellChecker.register(message2);
        parse = new JsonParsing();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void myFunc(String str[]) {
        cb.setModel(new DefaultComboBoxModel<String>(str));
        AutoCompleteDecorator.decorate(cb);
        users.getTableHeader().setOpaque(false);
        users.getTableHeader().setBackground(Color.DARK_GRAY);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SignUp = new javax.swing.JFrame();
        createAccount = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        signupName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        signupEmail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        signupPassword = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        signupPassword2 = new javax.swing.JPasswordField();
        upload = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        signupImage = new javax.swing.JTextPane();
        jLabel16 = new javax.swing.JLabel();
        Home = new javax.swing.JFrame();
        viewProfile = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        users = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        friendsGroups = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        message1 = new javax.swing.JTextPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        message2 = new javax.swing.JTextPane();
        send1 = new javax.swing.JButton();
        send2 = new javax.swing.JButton();
        chat1 = new javax.swing.JLabel();
        chat2 = new javax.swing.JLabel();
        add1 = new javax.swing.JButton();
        add2 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        msg2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        msg1 = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        prioList = new javax.swing.JComboBox();
        prio = new javax.swing.JLabel();
        groups = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        addGroup = new javax.swing.JButton();
        hey = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        notifications = new javax.swing.JComboBox();
        long1 = new javax.swing.JButton();
        long2 = new javax.swing.JButton();
        cb = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        unreadImage = new javax.swing.JTextPane();
        unreadMsg = new javax.swing.JLabel();
        Profile = new javax.swing.JFrame();
        jLabel1 = new javax.swing.JLabel();
        profileName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        profileEmail = new javax.swing.JTextField();
        profilePassword = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        image = new javax.swing.JTextPane();
        friendRequest = new javax.swing.JButton();
        groupRequest = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        groupsList = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        changePassword = new javax.swing.JTextField();
        ok = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jFrame1 = new javax.swing.JFrame();
        heading = new javax.swing.JLabel();
        label1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        label2 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        signup = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();

        SignUp.setMinimumSize(new java.awt.Dimension(770, 500));
        SignUp.getContentPane().setLayout(null);

        createAccount.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        createAccount.setText("Signup");
        createAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAccountActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(createAccount);
        createAccount.setBounds(320, 430, 100, 40);

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("SIGNUP");
        SignUp.getContentPane().add(jLabel7);
        jLabel7.setBounds(350, 10, 260, 25);

        signupName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupNameActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(signupName);
        signupName.setBounds(250, 70, 230, 30);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("Username : ");
        SignUp.getContentPane().add(jLabel3);
        jLabel3.setBounds(90, 60, 120, 50);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setText("Email : ");
        SignUp.getContentPane().add(jLabel4);
        jLabel4.setBounds(90, 150, 100, 28);

        signupEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupEmailActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(signupEmail);
        signupEmail.setBounds(253, 147, 230, 30);

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel5.setText("Password : ");
        SignUp.getContentPane().add(jLabel5);
        jLabel5.setBounds(90, 230, 130, 40);

        signupPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupPasswordActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(signupPassword);
        signupPassword.setBounds(260, 230, 230, 30);

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setText("Confirm Password : ");
        SignUp.getContentPane().add(jLabel6);
        jLabel6.setBounds(91, 323, 160, 50);

        signupPassword2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupPassword2ActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(signupPassword2);
        signupPassword2.setBounds(260, 330, 230, 30);

        upload.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        upload.setText("Upload Photo");
        upload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadActionPerformed(evt);
            }
        });
        SignUp.getContentPane().add(upload);
        upload.setBounds(620, 370, 120, 32);

        signupImage.setEditable(false);
        jScrollPane2.setViewportView(signupImage);

        SignUp.getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(560, 70, 230, 295);

        jLabel16.setIcon(new javax.swing.ImageIcon("H:\\softablitzProject\\back.jpg")); // NOI18N
        jLabel16.setMaximumSize(new java.awt.Dimension(820, 500));
        SignUp.getContentPane().add(jLabel16);
        jLabel16.setBounds(0, 0, 820, 500);

        Home.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Home.setMinimumSize(new java.awt.Dimension(2147483647, 2147483647));
        Home.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                HomeWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                HomeWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                HomeWindowOpened(evt);
            }
        });

        viewProfile.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        viewProfile.setText("Profile");
        viewProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewProfileActionPerformed(evt);
            }
        });

        users.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        users.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Recommended Users"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane6.setViewportView(users);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 255));
        jLabel11.setText("Friends");

        friendsGroups.setBackground(new java.awt.Color(0, 153, 153));
        friendsGroups.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 0, 51)));
        friendsGroups.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        friendsGroups.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Friend/Group", "Friend/Group", "Request"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        friendsGroups.setGridColor(new java.awt.Color(51, 51, 255));
        friendsGroups.setSelectionForeground(new java.awt.Color(204, 255, 0));
        jScrollPane4.setViewportView(friendsGroups);

        jPanel1.setLayout(null);

        message1.setEditable(false);
        jScrollPane5.setViewportView(message1);

        jPanel1.add(jScrollPane5);
        jScrollPane5.setBounds(359, 58, 230, 205);

        message2.setEditable(false);
        jScrollPane7.setViewportView(message2);

        jPanel1.add(jScrollPane7);
        jScrollPane7.setBounds(129, 58, 212, 205);

        send1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        send1.setText("send");
        send1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send1ActionPerformed(evt);
            }
        });
        jPanel1.add(send1);
        send1.setBounds(360, 460, 70, 30);

        send2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        send2.setText("Send");
        send2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send2ActionPerformed(evt);
            }
        });
        jPanel1.add(send2);
        send2.setBounds(130, 460, 70, 30);

        chat1.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        chat1.setForeground(new java.awt.Color(255, 0, 0));
        chat1.setText("fsalfsa");
        jPanel1.add(chat1);
        chat1.setBounds(370, 20, 90, 17);

        chat2.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        chat2.setForeground(new java.awt.Color(204, 0, 0));
        chat2.setText("sahflkfsa");
        jPanel1.add(chat2);
        chat2.setBounds(130, 20, 90, 20);

        add1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        add1.setText("Add To priority");
        add1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add1ActionPerformed(evt);
            }
        });
        jPanel1.add(add1);
        add1.setBounds(430, 460, 160, 30);

        add2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        add2.setText("Add to priority");
        add2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add2ActionPerformed(evt);
            }
        });
        jPanel1.add(add2);
        add2.setBounds(200, 460, 140, 30);

        msg2.setColumns(20);
        msg2.setRows(5);
        jScrollPane8.setViewportView(msg2);

        jPanel1.add(jScrollPane8);
        jScrollPane8.setBounds(129, 281, 212, 168);

        msg1.setColumns(20);
        msg1.setRows(5);
        jScrollPane3.setViewportView(msg1);

        jPanel1.add(jScrollPane3);
        jScrollPane3.setBounds(360, 280, 230, 170);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/back.jpg"))); // NOI18N
        jPanel1.add(jLabel18);
        jLabel18.setBounds(100, 0, 510, 500);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e1.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(0, 50, 90, 70);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e2.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);
        jButton2.setBounds(0, 130, 90, 60);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e3.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3);
        jButton3.setBounds(0, 200, 90, 70);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e4.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton4);
        jButton4.setBounds(0, 280, 90, 70);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e5.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton5);
        jButton5.setBounds(0, 360, 90, 60);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/groupon/user/e6.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton6);
        jButton6.setBounds(0, 430, 90, 70);

        prio.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        prio.setText("Priority Messages");

        groups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupsActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 0, 255));
        jLabel10.setText("My Groups:");

        addGroup.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        addGroup.setText("Add Group");
        addGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupActionPerformed(evt);
            }
        });

        hey.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        hey.setForeground(new java.awt.Color(0, 0, 204));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 0, 255));
        jLabel14.setText("Notifications");

        long1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        long1.setText("LONG POST");
        long1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                long1ActionPerformed(evt);
            }
        });

        long2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        long2.setText("LONG POST");
        long2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                long2ActionPerformed(evt);
            }
        });

        cb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Hi");

        jScrollPane9.setViewportView(unreadImage);

        unreadMsg.setText("jLabel17");

        javax.swing.GroupLayout HomeLayout = new javax.swing.GroupLayout(Home.getContentPane());
        Home.getContentPane().setLayout(HomeLayout);
        HomeLayout.setHorizontalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeLayout.createSequentialGroup()
                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(HomeLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(HomeLayout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(HomeLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(prioList, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(prio))
                                        .addGap(82, 82, 82)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(HomeLayout.createSequentialGroup()
                                        .addGap(25, 25, 25)
                                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(unreadMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(20, 20, 20)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6))
                            .addGroup(HomeLayout.createSequentialGroup()
                                .addComponent(cb, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hey, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groups, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(notifications, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addGroup)
                                .addGap(9, 9, 9))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, HomeLayout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(long2)
                        .addGap(150, 150, 150)
                        .addComponent(long1)))
                .addGap(50, 50, 50))
        );
        HomeLayout.setVerticalGroup(
            HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HomeLayout.createSequentialGroup()
                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HomeLayout.createSequentialGroup()
                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(HomeLayout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(hey, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomeLayout.createSequentialGroup()
                                    .addGap(14, 14, 14)
                                    .addComponent(viewProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(HomeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(groups, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(notifications, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(addGroup))))
                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomeLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(HomeLayout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(prio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(prioList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(unreadMsg))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(HomeLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(HomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(long1)
                    .addComponent(long2))
                .addGap(32, 32, 32))
        );

        Profile.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Profile.setMinimumSize(new java.awt.Dimension(800, 800));
        Profile.getContentPane().setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("Username :");
        Profile.getContentPane().add(jLabel1);
        jLabel1.setBounds(45, 68, 82, 16);

        profileName.setEditable(false);
        profileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileNameActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(profileName);
        profileName.setBounds(220, 65, 100, 30);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 255));
        jLabel2.setText("Email");
        Profile.getContentPane().add(jLabel2);
        jLabel2.setBounds(45, 116, 82, 16);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 255));
        jLabel8.setText("Password");
        Profile.getContentPane().add(jLabel8);
        jLabel8.setBounds(45, 169, 104, 17);

        profileEmail.setEditable(false);
        Profile.getContentPane().add(profileEmail);
        profileEmail.setBounds(220, 116, 100, 30);

        profilePassword.setEditable(false);
        profilePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilePasswordActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(profilePassword);
        profilePassword.setBounds(220, 169, 100, 30);

        jScrollPane1.setViewportView(image);

        Profile.getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(410, 70, 167, 178);

        friendRequest.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        friendRequest.setText("Send Friend Request");
        friendRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                friendRequestActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(friendRequest);
        friendRequest.setBounds(70, 430, 200, 60);

        groupRequest.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        groupRequest.setText("Send Group Request");
        groupRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupRequestActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(groupRequest);
        groupRequest.setBounds(320, 430, 210, 60);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 51, 51));
        jLabel12.setText("My Groups :");
        Profile.getContentPane().add(jLabel12);
        jLabel12.setBounds(45, 352, 90, 17);

        groupsList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupsListActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(groupsList);
        groupsList.setBounds(150, 350, 90, 20);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 255));
        jLabel13.setText("New password");
        Profile.getContentPane().add(jLabel13);
        jLabel13.setBounds(33, 222, 130, 17);
        Profile.getContentPane().add(changePassword);
        changePassword.setBounds(220, 219, 100, 30);

        ok.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ok.setText("Change");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });
        Profile.getContentPane().add(ok);
        ok.setBounds(150, 270, 80, 40);

        jLabel19.setIcon(new javax.swing.ImageIcon("H:\\softablitzProject\\back.jpg")); // NOI18N
        Profile.getContentPane().add(jLabel19);
        jLabel19.setBounds(0, 0, 600, 500);

        jFrame1.setMinimumSize(new java.awt.Dimension(1000, 1000));

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(520, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        heading.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        heading.setForeground(new java.awt.Color(51, 0, 153));
        heading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        heading.setText(" GROUPON MESSAGING SOFTWARE");
        heading.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 0, 0)));
        getContentPane().add(heading);
        heading.setBounds(62, 20, 385, 24);

        label1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label1.setText("Username :");
        getContentPane().add(label1);
        label1.setBounds(70, 90, 90, 20);

        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });
        getContentPane().add(username);
        username.setBounds(190, 80, 130, 30);

        label2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        label2.setText("Password :");
        getContentPane().add(label2);
        label2.setBounds(64, 130, 80, 30);
        getContentPane().add(password);
        password.setBounds(190, 130, 130, 30);

        login.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        login.setText("LOGIN");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });
        getContentPane().add(login);
        login.setBounds(120, 180, 70, 40);

        signup.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        signup.setText("SIGNUP");
        signup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupActionPerformed(evt);
            }
        });
        getContentPane().add(signup);
        signup.setBounds(210, 180, 80, 40);

        jLabel15.setIcon(new javax.swing.ImageIcon("H:\\softablitzProject\\back.jpg")); // NOI18N
        jLabel15.setAutoscrolls(true);
        jLabel15.setBorder(new javax.swing.border.MatteBorder(null));
        jLabel15.setMaximumSize(new java.awt.Dimension(700, 600));
        jLabel15.setMinimumSize(new java.awt.Dimension(700, 600));
        jLabel15.setPreferredSize(new java.awt.Dimension(700, 600));
        getContentPane().add(jLabel15);
        jLabel15.setBounds(0, 0, 520, 300);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_usernameActionPerformed


    private void createAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccountActionPerformed
        // TODO add your handling code here:
        String name = signupName.getText();
        String pssd = signupPassword.getText();
        String email = signupEmail.getText();
        String password2 = signupPassword2.getText();
        System.out.println("creating account");
        System.out.println(name + " " + pssd + " " + email + " " + password2);
        if (name.length() == 0 || pssd.length() == 0 || email.length() == 0 || password2.length() == 0) {
            JOptionPane.showMessageDialog(null, "Enter All Details");
            return;
        }
        if (!pssd.equals(password2)) {
            JOptionPane.showMessageDialog(null, "Password doesn't match");
            return;
        }
        user = new User(name, email, pssd, imagePath);
        doChat = user;
        System.out.println(imagePath);
        try {
            boolean connected = user.confirmConnection();
            chatOn();
            listen();
            workSocket = req2.getClientSocket();
            // user.fillGroups();
            if (!connected) {
                JOptionPane.showMessageDialog(null, "Could not connect to server!!");
                return;
            } else {
                System.out.println("client connected");
            }
            user.insertInDB("user", user);
            try {
                flag = 1;
                closeSocket();
                user.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot create account");
        }
    }//GEN-LAST:event_createAccountActionPerformed
    private void chatOn() {
        try {
            req2 = new Request();
            chatSocket = req2.getClientSocket();
            chatIn = new DataInputStream(chatSocket.getInputStream());
            chatOut = new DataOutputStream(chatSocket.getOutputStream());

            if (chatIn.readUTF().equals("accepted")) {
                System.out.println("connected");
                chatOut.flush();
                String register = user.getCredentials().getUsername();
                System.out.println(register);
                chatOut.writeUTF(register);
            }
        } catch (Exception e) {
            System.out.println(e + "chaton");
        }

    }
    private void signupNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signupNameActionPerformed

    private void signupEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signupEmailActionPerformed

    private void signupPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signupPasswordActionPerformed

    private void signupPassword2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupPassword2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signupPassword2ActionPerformed

    private void signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupActionPerformed
        // TODO add your handling code here:
        signupImage.insertIcon(new ImageIcon("H:\\softablitzProject\\GroupOn\\src\\images\\cms.png"));
        SignUp.setVisible(true);
    }//GEN-LAST:event_signupActionPerformed

    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    /**
     *
     * @author sushant oberoi
     */
    public void listen() {
        System.out.println(user.getCredentials().getUsername() + " listening for chat");
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (flag == 1) {
                            break;
                        }
                        String str = chatIn.readUTF();

                        System.out.println("got a message " + str);
                        System.out.println("end");
                        String message23 = "";

                        if (str.startsWith("group@")) {
                            str = str.substring(6);
                            message23 = str.split("#")[0];
                            System.out.println("str " + str);
                            String username = str.split("#")[2];  // groupname
                            String groupname = str.split("#")[1];

                            System.out.println("uss22 " + username + message23);
                            if (chat1.getText().equals(username)) {
                                message1.setText(reverseString(message23 + "\n"));
                            } else if (chat2.getText().equals(username)) {
                                message2.setText(reverseString(message23 + "\n"));
                            } else {

                                if (chatters == 0) {
                                    send1.setVisible(true);
                                    chat1.setText(username);
                                    chat1.setVisible(true);
                                    msg1.setVisible(true);
                                //String str2 = fetchFile(user.getCredentials().getUsername(), username);
                                    // System.out.println("str2 " + str2);
                                    //System.out.println("str " + str);
                                    message1.setText(reverseString(message23 + "\n"));   // display chat
                                    chatType = "group";
                                    add1.setVisible(true);
                                    message1.setVisible(true);
                                    chatters = 1;
                                } else if (chatters == 1) {
                                    send2.setVisible(true);
                                    chat2.setText(username);
                                    chat2.setVisible(true);
                                    // String str2 = fetchFile(user.getCredentials().getUsername(), username);
                                    message2.setText(reverseString(message23 + "\n"));
                                    msg2.setVisible(true);
                                    chatType = "group";
                                    add2.setVisible(true);
                                    message2.setVisible(true);
                                    chatters = 0;
                                }
                            }
                            continue;
                        }
                        /*String[] array = str.split(":");
                         String username = array[0];
                         str = "";
                         for (int i = 1; i < array.length; i++) {
                         str += (array[i] + ": ");
                         }
                         username.trim();
                         System.out.println("chatting with: " + chat1.getText());
                         System.out.println(username);*/

                        String[] array = str.split("#");
                        str = array[0];
                        String username = array[1].trim();
                        System.out.println("dlasd " + str + " " + username);
                        if (chat1.getText().equals(username)) {
                            message1.setText(message1.getText() + "\n" + str);
                            storeInfile(user.getCredentials().getUsername(), chat1.getText(), str);
                            storeInfile(chat1.getText(), user.getCredentials().getUsername(), str);
                        } else if (chat2.getText().equals(username)) {
                            message2.setText(message2.getText() + "\n" + str);
                            storeInfile(chat2.getText(), user.getCredentials().getUsername(), str);
                            storeInfile(user.getCredentials().getUsername(), chat2.getText(), str);
                        } else {
                            if (chatters == 0) {
                                send1.setVisible(true);
                                chat1.setText(username);
                                chat1.setVisible(true);
                                msg1.setVisible(true);
                                String str2 = fetchFile(user.getCredentials().getUsername(), username);
                                System.out.println("str2 " + str2);
                                System.out.println("str " + str);
                                message1.setText(reverseString(str2 + "\n"));   // display chat
                                chatType = "friend";
                                add1.setVisible(true);
                                message1.setVisible(true);
                                chatters = 1;
                            } else if (chatters == 1) {
                                send2.setVisible(true);
                                chat2.setText(username);
                                chat2.setVisible(true);
                                String str2 = fetchFile(user.getCredentials().getUsername(), username);
                                message2.setText(reverseString(str2 + "\n"));
                                msg2.setVisible(true);
                                chatType = "friend";
                                add2.setVisible(true);
                                message2.setVisible(true);
                                chatters = 0;
                            }
                        }

                    } catch (IOException ex) {
                        System.out.println("Jabardasti Ka error");
                    }
                }
            }
        }).start();

    }

    @Override
    public void update(String groupName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class ButtonEditor extends DefaultCellEditor {

        protected JButton button;

        private String label;

        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                String username = label;
                User currRow = user.getInfo(username);
                //JOptionPane.showMessageDialog(button, label + ": Ouch!");

                JFrame frame = Profile;
                Component[] cmp = frame.getContentPane().getComponents();
                int count = 0, btn = 0;
                for (Component node : cmp) {
                    if (node == null) {
                        continue;
                    }
                    if (node instanceof JTextField) {
                        JTextField txt = (JTextField) node;
                        if (count == 0) {
                            System.out.println(count + " " + txt.getText());
                            txt.setText(currRow.getCredentials().getUsername());
                        } else if (count == 1) {
                            System.out.println(count + " " + txt.getText());
                            txt.setText(currRow.getCredentials().getEmail());
                        } else {
                            System.out.println(count + " " + txt.getText());
                            txt.setText("");
                        }
                        count += 1;
                    } else if (node instanceof JButton) {
                        JButton button = (JButton) node;
                        button.setVisible(true);
                        if (btn == 0) {

                            System.out.println(btn + " " + button.getText());
                            button.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    doChat.sendRequest(currRow.getCredentials().getUsername());
                                    JOptionPane.showMessageDialog(null, "request sent!!");
                                }

                            });
                            // upload button
                        } else if (btn == 2) {
                            button.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    System.out.println("heyyeye");

                                    String name = JOptionPane.showInputDialog("Enter Group Name");
                                    if (name == null) {
                                        return;
                                    }
                                    user.sendGroupRequest(username, name);
                                    JOptionPane.showMessageDialog(null, "Request sent!!");
                                }

                            });
                            System.out.println(btn + " " + button.getText());

                            // send request
                        } else if (btn == 1) {
                            System.out.println(btn + " " + button.getText());
                            button.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    doChat.sendRequest(currRow.getCredentials().getUsername());
                                    JOptionPane.showMessageDialog(null, "request sent!!");
                                }

                            });

                        }
                        btn += 1;
                    } else if (node instanceof JComboBox) {
                        /*JComboBox list = (JComboBox)node;
                         String recieve = user.fetchOwners(currRow.getCredentials().getUsername());
                         System.out.println("owners " + recieve);
                         String[] array = recieve.split(" ");
                         int len = array.length;
                         for(int i=0 ; i < len ; i++){
                         list.addItem(array[i]);
                         }*/
                    }
                }
                frame.setVisible(true);
                // System.out.println(label + ": Ouch!");
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void closeSocket() {
        try {

            chatIn.close();
            chatOut.close();
            chatSocket.close();
        } catch (Exception e) {
            // cannot close socket
            System.out.println(e);
        }
    }
    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        // TODO add your handling code here:   

        String name = username.getText();
        String mypassword = password.getText();
        user = new User();
        Credentials account = user.getCredentials();
        hey.setText("Hey " + name);
        account.setUsername(name);
        doChat = user;
        if (name == null || mypassword == null) {
            JOptionPane.showMessageDialog(null, "Enter All Details");
            return;
        }
        try {
            System.out.println(name + mypassword);
            boolean connected = user.confirmConnection();

            if (!connected) {
                JOptionPane.showMessageDialog(null, "Could not connect to server!!");
                return;
            } else {
                System.out.println("client connected");
            }
            chatOn();
            workSocket = req2.getClientSocket();
            listen();
            createDirectory();
            System.out.println("work socket connected");
            String credentials = user.login(name);
            System.out.println(credentials);
            String[] array = credentials.split(" ");
            String passDB = array[0];
            System.out.println(passDB + " " + array[0]);
            if (passDB == null) {
                JOptionPane.showMessageDialog(null, "Kindly Signup first!!");
                return;
            }
            System.out.println(mypassword.equals(passDB));
            if (mypassword.equals(passDB)) {
                account.setUsername(name);
                account.setEmail(array[1]);
                account.setPassword(array[0]);
                account.setProfileImage(array[2]);
                displayFriends();
                System.out.println("displaying users");
                displayUsers();
                showOnlineUsers();
                hideAll();
                Home.setVisible(true);
                user.fillGroups();
                ArrayList<Group> grp = user.getGroups();
                int len = grp.size();
                for (int i = 0; i < len; i++) {
                    System.out.println(grp.get(i).getName());
                    groups.addItem(grp.get(i).getName());
                }
                String rec = user.getPriorityMessages();
                String[] array2 = rec.split("delimiter");
                for (int i = 0; i < array2.length; i++) {
                    prioList.addItem(array2[i]);
                }

                String data = user.fetchUsers();
                data += user.fetchGroups();
                myFunc(data.split(" "));
                String unreadMessages = user.getUnreadMessages();
                System.out.println("len " + unreadMessages.length() + " " + unreadMessages);
                unreadImage.setBackground(Color.LIGHT_GRAY);
                unreadMsg.setText("");
                if (unreadMessages.length() != 0) {
                    unreadImage.insertIcon(new ImageIcon("H:\\softablitzProject\\GroupOn\\src\\images\\unread.png"));
                    unreadMsg.setText("You Have Unread Messages !!");
                    String[] array3 = unreadMessages.split("@");
                    for (int i = 0; i < array3.length; i++) {
                        notifications.addItem(array3[i]);
                        System.out.println(array3[i]);
                    }
                    array3 = unreadMessages.split("#");
//                    System.out.println("array31 " + array3[1]);
                    if (array3.length > 1) {
                        array3 = array3[1].split("@");
                    }
                    System.out.println("user check " + array3[0]);

                    storeInfile(user.getCredentials().getUsername(), array3[0], unreadMessages.split("#")[0]);
                    storeInfile(array3[0], user.getCredentials().getUsername(), unreadMessages.split("#")[0]);
                    user.deleteUnreadMessages();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Password!!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_loginActionPerformed

    public void createDirectory() {
        String str = user.getCredentials().getUsername();
        File theDir = new File("H:\\softablitzProject\\messages\\ " + str);
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }

    private class ColoringCellRenderer extends DefaultTableCellRenderer {

        private final Map<Point, Color> cellColors = new HashMap<Point, Color>();

        void setCellColor(int r, int c, Color color) {
            if (color == null) {
                cellColors.remove(new Point(r, c));
            } else {
                cellColors.put(new Point(r, c), color);
            }
        }

        private Color getCellColor(int r, int c) {
            Color color = cellColors.get(new Point(r, c));
            if (color == null) {
                return Color.WHITE;
            }
            return color;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            Color color = getCellColor(row, column);
            setBackground(color);
            return this;
        }
    }
    ColoringCellRenderer cellRenderer;

    public void showOnlineUsers() {
        cellRenderer = new ColoringCellRenderer();
        TableColumnModel columnModel = friendsGroups.getColumnModel();
        int cc = columnModel.getColumnCount();
        for (int c = 0; c < cc; c++) {
            TableColumn column = columnModel.getColumn(c);
            column.setCellRenderer(cellRenderer);
        }
        try {

            workIn = new DataInputStream(workSocket.getInputStream());
            workOut = new DataOutputStream(workSocket.getOutputStream());
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            // display
                            String req = workIn.readUTF();
                            System.out.println("reee " + req);
                            String[] array = {};
                            if (req.startsWith("useradd")) {
                                array = req.split(" ");
                                for (int i = 1; i < array.length; i++) {
                                    useradd(array[i]);
                                }
                            } else if (req.startsWith("userdel")) {
                                array = req.split(" ");
                                System.out.println("array len " + array.length + " " + array[1]);
                                System.out.println("offline login " + array[1]);
                                userdel(array[1]);
                            } else if (req.startsWith("groupinvite")) {
                                array = req.split(" ");
                                System.out.println("gr inv " + array[1]);
                                JOptionPane.showMessageDialog(null, "new Group invite from user " + array[1]);
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }

            }).start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void useradd(String username) {
        System.out.println("online " + username);
        DefaultTableModel dm = (DefaultTableModel) friendsGroups.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            String str = (String) dm.getValueAt(i, 0);
            if (username.equalsIgnoreCase(str)) {
                cellRenderer.setCellColor(i, 0, new Color(0, 128, 0));
                friendsGroups.repaint();
            }
        }
    }

    public void userdel(String username) {
        DefaultTableModel dm = (DefaultTableModel) friendsGroups.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            String str = (String) dm.getValueAt(i, 0);
            if (username.equalsIgnoreCase(str)) {
                cellRenderer.setCellColor(i, 0, new Color(255, 0, 0));
                friendsGroups.repaint();
            }
        }
    }

    public void displayFriends() {
        try {
            friendsModel = new DefaultTableModel();
            String recieve = user.fetchFriends();
            String[] array = recieve.split(" ");
            String[] array2;
            String grpDisplay = user.getGroupRequests();
            array2 = grpDisplay.split(" ");
            //System.out.println("recieved "+ recieve + " " + grpDisplay);
            //System.out.println("ended");
            int len2 = array2.length;
            int len = array.length, pos = 0, cnt = 0;
            System.out.println("length " + len);
            for (int i = 0; i < len; i++) {
                System.out.println(array[i]);
            }
            System.out.println("end");
            int tmp = Math.max(0, len - 1);
            int sz = (tmp) / 3 + len2 / 3;
            System.out.println("size " + sz);
            Object[][] content = new Object[(len + len2) / 3][3];
            // System.out.println("lengths "+ len + " " + len2);
            // System.out.println("recieves "+ recieve + " " + recieve2);
            while (pos + 2 < len) {
                if (!array[pos].equals(user.getCredentials().getUsername())) {
                    Boolean status = true;
                    System.out.println("status " + array[pos + 2]);
                    array[pos + 2].trim();
                    System.out.println("len " + array[pos + 2].length());
                    if (array[pos + 2].equals("0")) {
                        status = false;
                    } else {
                        status = true;
                    }
                    content[cnt++] = new Object[]{array[pos], array[pos + 1], array[pos] + " " + status};
                }
                pos += 3;
            }
            // for group chat
            pos = 0;
            System.out.println("length2 " + len2);
            while (pos + 2 < len2) {
                Boolean status = true;
                System.out.println("status " + array2[pos + 2]);
                array2[pos + 2].trim();
                System.out.println("len " + array2[pos + 2].length());
                if (array2[pos + 2].equals("0")) {
                    status = false;
                } else {
                    status = true;
                }
                System.out.println(array2[pos] + " " + array2[pos + 1] + " " + status);
                content[cnt++] = new Object[]{array2[pos], array2[pos + 1], status};
                pos += 3;
            }

            /*pos = 0;
             while (pos + 2 < len2) {
             Boolean status = true;
             System.out.println("status " + array2[pos + 2]);
             array2[pos + 2].trim();
             System.out.println("len " + array2[pos + 2].length());
             if (array2[pos + 2].equals("0")) {
             status = false;
             } else {
             status = true;
             }
             content[cnt++] = new Object[]{array2[pos], array2[pos + 1], array2[pos] + " " + status};
             pos += 3;
             }*/
            friendsModel.setDataVector(content, new Object[]{"Friend/Group Name", "Type", "Request"});

            friendsGroups.setModel(friendsModel);
            friendsGroups.getColumn("Friend/Group Name").setCellRenderer(new ButtonRenderer());
            friendsGroups.getColumn("Friend/Group Name").setCellEditor(
                    new ButtonEditorFriends(new JCheckBox()));
            friendsGroups.getColumn("Request").setCellRenderer(new ButtonRenderer());
            friendsGroups.getColumn("Request").setCellEditor(
                    new ButtonEditorRequest(new JCheckBox()));
            friendsGroups.setVisible(true);
        } catch (Exception e) {
            System.out.println("display friends" + e);
        }
    }

    public void hideAll() {
        long1.setVisible(false);
        long2.setVisible(false);
        chat1.setVisible(false);
        chat2.setVisible(false);
        message1.setVisible(false);
        message2.setVisible(false);
        send1.setVisible(false);
        msg1.setVisible(false);
        msg2.setVisible(false);
        send2.setVisible(false);
        add1.setVisible(false);
        add2.setVisible(false);
    }

    public void displayUsers() {
        try {
            String recomm = user.getRecommendedUsers();
            String[] network = recomm.split(" ");
            Object[][] content = new Object[network.length][1];
            int len = network.length;
            int pos = 0, cnt = 0;
            while (pos < len) {
                content[cnt++] = new Object[]{network[pos]};
                System.out.println(network[pos]);
                pos++;
            }
            usersModel = new DefaultTableModel();
            usersModel.setDataVector(content, new Object[]{"Recommended users"});

            users.setModel(usersModel);
            users.getColumn("Recommended users").setCellRenderer(new ButtonRenderer());
            users.getColumn("Recommended users").setCellEditor(
                    new ButtonEditor(new JCheckBox()));
            users.setVisible(true);
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }
    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */

    /**
     *
     * @author sushant oberoi
     */
    /*
     * To change this license header, choose License Headers in Project Properties.
     * To change this template file, choose Tools | Templates
     * and open the template in the editor.
     */
    /**
     *
     * @author sushant oberoi
     */
    private class ButtonEditorRequest extends DefaultCellEditor {

        protected JButton button;

        private String label;
        private JTable table;
        private boolean isPushed;
        int color = 0;
        int row = 0;

        public ButtonEditorRequest(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            this.table = table;
            System.out.println("color " + color);
            if (color == 0) {
                button.setText("true");
                button.setBackground(Color.GREEN);
            } else {
                button.setText("false");
                button.setBackground(Color.RED);
            }
            int flag = 0; // group or user
            label = (value == null) ? "" : value.toString();
            System.out.println("label " + label);
            //button.setText(label);
            String[] array = label.split(" ");
            int len = array.length;
            if (len > 1) {
                flag = 1; // friend
                if (array[1].equals("false")) {
                    color = 1;
                } else {
                    color = 0;
                }
            } else {
                if (array[0].equals("false")) {
                    color = 1;
                } else {
                    color = 0;
                }
            }
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            String[] array = label.split(" ");
            String username = array[0];
            String val = (String) table.getModel().getValueAt(row, 1);
            String name = val.substring(5);
            if (isPushed) {
                unreadMsg.setText("");
                if (color == 0) {
                    System.out.println("updating request");
                    if (flag == 1) {
                        doChat.acceptRequest(username, 0);
                    } else {
                        doChat.acceptGroupRequest(name, (String) table.getModel().getValueAt(row, 0), user.getCredentials().getUsername(), 0);
                    }
                    label = array[0] + " " + "true";
                    JOptionPane.showMessageDialog(null, "request deleted");
                    color = 1;
                } else {
                    System.out.println("updating request");
                    if (flag == 1) {
                        doChat.acceptRequest(username, 1);
                    } else {
                        doChat.acceptGroupRequest(name, (String) table.getModel().getValueAt(row, 0), user.getCredentials().getUsername(), 1);
                    }
                    JOptionPane.showMessageDialog(null, "request accpeted");
                    label = array[0] + "false";
                    color = 0;
                }
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private class ButtonEditorFriends extends DefaultCellEditor {

        protected JButton button;

        private String label;

        private boolean isPushed;

        public ButtonEditorFriends(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            chatType = (String) table.getValueAt(row, 1);
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        int chatters = 0;

        public Object getCellEditorValue() {
            if (isPushed) {
                String username = label;
                System.out.println("label " + label);
                if (chatters == 0) {
                    send1.setVisible(true);
                    chat1.setText(username);
                    chat1.setVisible(true);
                    long1.setVisible(true);
                    msg1.setVisible(true);
                    String chatt = fetchFile(user.getCredentials().getUsername(), username);
                    String str = message1.getText();
                    message1.setText(chatt + "\n" + str);   // display chat
                    add1.setVisible(true);
                    message1.setVisible(true);
                    chatters = 1;
                } else if (chatters == 1) {
                    send2.setVisible(true);
                    chat2.setText(username);
                    long2.setVisible(true);
                    chat2.setVisible(true);
                    String str = message2.getText();
                    String chatt = fetchFile(user.getCredentials().getUsername(), username);
                    message2.setText(chatt + "\n" + str);
                    msg2.setVisible(true);
                    add2.setVisible(true);
                    message2.setVisible(true);

                    chatters = 0;
                }
                // System.out.println(label + ": Ouch!");
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public String fetchFile(String user1, String user2) {

        String str = "";
        Readfile obj = new Readfile();
        String path = user1 + user2 + ".txt";
        String p = "H:\\softablitzProject\\messages\\" + user1 + "\\" + user2 + ".txt";
        try {
            return obj.read(path);
        } catch (Exception ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    private void viewProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewProfileActionPerformed
        // TODO add your handling code here:
        ArrayList<Group> grps = user.getGroups();
        for (int i = 0; i < grps.size(); i++) {
            groupsList.addItem(grps.get(i).getName());
        }
        friendRequest.setVisible(false);
        groupRequest.setVisible(false);
        Profile.setVisible(true);
        Credentials account = user.getCredentials();
        User profile = user.getInfo(account.getUsername());
        account = profile.getCredentials();
        profileName.setText(account.getUsername());
        profilePassword.setText(account.getPassword());
        profileEmail.setText(account.getEmail());
        ArrayList<Group> list = user.getGroups();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            groupsList.addItem(list.get(i));
        }

        user.recieveImage();
        String path = "H:\\softablitzProject\\GroupOn\\Client\\" + user.getCredentials().getUsername() + ".jpg";
        //image.setIcon(new ImageIcon(path));
        image.insertIcon(new ImageIcon(path));


    }//GEN-LAST:event_viewProfileActionPerformed


    private void uploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadActionPerformed
        // TODO add your handling code here:
        JFileChooser openFile = new JFileChooser();
        openFile.showOpenDialog(null);
        imagePath = null;
        File image = null;
        image = openFile.getSelectedFile();
        if (image == null) {
            ;
        } else {
            imagePath = image.getPath();
            signupImage.insertIcon(new ImageIcon(imagePath));
        }
    }//GEN-LAST:event_uploadActionPerformed

    private void profileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_profileNameActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

    }//GEN-LAST:event_formWindowClosed

    private void clearJtable(DefaultTableModel dm) {
        if (dm == null) {
            return;
        }
        int rowCount = dm.getRowCount();
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            dm.removeRow(i);
        }
    }
    private void HomeWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_HomeWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_HomeWindowClosed

    private void HomeWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_HomeWindowClosing
        // TODO add your handling code here:
        flag = 1;
        user.offline();
        closeSocket();
        System.out.println("clearing jtable friends");
        clearJtable(friendsModel);
        System.out.println("clearing jtable users");
        clearJtable(usersModel);
        System.out.println("closing home");

        Home.setVisible(false);
        System.out.println("home closed");
        System.out.println("closing user");

        user.close();
        System.out.println("user closed");
    }//GEN-LAST:event_HomeWindowClosing

    private void HomeWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_HomeWindowOpened
        // TODO add your handling code here:
        // System.out.println("Login window visible false");
/*        String recieve = user.fetchOwners(user.getCredentials().getUsername());
         System.out.println("dsad "+recieve);
         String[] array = recieve.split(" ");
         int len = array.length;
         for(int i=0 ; i<len ; i++){
         groups.addItem(array[i]);
         }*/
    }//GEN-LAST:event_HomeWindowOpened

    private void friendRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_friendRequestActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_friendRequestActionPerformed

    private void send1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send1ActionPerformed
        // TODO add your handling code here:
        System.out.println("chattype " + chatType);
        try {
            if (chatType.equals("friend")) {
                String message = msg1.getText();
                String sendTo = chat1.getText();
                if (message == "") {
                    JOptionPane.showMessageDialog(null, "first enter a message!!");
                    return;
                }
                message = formatMessage(user.getCredentials().getUsername(), message);
                String newMsg = message;
                System.out.println("new message " + newMsg);
                message = message1.getText() + "\n" + message;
                message1.setText(message);
                doChat.sendMessage(sendTo, newMsg);
                int cnt = 6000;
                int i = message.length() - 1;
                String ans = "";
                while (i >= 0 && cnt > 0) {
                    ans += message.charAt(i);
                    i--;
                    cnt--;
                }
                message = reverseString(message);
                storeInfile(user.getCredentials().getUsername(), sendTo, message);
                storeInfile(sendTo, user.getCredentials().getUsername(), message);
            } else {
                String groupName = chatType.substring(5);
                System.out.println("groupname " + groupName);
                String message = msg1.getText();
                String sendTo = chat1.getText();
                if (message == "") {
                    JOptionPane.showMessageDialog(null, "first enter a message!!");
                    return;
                }
                message = formatMessage(user.getCredentials().getUsername(), message);
                String newMsg = message;
                message = message1.getText() + "\n" + message;
                message1.setText(message);
                int cnt = 0;
                String temp = "";
                int i = message.length() - 1;
                while (i >= 0 && cnt < 6000) {
                    temp += message.charAt(i);
                    i--;
                    cnt++;
                }
                //temp = reverseString(temp);
                //temp = formatMessage(groupName, temp);
                System.out.println("temp " + temp);

                System.out.println("message " + message);
                doChat.sendGroupMessage(groupName, temp);
                temp = reverseString(temp);
              //  storeInfile(user.getCredentials().getUsername(), groupName, temp);
                //  storeInfile(groupName, user.getCredentials().getUsername(), temp);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        msg1.setText("");
    }//GEN-LAST:event_send1ActionPerformed

    public static String reverseString(String str) {
        char ch[] = str.toCharArray();
        String rev = "";
        for (int i = ch.length - 1; i >= 0; i--) {
            rev += ch[i];
        }
        return rev;
    }

    private String formatMessage(String user, String message) {
        String ans = "";
        ans += "\n";
        ans += user + ": ";
        ans += message;
        return ans;
    }
    private void add1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add1ActionPerformed
        // TODO add your handling code here:
        String message = msg1.getText();
        if (message == "") {
            JOptionPane.showMessageDialog(null, "first enter a message");
            return;
        }
        message = formatMessage(chat1.getText(), message);
        prioList.addItem(message);
        user.addToPriority(message);
    }//GEN-LAST:event_add1ActionPerformed

    public void storeInfile(String user1, String user2, String message) {
        String path = user1 + user2 + ".txt";
        //  String p = "H:\\softablitzProject\\messages\\" + user1 + "\\" + user2 + ".txt";
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.println(message);
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void add2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add2ActionPerformed
        // TODO add your handling code here:
        String message = msg2.getText();
        if (message == "") {
            JOptionPane.showMessageDialog(null, "first enter a message");
            return;
        }
        message = formatMessage(chat2.getText(), message);
        prioList.addItem(message);
        user.addToPriority(message);
    }//GEN-LAST:event_add2ActionPerformed

    private void send2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send2ActionPerformed
        // TODO add your handling code here:
        try {
            if (chatType.equals("friend")) {
                String message = msg2.getText();
                String sendTo = chat2.getText();
                if (message == "") {
                    JOptionPane.showMessageDialog(null, "first enter a message!!");
                    return;
                }
                message = formatMessage(user.getCredentials().getUsername(), message);
                String newMsg = message;
                System.out.println("new message " + newMsg);
                message = message2.getText() + "\n" + message;
                message2.setText(message);
                doChat.sendMessage(sendTo, newMsg);
                int cnt = 6000;
                int i = message.length() - 1;
                String ans = "";
                while (i >= 0 && cnt > 0) {
                    ans += message.charAt(i);
                    i--;
                    cnt--;
                }
                message = reverseString(message);
                storeInfile(user.getCredentials().getUsername(), sendTo, message);
                storeInfile(sendTo, user.getCredentials().getUsername(), message);
            } else {
                String groupName = chatType.substring(5);
                System.out.println("groupname " + groupName);
                String message = msg2.getText();
                String sendTo = chat2.getText();
                if (message == "") {
                    JOptionPane.showMessageDialog(null, "first enter a message!!");
                    return;
                }
                message = formatMessage(user.getCredentials().getUsername(), message);
                String newMsg = message;
                message = message2.getText() + "\n" + message;
                message2.setText(message);
                int cnt = 0;
                String temp = "";
                int i = message.length() - 1;
                while (i >= 0 && cnt < 6000) {
                    temp += message.charAt(i);
                    i--;
                    cnt++;
                }
                //temp = reverseString(temp);
                //temp = formatMessage(groupName, temp);
                System.out.println("temp " + temp);

                System.out.println("message " + message);
                doChat.sendGroupMessage(groupName, temp);
                temp = reverseString(temp);
            }
        } catch (Exception e) {
        }
        msg2.setText("");
    }//GEN-LAST:event_send2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        flag = 1;
        closeSocket();
    }//GEN-LAST:event_formWindowClosing

    private void addGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupActionPerformed
        // TODO add your handling code here:
        String name = JOptionPane.showInputDialog(null, "Enter group name:");
        if (name == null) {
            return;
        }
        String username = user.getCredentials().getUsername();
        Group grp = new Group(name, username);
        user.addGroup(grp);
        groups.addItem(name);
    }//GEN-LAST:event_addGroupActionPerformed

    private void groupRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupRequestActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_groupRequestActionPerformed

    private void groupsListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupsListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_groupsListActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        // TODO add your handling code here:
        String newPass = changePassword.getText();
        if (newPass == null) {
            JOptionPane.showMessageDialog(null, "Enter updated password");
            return;
        }
        user.updatePassword(newPass);
        JOptionPane.showMessageDialog(null, "password changed");
    }//GEN-LAST:event_okActionPerformed

    private void groupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_groupsActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (msg1.getCaretPosition() >= 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (msg1.getCaretPosition() >= 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if (chatters == 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if (chatters == 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if (chatters == 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if (chatters == 0) {
            msg1.append('\u263A' + "");
        } else {
            msg2.append('\u263A' + "");
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void long1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_long1ActionPerformed
        // TODO add your handling code here:
        jFrame1.setVisible(true);
    }//GEN-LAST:event_long1ActionPerformed

    private void long2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_long2ActionPerformed
        // TODO add your handling code here:
        jFrame1.setVisible(true);
    }//GEN-LAST:event_long2ActionPerformed

    private void profilePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilePasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_profilePasswordActionPerformed

    private void cbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbActionPerformed
        // TODO add your handling code here:
        String username = (String) cb.getSelectedItem();
        if (username.startsWith("group@")) {
            String[] groups = username.split("@");
            String groupname = groups[2];
            String rec = user.fetchGroupMembers(groupname);
            JFrame f = new JFrame();
            DefaultListModel<String> l1 = new DefaultListModel<>();
            l1.addElement("Group Members: -----\n");
            l1.addElement("\n\n");

            String[] array = rec.split(" ");
            for (int i = 0; i < array.length; i++) {
                l1.addElement((i + 1) + ".  " + array[i]);
                System.out.println("Printing : " + array[i]);
            }
            JList<String> list = new JList<>(l1);
            list.setBounds(50, 50, 500, 500);
            list.setToolTipText("Group Memebers");
            list.setFont(new Font("Arial", Font.BOLD, 16));

            list.setBackground(Color.YELLOW);
            list.setSelectionForeground(Color.BLUE);
            f.add(list);
            f.setSize(400, 400);
            f.setLayout(null);
            f.setVisible(true);
            return;
        }
        System.out.println("username fds " + username);
        User currRow = user.getInfo(username);
        //JOptionPane.showMessageDialog(button, label + ": Ouch!");

        JFrame frame = Profile;
        Component[] cmp = frame.getContentPane().getComponents();
        int count = 0, btn = 0;
        for (Component node : cmp) {
            if (node == null) {
                continue;
            }
            if (node instanceof JTextField) {
                JTextField txt = (JTextField) node;
                if (count == 0) {
                    System.out.println(count + " " + txt.getText());
                    txt.setText(currRow.getCredentials().getUsername());
                } else if (count == 1) {
                    System.out.println(count + " " + txt.getText());
                    txt.setText(currRow.getCredentials().getEmail());
                } else {
                    System.out.println(count + " " + txt.getText());
                    txt.setText("");
                }
                count += 1;
            } else if (node instanceof JButton) {
                JButton button = (JButton) node;
                button.setVisible(true);
                if (btn == 0) {

                    System.out.println(btn + " " + button.getText());
                    button.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            doChat.sendRequest(currRow.getCredentials().getUsername());
                            JOptionPane.showMessageDialog(null, "request sent!!");
                        }

                    });
                    // upload button
                } else if (btn == 1) {
                    button.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            System.out.println("heyyeye");

                            String name = JOptionPane.showInputDialog("Enter Group Name");
                            if (name == null) {
                                return;
                            }
                            user.sendGroupRequest(username, name);
                            JOptionPane.showMessageDialog(null, "Request sent!!");
                        }

                    });
                    System.out.println(btn + " " + button.getText());

                    // send request
                } else if (btn == 2) {
                    System.out.println(btn + " " + button.getText());
                    button.setVisible(false);
                }
                btn += 1;
            } else if (node instanceof JComboBox) {
                /*JComboBox list = (JComboBox)node;
                 String recieve = user.fetchOwners(currRow.getCredentials().getUsername());
                 System.out.println("owners " + recieve);
                 String[] array = recieve.split(" ");
                 int len = array.length;
                 for(int i=0 ; i < len ; i++){
                 list.addItem(array[i]);
                 }*/
            } else if (node instanceof JTextPane) {
                JTextPane obj = (JTextPane) node;
                System.out.println("username " + username);
                obj.insertIcon(new ImageIcon("H:\\softablitzProject\\GroupOn\\Client\\" + username + ".jpg"));
            }
        }
        frame.setVisible(true);
    }//GEN-LAST:event_cbActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                loginObject = new Login();
                loginObject.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame Home;
    private javax.swing.JFrame Profile;
    private javax.swing.JFrame SignUp;
    private javax.swing.JButton add1;
    private javax.swing.JButton add2;
    private javax.swing.JButton addGroup;
    private javax.swing.JComboBox cb;
    private javax.swing.JTextField changePassword;
    private javax.swing.JLabel chat1;
    private javax.swing.JLabel chat2;
    private javax.swing.JButton createAccount;
    private javax.swing.JButton friendRequest;
    private javax.swing.JTable friendsGroups;
    private javax.swing.JButton groupRequest;
    private javax.swing.JComboBox groups;
    private javax.swing.JComboBox groupsList;
    private javax.swing.JLabel heading;
    private javax.swing.JLabel hey;
    private javax.swing.JTextPane image;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JButton login;
    private javax.swing.JButton long1;
    private javax.swing.JButton long2;
    private javax.swing.JTextPane message1;
    private javax.swing.JTextPane message2;
    private javax.swing.JTextArea msg1;
    private javax.swing.JTextArea msg2;
    private javax.swing.JComboBox notifications;
    private javax.swing.JButton ok;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel prio;
    private javax.swing.JComboBox prioList;
    private javax.swing.JTextField profileEmail;
    private javax.swing.JTextField profileName;
    private javax.swing.JTextField profilePassword;
    private javax.swing.JButton send1;
    private javax.swing.JButton send2;
    private javax.swing.JButton signup;
    private javax.swing.JTextField signupEmail;
    private javax.swing.JTextPane signupImage;
    private javax.swing.JTextField signupName;
    private javax.swing.JPasswordField signupPassword;
    private javax.swing.JPasswordField signupPassword2;
    private javax.swing.JTextPane unreadImage;
    private javax.swing.JLabel unreadMsg;
    private javax.swing.JButton upload;
    private javax.swing.JTextField username;
    private javax.swing.JTable users;
    private javax.swing.JButton viewProfile;
    // End of variables declaration//GEN-END:variables

}
