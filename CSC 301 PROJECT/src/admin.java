/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author LENOVO
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.Timer;

public class admin extends javax.swing.JFrame {

    private String matno;
            


    String durationS;

    int duration;
    List<String> questions = new ArrayList<>();

    List<String> optionA = new ArrayList<>();

    List<String> optionB = new ArrayList<>();

    List<String> optionC = new ArrayList<>();

    List<String> optionD = new ArrayList<>();

    List<String> answer = new ArrayList<>();

    List<String> userAnswer = new ArrayList<>(); //this should be filled up with empty strings

    String yourOption = "";

    int score;

//    String question[] = new String[100];
//    String OptionA[] = new String[100];
//    String OptionB[] = new String[100];
//    String OptionC[] = new String[100];
//    String OptionD[] = new String[100];
//    String Answer[] = new String[100];
//    String YourAnswer[];
    int i = 0;
//    String yourOption = "";

    /**
     * Creates new form admin
     */
    Timer time;

    public admin(String matno) {
        initComponents();
        this.matno = matno;
        //part that gets the questions and all and stores in an array
        try {

            int numberOfQuestions = 3;
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt", "root", "KAROL2005");

            // Create a statement with a query to select a given amount of questions randomly
            String query = "SELECT question, optionA, optionB, optionC, optionD, answer FROM question ORDER BY RAND() LIMIT ?";

            // Adjust the number in setInt(1, numberOfQuestions) to the desired number of questions
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, numberOfQuestions); //"numberofquestions" executes the LIMIT stuff. basically limits/regulates the amount of questions

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the results and store them in the array
            while (resultSet.next()) {
                String a = resultSet.getString("question");
                String b = resultSet.getString("optionA");
                String c = resultSet.getString("optionB");
                String d = resultSet.getString("optionC");
                String e = resultSet.getString("optionD");
                String f = resultSet.getString("answer");

                questions.add(a);
                optionA.add(b);
                optionB.add(c);
                optionC.add(d);
                optionD.add(e);
                answer.add(f);
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //sets first set of questions
        // jQNo.setText("" + m);
        jTextArea1.setText(questions.get(i));
        jRadioButton1.setText(optionA.get(i));
        jRadioButton2.setText(optionB.get(i));
        jRadioButton3.setText(optionC.get(i));
        jRadioButton4.setText(optionD.get(i));

        //dependent on whatever the question size is
        //the iterator here which was i before was conflicting with the initial i, so it was changed to j
        for (int j = 0; j < questions.size(); j++) {
            userAnswer.add("n/a"); // Add an empty string for each question
        }

        System.out.println(questions);

        //retieve duration
        //timer 
        //for duration
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");                               //db name       //username //password
            java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt", "root", "KAROL2005");
            Statement st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery("select *from duration");//automatically selects the last value amongst all values
            while (rs.next()) {
                //jLabel2.setText(rs.getString(1));
                durationS = rs.getString(1);
            }
        } catch (Exception e) {

        }
        System.out.println("duration: " + durationS);
        duration = Integer.parseInt(durationS);
        System.out.println("" + duration);
//        timer

        setLocationRelativeTo(this);

        time = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int hours = duration / 3600;
                int minutes = (duration % 3600) / 60;
                int seconds = duration % 60;

                String formattedTime = String.format("Time remaining: %02d:%02d:%02d%n", hours, minutes, seconds);
                jDuration.setText(formattedTime);

                if (duration == 0) {
                    time.stop();
                    JOptionPane.showMessageDialog(null, "Timer finished!");

                }

                --duration;
            }
        });
        time.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jDuration = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(102, 102, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("CBT TEST");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        buttonGroup1.add(jRadioButton1);

        buttonGroup1.add(jRadioButton2);

        buttonGroup1.add(jRadioButton3);

        buttonGroup1.add(jRadioButton4);

        jButton1.setText("Previous");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Next");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Calculator");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Submit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jDuration.setText("jLabel3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jDuration)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(139, 139, 139))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addGap(32, 32, 32)
                                .addComponent(jButton3)))))
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jDuration))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(10, 10, 10)
                .addComponent(jButton4)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int a = JOptionPane.showConfirmDialog(null, "Do you want to submit?", "Select", JOptionPane.YES_NO_OPTION);

        if (a == 0) {
            time.stop();

            if (jRadioButton1.isSelected()) {
                yourOption = jRadioButton1.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton2.isSelected()) {
                yourOption = jRadioButton2.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton3.isSelected()) {
                yourOption = jRadioButton3.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton4.isSelected()) {
                yourOption = jRadioButton4.getText();
                userAnswer.set(i, yourOption);
            }

            if (userAnswer.get(i) == jRadioButton1.getText()) {
                jRadioButton1.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton2.getText()) {
                jRadioButton2.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton3.getText()) {
                jRadioButton3.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton4.getText()) {
                jRadioButton4.setSelected(true);
            }

            String ans; //actual answer
            String sanswer; //student answer
            int k;
            for (k = 0; k < questions.size(); k++) {
                ans = answer.get(k);
                sanswer = userAnswer.get(k);

                if (sanswer.equals(ans)) {
                    score = score + 5;
                } else {
                    score = score + 0;
                }

            }// TODO add your handling code here:
            System.out.println(score);

            String updateQuery = "UPDATE studentdetails SET score = ? WHERE matno = ?";

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt", "root", "KAROL2005"); PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                String studentMarks = String.valueOf(score);

                // Set the parameters for the update statement
                preparedStatement.setString(1, studentMarks);

                //update where matno equals matno
                preparedStatement.setString(2, matno);

                // Execute the update statement
                preparedStatement.executeUpdate();

                // Optionally commit the changes (depending on your transaction requirements)
                //connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String subject = "Your score for the quiz!\n";
            String receiver = "karol.okoede@pau.edu.ng";
            String body = "This mail contains the score for the quiz .\n"
                    + "Score " + score + "\n"
                    + "thank you";
            String senderEmail = "karol.okoede@gmail.com";
            String senderPassword = "jssbzrezcedsjhpu";
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                JOptionPane.showMessageDialog(rootPane, "Email Sent");

            } catch (MessagingException e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }

        }

