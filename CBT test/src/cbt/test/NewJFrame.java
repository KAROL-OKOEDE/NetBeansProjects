/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cbt.test;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO
 */
public class NewJFrame extends javax.swing.JFrame {

String question[] = {"2 + 5","what is my favourite food","who am i","which party is it" };
String OptionA[]= {"7", "cake", "karol", "2"};
String OptionB[]= {"8", "RICE", "kene", "4"};
String OptionC[]= {"17", "BEANS", "kennsal", "3"};
String OptionD[]= {"27", "SWEETS", "kennedy", "22"};
String Answer[]= {"7", "cake", "22", "karol"};
String YourAnswer[]= {"", "","","", "", ""};
int i =0;
String yourOption = "";

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
        jTextArea1.setText(question[i]);
        jRadioButton1.setText(OptionA[i]);
        jRadioButton2.setText(OptionB[i]);
        jRadioButton3.setText(OptionC[i]);
        jRadioButton4.setText(OptionD[i]);
       
        time t = new time();
        Thread t1 = new Thread(t);
        t1.start();
        
        blink b = new blink();
        Thread b1 = new Thread(b);
        b1.start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 0, 153));
        jPanel1.setForeground(new java.awt.Color(0, 0, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Computer Based Test");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("jRadioButton2");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("jRadioButton3");

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("jRadioButton4");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("jRadioButton1");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jButton5.setText("Calculator");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton5)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButton2)
                        .addComponent(jRadioButton3)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(96, 96, 96)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addContainerGap())
        );

        jButton1.setText("Next");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Previous");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Submit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton1)
                .addGap(62, 62, 62)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(0, 29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
if (i< question.length - 1){
    //String[] yourAnswer = null;
   if(jRadioButton1.isSelected()){
       yourOption = jRadioButton1.getText();
   YourAnswer [i]= yourOption; } 
   else if (jRadioButton2.isSelected()){
       yourOption = jRadioButton2.getText();
   YourAnswer [i]= yourOption; }
   else if (jRadioButton3.isSelected()){
       yourOption = jRadioButton3.getText();
   YourAnswer [i]= yourOption; } 
           else if (jRadioButton4.isSelected()){
       yourOption = jRadioButton4.getText();
   YourAnswer [i]= yourOption; } 
     
 
        ++i;    
        jTextArea1.setText(question[i]);
        jRadioButton1.setText(OptionA[i]);
        jRadioButton2.setText(OptionB[i]);
        jRadioButton3.setText(OptionC[i]);
        jRadioButton4.setText(OptionD[i]);
        buttonGroup1.clearSelection();
        yourOption = YourAnswer[i];
    if (yourOption.equals(jRadioButton1.getText())){
    jRadioButton1.setSelected(true);
    }else if (yourOption.equals(jRadioButton2.getText())){
        jRadioButton2.setSelected(true);
    }else if (yourOption.equals(jRadioButton3.getText())){
        jRadioButton3.setSelected(true);
    }else if (yourOption.equals(jRadioButton4.getText())){
        jRadioButton4.setSelected(true); 
            }


} else{
    JOptionPane.showMessageDialog(rootPane, "This is the last question");
}        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
if (i< question.length - 1){
    //String[] yourAnswer = ;
   if(jRadioButton1.isSelected()){
        yourOption = jRadioButton1.getText();
   YourAnswer [i]= yourOption; } 
   else if (jRadioButton2.isSelected()){
      yourOption = jRadioButton2.getText();
   YourAnswer [i]= yourOption; }
   else if (jRadioButton3.isSelected()){
        yourOption = jRadioButton3.getText();
   YourAnswer [i]= yourOption; } 
           else if (jRadioButton4.isSelected()){
        yourOption = jRadioButton4.getText();
   YourAnswer [i]= yourOption; 
                    
    --i;
  buttonGroup1.clearSelection();
    jTextArea1.setText(question[i]);
    jRadioButton1.setText(OptionA[i]);
    jRadioButton2.setText(OptionB[i]);
    jRadioButton3.setText(OptionC[i]);
    jRadioButton4.setText(OptionD[i]);
    yourOption = YourAnswer[i];
    if (yourOption.equals(jRadioButton1.getText())){
    jRadioButton1.setSelected(true);
    }else if (yourOption.equals(jRadioButton2.getText())){
        jRadioButton2.setSelected(true);
    }else if (yourOption.equals(jRadioButton3.getText())){
        jRadioButton3.setSelected(true);
    }else if (yourOption.equals(jRadioButton4.getText())){
        jRadioButton4.setSelected(true); 
            }
          
    JOptionPane.showMessageDialog(rootPane, "this is the first question");// TODO add your handling code here:
}}
           
           
  
    
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
if(jRadioButton1.isSelected()) {
    yourOption =jRadioButton1.getText();
    YourAnswer[i] = yourOption;
}else if (jRadioButton2.isSelected()){
    yourOption = jRadioButton2.getText();
    YourAnswer[i] = yourOption;
}else if (jRadioButton3.isSelected()){
    yourOption = jRadioButton3.getText();
    YourAnswer[i] = yourOption;
}else if (jRadioButton4.isSelected()){
    yourOption = jRadioButton4.getText();
    YourAnswer[i] = yourOption;
}
int score = 0;
String Yanswer = "";
String canswer ="";
for (int i = 0; i < question.length -1; i++){
    Yanswer = YourAnswer[i];
    canswer = Answer[i];
    if(canswer.equals(Yanswer)){
       score++; 
    }
    
}
    Component rootpane = null;
JOptionPane.showMessageDialog(rootpane,"your score is "+ score);
// TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed
 
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
try{
    Runtime.getRuntime().exec("calc");
}  catch (Exception e) {}      // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public class time implements Runnable{
    @Override
    public void run (){
    try{ 
        for (int i = 1; i<2; i--) {
        Date date = new Date();
        jLabel2.setText(date.toString());
        TimeUnit.SECONDS.sleep(1);
        }
          } catch (Exception e) {
                System.out.println("error")  ;     
          }
    }
    }
    public class blink implements Runnable {
    @Override
    public void run(){
    try{
         for (int i = 1; i<2; i--)
            jLabel2.setForeground(Color.red);
         TimeUnit.SECONDS.sleep(1);
            jLabel2.setForeground(Color.blue);
         TimeUnit.SECONDS.sleep(1);
            jLabel2.setForeground(Color.green);
         TimeUnit.SECONDS.sleep(1);
            jLabel2.setForeground(Color.orange);
         TimeUnit.SECONDS.sleep(1);
            jLabel2.setForeground(Color.cyan);
         TimeUnit.SECONDS.sleep(1);
            jLabel2.setForeground(Color.black);
         TimeUnit.SECONDS.sleep(1);
    
    
    } catch (Exception e){
        System.out.println("error")  ;     
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
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
