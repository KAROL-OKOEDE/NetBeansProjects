/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ultimatechatapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO
 */
public class Client extends javax.swing.JFrame {
        
    
    private boolean theMuted = false;


    boolean attachmentOpen = false;
    String mydownloadfolder = "C:\\";

    String username, command, password, sname, oname, phone, email,path;
    String host;
    register registerform;
    Login loginform;
    int port;
    Socket socket;
    DataOutputStream dos;
    String SendTo = "";
    Thread t;

    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();

    }

    public void verifyLoginDetails(String username, String host, int port, String command, String password, Login loginform) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.command = command;
        this.password = password;
        this.loginform = loginform;
        connect();
    }

    public void connect() {
        System.out.println("Login");
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            /**
             * Send username and password *
             */
            dos.writeUTF("CMD_LOGIN " + username + " " + password);
            // dos.writeUTF(command + username+" "+password);
            System.out.println("Login details sent to sever");

            /**
             * Start Client Thread *
             */
            ClientThread clientThread = new ClientThread(socket, this, loginform);
            Thread thread = new Thread(clientThread);
            thread.start();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(this,
                    "Unable to Connect to Server, please try again later.!",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void RegistrationDetails(String sname, String oname, String email, String phone, String username, String host, int port, String command, String password, String path, register registerform) {
        this.username = username;
        this.sname = sname;
        this.oname = oname;
        this.email = email;
        this.phone = phone;
        this.host = host;
        this.port = port;
        this.command = command;
        this.password = password;
        this.registerform = registerform;
        this.path = path;
        register();
    }

    public void register() {
        System.out.println("Registration");
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            /**
             * Send username and password *
             */
            dos.writeUTF("CMD_REGISTER " + sname + " " + oname + " " + email + " " + phone + " " + username + " " + password + " "+ path);

            System.out.println("Registration  details sent to sever");

            /**
             * Start Client Thread *
             */
            ClientThread clientThread = new ClientThread(socket, this, registerform);
            Thread thread = new Thread(clientThread);
            thread.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to Connect to Server, please try again later.!",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void openFolder() {
        jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = jFileChooser1.showDialog(this, "Select Folder");
        if (open == jFileChooser1.APPROVE_OPTION) {
            mydownloadfolder = jFileChooser1.getSelectedFile().toString() + "\\";
        } else {
            mydownloadfolder = "C:\\";
        }
    }

    public void setMyTitle(String s) {
        setTitle(s);
    }

    public String getMyDownloadFolder() {
        return this.mydownloadfolder;
    }

    public void updateAttachment(boolean b) {
        this.attachmentOpen = b;
    }

    public String getMyUsername() {
        return this.username;
    }

    public void appendMessage(String msg) {
        jTextArea1.append(msg);
    }

    public void appendNotification(String msg) {
        jLabel4.setText("");
        jLabel4.setText(msg);
    }

    public void appendOnlineList(Vector list) {

        OnlineList(list);
    }

    private void OnlineList(Vector list) {
        list2.removeAll();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                Object e = i.next();
                String users = String.valueOf(e);
                list2.add(users);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }

        }
    }

    public String getMyHost() {
        return this.host;
    }

    public int getMyPort() {
        return this.port;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        list1 = new java.awt.List();
        jPanel5 = new javax.swing.JPanel();
        list2 = new java.awt.List();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jAudio = new javax.swing.JButton();
        jVideo = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/minimize-2.png"))); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/x-square.png"))); // NOI18N
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/settings.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("📩", jPanel4);

        list2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list2, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(list2, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("😀", jPanel5);

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane5.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Talking to");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/send.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/mic.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/glyph.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane6.setViewportView(jTextArea2);

        jLabel2.setText("jLabel2");

        jAudio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/phone.png"))); // NOI18N
        jAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAudioActionPerformed(evt);
            }
        });

        jVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/message/video.png"))); // NOI18N
        jVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jVideoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4)))
                        .addContainerGap(30, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jAudio)
                        .addGap(18, 18, 18)
                        .addComponent(jVideo)
                        .addGap(28, 28, 28))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jAudio)
                    .addComponent(jVideo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton3)
                            .addComponent(jButton6)
                            .addComponent(jButton4)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Notification");

        jLabel4.setText("jLabel4");

        jLabel5.setText("jLabel5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked

        System.exit(0);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        setState(ICONIFIED);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            String to = jLabel2.getText();
            /**
             * CMD_CHAT [from] [sendTo] [message] *
             */
            if (!to.equalsIgnoreCase("")) {
                String message = username + " " + to + " " + jTextArea2.getText();
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_CHAT " + message);
                jTextArea1.append("\n" + username + " : " + jTextArea2.getText());
                jTextArea2.setText("");
            } else {
                JOptionPane.showMessageDialog(rootPane, "Select who you want to Chat With");

            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        updatePage up = new updatePage();
        up.init(username);
        up.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        capturingAudio ca = new capturingAudio();
        ca.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (!attachmentOpen) {
            Attachment sendfile = new Attachment();
            if (sendfile.prepare(username, host, port, this)) {
                sendfile.setLocationRelativeTo(null);
                sendfile.setVisible(true);
                attachmentOpen = true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to establish File Sharing at this moment, please try again later.!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void list2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list2MouseClicked
        jTextArea1.setText("");
        SendTo = list2.getSelectedItem();
        jLabel2.setText(SendTo);
//Get All Messages from Database BETWEEN  you and the friend
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF("CMD_GETMESSAGES " + SendTo + " " + username);
        } catch (Exception e) {
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_list2MouseClicked

    private void jAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAudioActionPerformed
      String sendTo = jLabel2.getText();

        if (sendTo.length() > 0)  {
            try {
                // Format: CMD_SEND_FILE_XD [sender] [receiver] [filename]
                jLabel2.setText("");
//                String fname = getThisFilename(file);//
                String format = "CMD_AUDIO_CALL_XD " + username + " " + sendTo;
                dos.writeUTF(format);
                System.out.println(format);
//                updateBtn("Sending...");
                jButton2.setEnabled(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incomplete Form.!", "Error", JOptionPane.ERROR_MESSAGE);
        }      
    
        
// TODO add your handling code here:
    }//GEN-LAST:event_jAudioActionPerformed

    private void jVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jVideoActionPerformed
String sendTo = jLabel2.getText();

        if (sendTo.length() > 0)  {
            try {
                // Format: CMD_SEND_FILE_XD [sender] [receiver] [filename]
                jLabel2.setText("");
//                String fname = getThisFilename(file);//
                String format = "CMD_VIDEO_CALL_XD " + username + " " + sendTo;
                dos.writeUTF(format);
                System.out.println(format);
//                updateBtn("Sending...");
                jButton2.setEnabled(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incomplete Form.!", "Error", JOptionPane.ERROR_MESSAGE);
        }      
    
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jVideoActionPerformed

    /**
     * @param args the command line arguments
     */
    
     //Notification Thread
    public class Notification implements Runnable {

        int count = 0;
        String friend = "";

        @Override
        public void run() {
            while (true) {
                count = list1.getItemCount();
                for (int i = 0; i < count; i++) {
                    friend = list1.getItem(i);
                    try {
                        dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeUTF("CMD_NOTIFICATION " + friend + " " + username);

                        Thread.sleep(1900);
                    } catch (Exception e) {
                    }

                }

            }

        }
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAudio;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    public static javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JButton jVideo;
    private java.awt.List list1;
    private java.awt.List list2;
    // End of variables declaration//GEN-END:variables
}