//        if (jRadioButton1.isSelected()) {
//            yourOption = jRadioButton1.getText();
//            YourAnswer[i] = yourOption;
//        } else if (jRadioButton2.isSelected()) {
//            yourOption = jRadioButton2.getText();
//            YourAnswer[i] = yourOption;
//        } else if (jRadioButton3.isSelected()) {
//            yourOption = jRadioButton3.getText();
//            YourAnswer[i] = yourOption;
//        } else if (jRadioButton4.isSelected()) {
//            yourOption = jRadioButton4.getText();
//            YourAnswer[i] = yourOption;
//        }
//
//        int score = 0;
//        String yanswer = " ";
//        String canswer = " ";
//        for (i; i < question.length - 1; i++) {
//            yanswer = YourAnswer[i];
//            canswer = Answer[i];
//            if (canswer.equals(yanswer)) {
//                score++;
//            }
//
//        }
//
//        JOptionPane.showMessageDialog(rootPane, "Your Score is " + score);
//        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            Runtime.getRuntime().exec("calc");        // TODO add your handling code here:
        } catch (Exception e) {

        }          // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (i == (questions.size() - 1)) {
            JOptionPane.showMessageDialog(null, "This is the last question");
        } else {
            if (jRadioButton1.isSelected()) {
                yourOption = jRadioButton1.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton2.isSelected()) {
                yourOption = jRadioButton2.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton3.isSelected()) {
                yourOption = jRadioButton3.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton4.isSelected()) {
                yourOption = jRadioButton4.getText();
                userAnswer.set(i, yourOption);
            }

            //changes question
            i = i + 1;

            jTextArea1.setText(questions.get(i));
            jRadioButton1.setText(optionA.get(i));
            jRadioButton2.setText(optionB.get(i));
            jRadioButton3.setText(optionC.get(i));
            jRadioButton4.setText(optionD.get(i));
            buttonGroup1.clearSelection();

            if (userAnswer.get(i) == jRadioButton1.getText()) {
                jRadioButton1.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton2.getText()) {
                jRadioButton2.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton3.getText()) {
                jRadioButton3.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton4.getText()) {
                jRadioButton4.setSelected(true);
            }

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (i == 0) {
            JOptionPane.showMessageDialog(null, "This is the first question");

            if (jRadioButton1.isSelected()) {
                yourOption = jRadioButton1.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton2.isSelected()) {
                yourOption = jRadioButton2.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton3.isSelected()) {
                yourOption = jRadioButton3.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton4.isSelected()) {
                yourOption = jRadioButton4.getText();
                userAnswer.set(i, yourOption);
            }

        } else {
            if (jRadioButton1.isSelected()) {
                yourOption = jRadioButton1.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton2.isSelected()) {
                yourOption = jRadioButton2.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton3.isSelected()) {
                yourOption = jRadioButton3.getText();
                userAnswer.set(i, yourOption);
            } else if (jRadioButton4.isSelected()) {
                yourOption = jRadioButton4.getText();
                userAnswer.set(i, yourOption);
            }
            i = i - 1;

            jTextArea1.setText(questions.get(i));
            jRadioButton1.setText(optionA.get(i));
            jRadioButton2.setText(optionB.get(i));
            jRadioButton3.setText(optionC.get(i));
            jRadioButton4.setText(optionD.get(i));

            buttonGroup1.clearSelection();

            if (userAnswer.get(i) == jRadioButton1.getText()) {
                jRadioButton1.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton2.getText()) {
                jRadioButton2.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton3.getText()) {
                jRadioButton3.setSelected(true);
            } else if (userAnswer.get(i) == jRadioButton4.getText()) {
                jRadioButton4.setSelected(true);
            }
            // TODO add your handling code here:
        }
    }//GEN-LAST:event_jButton1ActionPerformed

//    private class timer implements Runnable {
//
//     
//        @Override
//        public void run() {
//            
//
//            while (duration > 0) {
//                int remainingHours = duration / 3600;
//                int remainingMinutes = (duration % 3600) / 60;
//                int remainingSeconds = duration % 60;
//                String formattedTime = String.format("Time remaining: %02d:%02d:%02d%n", remainingHours, remainingMinutes, remainingSeconds);
//                jDuration.setText(formattedTime);
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                duration--;
//            }
//            String formattedTime = String.format("Time remaining: %02d:%02d:%02d%n", 0, 0, 0);
//            jDuration.setText(formattedTime);
//            JOptionPane.showMessageDialog(rootPane, "Time Up", "Time Up", JOptionPane.INFORMATION_MESSAGE);
//        }
//        
//    }
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(admin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(admin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(admin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(admin.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new admin("").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jDuration;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
