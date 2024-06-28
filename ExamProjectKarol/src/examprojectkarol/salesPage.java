/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package examprojectkarol;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author LENOVO
 */
public final class salesPage extends javax.swing.JFrame {
    
    String userId = HOME.jTextField1.getText();
    
    
    private double total=0.0;
    private int x =0;
    private double tax =0.0;
    private static final int CHECK_INTERVAL_MINUTES = 5; // Adjust as needed
    private static final int LOW_STOCK_THRESHOLD = 15;
    //private boolean alertSent = false;
    private final ScheduledExecutorService executorService;    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private TrayIcon trayIcon;

    private static final ArrayList<String> storedBirthdayNotifications = new ArrayList<>();
    private static final ArrayList<String> storedLowStockNotifications = new ArrayList<>();
    private static final ArrayList<String> storedAlmostExpiredNotifications = new ArrayList<>();
    private static final ArrayList<String> storedExpiredNotifications = new ArrayList<>();
    /**

    /**
     * Creates new form salesPage
     */
    public salesPage() {
        initComponents();
        //System.out.println(userId);
        
         if (SystemTray.isSupported()) {
            // Create a system tray icon
            SystemTray systemTray = SystemTray.getSystemTray();
            Image trayImage = Toolkit.getDefaultToolkit().getImage("C:\\Users\\USER\\Downloads\\bell.png"); // Specify your icon path
            trayIcon = new TrayIcon(trayImage, "Notification");
            trayIcon.setImageAutoSize(true);

            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        // Schedule the expiry date check to run every hour
        scheduler.scheduleAtFixedRate(this::checkExpiryDate, 0, 1, TimeUnit.DAYS);

        //init();
        setImage();
        setTime();

        // Start the continuous stock checking when the Inventory Manager signs in
        startContinuousStockChecking();
        startContinuousBirthdayChecking();
        checkExpiryDate();


    }
    private void startContinuousStockChecking() {
        // Start the continuous checking of stock levels
        executorService.scheduleAtFixedRate(this::checkStock, 0, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }
    private void checkStock() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306 " + "/exam", "root", "KAROL2005");
            // Query to check for low stock products
            String sql = "SELECT productNo, productName, quantities FROM inventory WHERE quantities < ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setInt(1, LOW_STOCK_THRESHOLD);

                // Execute the query
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String> lowStockProducts = new ArrayList<>();

                // Check if there are any products with low stock
                while (resultSet.next()) {
                    String productName = resultSet.getString("productName");
                    String productCode = resultSet.getString("productNo");
                    int quantity = resultSet.getInt("quantities");

                    lowStockProducts.add(productName + " (Code: " + productCode + ") - Quantity: " + quantity);
                }

                // Close the result set
                resultSet.close();

                
                if (!lowStockProducts.isEmpty()) {
                    DisplayLowStockAlert(lowStockProducts);
                    //sendLowStockAlert(lowStockProducts);
                }
               
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }
    private void DisplayLowStockAlert(List<String> lowStockProducts) {
        // Display a message using JOptionPane
        StringBuilder messageText = new StringBuilder("Low stock alert!\n\nProducts with low stock:\n");
        for (String product : lowStockProducts) {
            messageText.append(product).append("\n");
        }

        JOptionPane.showMessageDialog(this, messageText.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);

        // Send an email alert
        sendLowStockAlert(lowStockProducts);
    }
    private void sendLowStockAlert(List<String> lowStockProducts) {    
            String toEmail = "karol.okoede@pau.edu.ng";
            String senderEmail = "karol.okoede@gmail.com";
            // password generated by app and not actual password
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
                }
            );
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new javax.mail.internet.InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(toEmail));
                 StringBuilder messageText = new StringBuilder("Low stock alert!\n\nProducts with low stock:\n");
            for (String product : lowStockProducts) {
                messageText.append(product).append("\n");
            }
            message.setText(messageText.toString());

            Transport.send(message);
                JOptionPane.showMessageDialog(rootPane, "Email sent");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }
    }
     private void startContinuousBirthdayChecking() {
        // Start the continuous checking of staff birthdays
        scheduler.scheduleAtFixedRate(this::checkBirthdays, 0, 1, TimeUnit.DAYS);
    }
    private void checkBirthdays() {
        // Connect to your database and retrieve staff information
        // Replace the following with your database connection details
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String path = "jdbc:mysql://localhost:3306/exam";
            String user = "root";
            String pass = "KAROL2005";
            Connection connection = DriverManager.getConnection(path,user,pass);
            String sql = "SELECT firstName,surName,dob FROM register";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                    String firstName = rs.getString("firstName");
                    String surName = rs.getString("surName");
                    String dob = rs.getString("dob");
                    String notificationMessage = getBirthdayNotificationMessage(firstName,surName,dob);
//                    if (notificationMessage != null && trayIcon != null) {
//                        trayIcon.displayMessage("Birthday Notification", notificationMessage, TrayIcon.MessageType.INFO);
//                    }
 if (notificationMessage != null) {
                        storedBirthdayNotifications.add(notificationMessage);
                        
                    }
            }
            storedBirthdayNotifications();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }

    private String getBirthdayNotificationMessage(String firstName, String surName, String dobString) {
        // Parse the date of birth from the database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dob = dateFormat.parse(dobString);

            // Get the current date
            Date currentDate = new Date();

            // Check if it's the staff member's birthday
            if (isSameDay(dob, currentDate)) {
                return "Happy Birthday, " + firstName +" " + surName+"!";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if it's not the staff member's birthday
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }
    private void storedBirthdayNotifications() {
    if (!storedBirthdayNotifications.isEmpty()) {
        StringBuilder notificationMessage = new StringBuilder("\n");
        
        // Concatenate all birthday notifications into one message
        for (String notification : storedBirthdayNotifications) {
            notificationMessage.append(notification).append("\n");
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(rootPane, notificationMessage, "Birthday Notification", JOptionPane.INFORMATION_MESSAGE);
            
        });

        // Clear stored notifications after displaying them
    }
}
    
    
    private void checkExpiryDate() {
        // Connect to your database and retrieve product information
        // Replace the following with your database connection details
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String path = "jdbc:mysql://localhost:3306/exam";
            String user = "root";
            String pass = "KAROL2005";
            Connection connection = DriverManager.getConnection(path,user,pass);

            
            String sql = "SELECT productName, productNo, expDate FROM inventory";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                    String productName = rs.getString("productName");
                    String productCode = rs.getString("productNo");
                    String expiryDateString = rs.getString("expDate");
                    
                    
                    // Check if the product is close to or beyond the expiry date
                    String notificationMessage = isExpiryDateClose(expiryDateString,productName,productCode); 
                    if (notificationMessage != null) {
                        storedExpiredNotifications.add(notificationMessage);
                        
                    }
                }
            StringBuilder messageText = new StringBuilder("\n");
            for (String notification : storedExpiredNotifications) {
                messageText.append(notification).append("\n");
                
            }
            JOptionPane.showMessageDialog(this, messageText.toString(), "Expiry Date Alert", JOptionPane.WARNING_MESSAGE);
            storedExpiredNotifications();
            //storedAlmostExpiredNotifications();
        } catch (Exception e) {
        }
        
    }

    private String isExpiryDateClose(String expiryDateString, String productName, String productCode) throws java.text.ParseException {
        // Parse the expiry date from the database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date expiryDate = dateFormat.parse(expiryDateString);
        // Get the current date
        Date currentDate = new Date();
        // Set the threshold for considering the expiry date as close
        long thresholdMillis = 30 * 24 * 60 * 60 * 1000L; // 30 days
        // Check if the expiry date is close or has passed
        //return expiryDate.getTime() - currentDate.getTime() <= thresholdMillis;
        if (expiryDate.getTime() - currentDate.getTime() <= 0) {
            // Product has expired
            //JOptionPane.showMessageDialog(this, "Product '" + productName +" (Code: " + productCode + ")"+ "' has expired!", "Expiry Date Alert", JOptionPane.WARNING_MESSAGE);
            return "Product '" + productName +" (Code: " + productCode + ")"+ "' has expired!";
        } else if (expiryDate.getTime() - currentDate.getTime() <= thresholdMillis) {
            // Product is close to expiry
            
            //JOptionPane.showMessageDialog(this,"Product '" + productName +" (Code: " + productCode + ")"+ "' is about to expire!", "Expiry Date Alert", JOptionPane.WARNING_MESSAGE);
            //JOptionPane.showMessageDialog(this, messageText.toString(), "Expiry Date Alert", JOptionPane.WARNING_MESSAGE);
            //trayIcon.displayMessage("Low Stock Alert", "Product '" + productName +" (Code: " + productCode + ")"+ "' is about to expire!", TrayIcon.MessageType.INFO);
            return "Product '" + productName +" (Code: " + productCode + ")"+ "' is about to expire!";
            //return messageText.toString();
//                String notificationMessage = "Product '" + productName +" (Code: " + productCode + ")"+ "' is about to expire!";
//                storedAlmostExpiredNotifications.add(notificationMessage);
//                storedAlmostExpiredNotifications();

//return "Product '" + productName + "' is close to expiry!";
        }

        return null;
    }
//    private void storedAlmostExpiredNotifications() {
//        // Display the stored notifications
//        if (!storedAlmostExpiredNotifications.isEmpty() && trayIcon != null) {
//            for (String notification : storedAlmostExpiredNotifications) {
//                trayIcon.displayMessage("Expiry Date Alert", notification, TrayIcon.MessageType.INFO);
//            }
//        }
//    }
    private void storedExpiredNotifications() {
        // Display the stored notifications
        if (!storedExpiredNotifications.isEmpty() && trayIcon != null) {
            StringBuilder notificationMessage = new StringBuilder("\n");
            for (String notification : storedExpiredNotifications) {
                notificationMessage.append(notification).append("\n");
                
            }
            SwingUtilities.invokeLater(() -> {
            trayIcon.displayMessage("Expiry Date Alert", notificationMessage.toString(), TrayIcon.MessageType.INFO);
        });
        }
    }    
    
    
//    public void init(){
//    setImage();
//    
//    }

//    public salesPage(int idForThisPage) {
//        initComponents();
//    }


    //  private salesPage() {
    //    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    //}
    public void setImage() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/image/cream.jpg"));
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/image/fish.jpg"));
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/image/waterbottle.jpg"));
        ImageIcon icon3 = new ImageIcon(getClass().getResource("/image/fanta.jpg"));
        ImageIcon icon4 = new ImageIcon(getClass().getResource("/image/biscuit.jpg"));
        ImageIcon icon5 = new ImageIcon(getClass().getResource("/image/notebook.jpg"));
        ImageIcon icon6 = new ImageIcon(getClass().getResource("/image/chicken.jpg"));
        ImageIcon icon7 = new ImageIcon(getClass().getResource("/image/indomie.jpg"));
        ImageIcon icon8 = new ImageIcon(getClass().getResource("/image/soap.jpg"));
        ImageIcon icon9 = new ImageIcon(getClass().getResource("/image/deodorant.jpg"));
        ImageIcon icon10 = new ImageIcon(getClass().getResource("/image/tissue.jpg"));
        ImageIcon icon11 = new ImageIcon(getClass().getResource("/image/apple.jpg"));

        Image img = icon.getImage().getScaledInstance(jLabelimage.getWidth(), jLabelimage.getHeight(), Image.SCALE_SMOOTH);
        Image img1 = icon1.getImage().getScaledInstance(jLabelimage1.getWidth(), jLabelimage1.getHeight(), Image.SCALE_SMOOTH);
        Image img2 = icon2.getImage().getScaledInstance(jLabelimage2.getWidth(), jLabelimage2.getHeight(), Image.SCALE_SMOOTH);
        Image img3 = icon3.getImage().getScaledInstance(jLabelimage3.getWidth(), jLabelimage3.getHeight(), Image.SCALE_SMOOTH);
        Image img4 = icon4.getImage().getScaledInstance(jLabelimage8.getWidth(), jLabelimage8.getHeight(), Image.SCALE_SMOOTH);
        Image img5 = icon5.getImage().getScaledInstance(jLabelimage7.getWidth(), jLabelimage7.getHeight(), Image.SCALE_SMOOTH);
        Image img6 = icon6.getImage().getScaledInstance(jLabelimage4.getWidth(), jLabelimage4.getHeight(), Image.SCALE_SMOOTH);
        Image img7 = icon7.getImage().getScaledInstance(jLabelimage6.getWidth(), jLabelimage6.getHeight(), Image.SCALE_SMOOTH);
        Image img8 = icon8.getImage().getScaledInstance(jLabelimage9.getWidth(), jLabelimage9.getHeight(), Image.SCALE_SMOOTH);
        Image img9 = icon9.getImage().getScaledInstance(jLabelimage10.getWidth(), jLabelimage10.getHeight(), Image.SCALE_SMOOTH);
        Image img10 = icon10.getImage().getScaledInstance(jLabelimage11.getWidth(), jLabelimage11.getHeight(), Image.SCALE_SMOOTH);
        Image img11 = icon11.getImage().getScaledInstance(jLabelimage12.getWidth(), jLabelimage12.getHeight(), Image.SCALE_SMOOTH);

        jLabelimage.setIcon(new ImageIcon(img));
        jLabelimage1.setIcon(new ImageIcon(img1));
        jLabelimage2.setIcon(new ImageIcon(img2));
        jLabelimage3.setIcon(new ImageIcon(img3));
        jLabelimage8.setIcon(new ImageIcon(img4));
        jLabelimage7.setIcon(new ImageIcon(img5));
        jLabelimage4.setIcon(new ImageIcon(img6));
        jLabelimage6.setIcon(new ImageIcon(img7));
        jLabelimage9.setIcon(new ImageIcon(img8));
        jLabelimage10.setIcon(new ImageIcon(img9));
        jLabelimage11.setIcon(new ImageIcon(img10));
        jLabelimage12.setIcon(new ImageIcon(img11));

    }

    
    
    public boolean quantityIsAZero(int qty){
    if(qty == 0){
    JOptionPane.showMessageDialog(null,"Please increase the items quantity ");
    
    return false;
    
    }
    return true;
    
    }
    public void reset() {
        
                 btnTotal.setEnabled(true);

        jSpinner1.setValue(0);
        jSpinner2.setValue(0);
        jSpinner3.setValue(0);
        jSpinner4.setValue(0);
        jSpinner5.setValue(0);
        jSpinner23.setValue(0);
        jSpinner24.setValue(0);
        jSpinner22.setValue(0);
        jSpinner21.setValue(0);
        jSpinner25.setValue(0);
        jSpinner26.setValue(0);
        jSpinner27.setValue(0);
        jTextFieldTax.setText("0.00");
        jTextFieldSubTotal.setText("0.00");
        jTextFieldTotal.setText("0.00");
        jTextArea.setText(" ");
        jCheckBox1.setSelected(false);
     jCheckBox2.setSelected(false);
     jCheckBox3.setSelected(false);
      jCheckBox4.setSelected(false);
     jCheckBox9.setSelected(false);
     jCheckBox8.setSelected(false);
     jCheckBox5.setSelected(false);
     jCheckBox7.setSelected(false);
     jCheckBox10.setSelected(false);
     jCheckBox11.setSelected(false);
     jCheckBox12.setSelected(false);
     jCheckBox13.setSelected(false);
       

    }
    
    
    public void  dudate(){
    jTextFieldTax.setText(String.valueOf(String.format("%1f", tax)));
   jTextFieldSubTotal.setText(String.valueOf(String.format("%1f", total)));
   jTextFieldTotal.setText(String.valueOf(String.format("%1f", total+ tax)));

    
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTxTime = new javax.swing.JLabel();
        jTxtDate = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabelimage = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabelimage1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jSpinner3 = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabelimage2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jSpinner4 = new javax.swing.JSpinner();
        jLabel26 = new javax.swing.JLabel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jLabelimage3 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jSpinner23 = new javax.swing.JSpinner();
        jLabel32 = new javax.swing.JLabel();
        jCheckBox5 = new javax.swing.JCheckBox();
        jLabelimage4 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jSpinner24 = new javax.swing.JSpinner();
        jLabel44 = new javax.swing.JLabel();
        jCheckBox7 = new javax.swing.JCheckBox();
        jLabelimage6 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jSpinner22 = new javax.swing.JSpinner();
        jLabel50 = new javax.swing.JLabel();
        jCheckBox8 = new javax.swing.JCheckBox();
        jLabelimage7 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jSpinner21 = new javax.swing.JSpinner();
        jLabel56 = new javax.swing.JLabel();
        jCheckBox9 = new javax.swing.JCheckBox();
        jLabelimage8 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jSpinner25 = new javax.swing.JSpinner();
        jLabel62 = new javax.swing.JLabel();
        jCheckBox10 = new javax.swing.JCheckBox();
        jLabelimage9 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jSpinner26 = new javax.swing.JSpinner();
        jLabel68 = new javax.swing.JLabel();
        jCheckBox11 = new javax.swing.JCheckBox();
        jLabelimage10 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jSpinner27 = new javax.swing.JSpinner();
        jLabel74 = new javax.swing.JLabel();
        jCheckBox12 = new javax.swing.JCheckBox();
        jLabelimage11 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jCheckBox13 = new javax.swing.JCheckBox();
        jLabelimage12 = new javax.swing.JLabel();
        jSpinner5 = new javax.swing.JSpinner();
        jButton19 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        btnTotal = new javax.swing.JButton();
        btnReceipt = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea = new javax.swing.JTextArea();
        jTextFieldTax = new javax.swing.JTextField();
        jTextFieldSubTotal = new javax.swing.JTextField();
        jTextFieldTotal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1380, 752));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(1370, 752));

        jPanel2.setBackground(new java.awt.Color(51, 51, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PAUMART Cashier Monitor");

        jTxTime.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jTxtDate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(157, 157, 157)
                .addComponent(jTxTime, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 16, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTxtDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(250, 250, 250));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Price:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Quantity:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Cream");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("₦5000");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Purchased:");

        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox1)
                .addGap(66, 66, 66))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel7))
                    .addComponent(jLabelimage, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabelimage, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel8)))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Stock Items");

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Price:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Quantity:");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Fish");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("₦2500");

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Purchased:");

        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox2)
                .addGap(66, 66, 66))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel13))
                    .addComponent(jLabelimage1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel12)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabelimage1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel14))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText("Price:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Quantity:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("Water bottle");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("₦150");

        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Purchased:");

        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox3)
                .addGap(66, 66, 66))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel19))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabelimage2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabelimage2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox3)
                    .addComponent(jLabel20)))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("Price:");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("Quantity:");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setText("fanta");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setText("₦250");

        jSpinner4.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("Purchased:");

        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox4)
                .addGap(66, 66, 66))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel25))
                    .addComponent(jLabelimage3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel24)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabelimage3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox4)
                    .addComponent(jLabel26)))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel28.setText("Price:");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setText("Quantity:");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Chicken");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("₦3000");

        jSpinner23.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel32.setText("Purchased:");

        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox5)
                .addGap(85, 85, 85))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner23, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel31))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel30))
                    .addComponent(jLabelimage4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabelimage4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jSpinner23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox5)
                    .addComponent(jLabel32)))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel40.setText("Price:");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel41.setText("Quantity:");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("indomie");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel43.setText("₦600");

        jSpinner24.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setText("Purchased:");

        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel44)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox7)
                .addGap(66, 66, 66))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner24, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelimage6, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel42)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                            .addComponent(jLabel40)
                            .addGap(25, 25, 25)
                            .addComponent(jLabel43))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabelimage6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(jSpinner24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox7)
                    .addComponent(jLabel44)))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setText("Price:");

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel47.setText("Quantity:");

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("Book");

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel49.setText("₦1500");

        jSpinner22.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel50.setText("Purchased:");

        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox8)
                .addGap(66, 66, 66))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner22, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel46)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel49))
                    .addComponent(jLabelimage7, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel48)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabelimage7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(jSpinner22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox8)
                    .addComponent(jLabel50)))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel52.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel52.setText("Price:");

        jLabel53.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel53.setText("Quantity:");

        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText("Biscuit");

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel55.setText("₦4000");

        jSpinner21.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel56.setText("Purchased:");

        jCheckBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox9)
                .addGap(66, 66, 66))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner21, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel55))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addGap(32, 32, 32)
                        .addComponent(jLabel54))
                    .addComponent(jLabelimage8, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabelimage8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(jSpinner21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox9)
                    .addComponent(jLabel56)))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel58.setText("Price:");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel59.setText("Quantity:");

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel60.setText("Soap");

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel61.setText("₦900");

        jSpinner25.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel62.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel62.setText("Purchased:");

        jCheckBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox10)
                .addGap(66, 66, 66))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner25, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel58)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel61))
                    .addComponent(jLabelimage9, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel60)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabelimage9, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jLabel61))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(jSpinner25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox10)
                    .addComponent(jLabel62)))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel64.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel64.setText("Price:");

        jLabel65.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel65.setText("Quantity:");

        jLabel67.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel67.setText("₦2500");

        jSpinner26.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel68.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel68.setText("Purchased:");

        jCheckBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox11ActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("deodorant");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox11)
                .addGap(66, 66, 66))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner26, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelimage10, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel67))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel21)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabelimage10, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(jLabel67))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(jSpinner26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox11)
                    .addComponent(jLabel68)))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel70.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel70.setText("Price:");

        jLabel71.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel71.setText("Quantity:");

        jLabel72.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel72.setText("Tissue");

        jLabel73.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel73.setText("₦250");

        jSpinner27.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));

        jLabel74.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel74.setText("Purchased:");

        jCheckBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox12)
                .addGap(66, 66, 66))
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner27, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelimage11, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel73))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel72)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabelimage11, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel72)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(jLabel73))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(jSpinner27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox12)
                    .addComponent(jLabel74)))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jLabel76.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel76.setText("Price:");

        jLabel77.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel77.setText("Quantity:");

        jLabel78.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel78.setText("Apple");

        jLabel79.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel79.setText("₦800");

        jLabel80.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel80.setText("Purchased:");

        jCheckBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox13ActionPerformed(evt);
            }
        });

        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel80)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox13)
                .addGap(66, 66, 66))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel77)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel76)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel79))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jLabel78))
                    .addComponent(jLabelimage12, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabelimage12, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel78)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(jLabel79))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox13)
                    .addComponent(jLabel80)))
        );

        jButton19.setText("🔔");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.setBackground(new java.awt.Color(250, 250, 250));
        jPanel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        btnTotal.setBackground(new java.awt.Color(255, 102, 255));
        btnTotal.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnTotal.setForeground(new java.awt.Color(255, 255, 255));
        btnTotal.setText("Pay");
        btnTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotalActionPerformed(evt);
            }
        });

        btnReceipt.setBackground(new java.awt.Color(0, 204, 204));
        btnReceipt.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnReceipt.setForeground(new java.awt.Color(255, 255, 255));
        btnReceipt.setText("Receipt");
        btnReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiptActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 153, 51));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Reset");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 0, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Exit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnTotal)
                .addGap(54, 54, 54)
                .addComponent(btnReceipt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(67, 67, 67)
                .addComponent(jButton4)
                .addGap(41, 41, 41))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addComponent(btnReceipt))
                    .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.setBackground(new java.awt.Color(250, 250, 250));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230), null));

        jTextArea.setColumns(20);
        jTextArea.setRows(5);
        jScrollPane1.setViewportView(jTextArea);

        jTextFieldTax.setEditable(false);
        jTextFieldTax.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jTextFieldTax.setText(" ₦0.00");
        jTextFieldTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTaxActionPerformed(evt);
            }
        });

        jTextFieldSubTotal.setEditable(false);
        jTextFieldSubTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jTextFieldSubTotal.setText(" ₦0.00");
        jTextFieldSubTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSubTotalActionPerformed(evt);
            }
        });

        jTextFieldTotal.setEditable(false);
        jTextFieldTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jTextFieldTotal.setText(" ₦0.00");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Taxes");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("SubTotal");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setText("Total");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextFieldTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                            .addComponent(jTextFieldSubTotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                            .addComponent(jTextFieldTax, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(30, 30, 30))))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(12, 12, 12)
                        .addComponent(jTextFieldSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(0, 21, Short.MAX_VALUE))
        );

        jTextField1.setText("Search Product Name");
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"301", "Chicken", "₦3000"},
                {"302", "Cream", "₦5000"},
                {"303", "indomie", "₦600"},
                {"304", "Fanta", "₦250"},
                {"305", "Water bottle", "₦150"},
                {"306", "Biscuit", "₦4000"},
                {"307", "Book", "₦1500"},
                {"308", "Soap", "₦900"},
                {"309", "Deodorant", "₦2500"},
                {"310", "Tissue", "₦250"},
                {"311", "Apple", "₦800"},
                {"312", "Fish", "2500"}
            },
            new String [] {
                "ProductNo", "ProductName", "Price"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductName", "Quantity"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 108, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
  
    public void PAUMART(){
    jTextArea.setText("*************************PAUMART************************\n"
                         +"Time: "+jTxTime.getText()+"  Date: "+ jTxtDate.getText() +"\n" 
                         +"*************************************************************\n"    
                          +"Item Name: \t\t\t" + "Price(₦)\n");
    
    
    
    }
    
    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
   int qty  = Integer.parseInt(jSpinner1.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox1.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*5000.00;
                total+=price;
                getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel6.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox1.setSelected(false);
    }
    
    DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel6.getText(),String.valueOf(qty)};
    td.addRow(data);
    // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed
    public void setTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(salesPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    Date date  =  new Date();
                    SimpleDateFormat tf =  new SimpleDateFormat("h:mm:ss aa");
                    SimpleDateFormat df  = new SimpleDateFormat("EEEEE, dd-MM-yyyy");
                    String time = tf.format(date);
                    jTxTime.setText(time.split(" ")[0] +" "+time.split(" ")[1]);
                    jTxtDate.setText(df.format(date));
                }

            }

        }).start();
    }
      
    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
 int qty  = Integer.parseInt(jSpinner2.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox2.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*2500.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel12.getText()+ "\t\t\t"+ price+ "\n");
         dudate();
     } else{
     jCheckBox2.setSelected(false);
    } 
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel12.getText(),String.valueOf(qty)};
    td.addRow(data);
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
    int qty  = Integer.parseInt(jSpinner3.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox3.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*150.00;
        total+=price;
        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel18.getText()+ "\t\t\t"+String.format("%2f", price)+ "\n");
         dudate();
     } else{
     jCheckBox3.setSelected(false);
    } 
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel18.getText(),String.valueOf(qty)};
    td.addRow(data);

        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
    int qty  = Integer.parseInt(jSpinner4.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox4.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*250.00;
        total+=price;
        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel24.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox4.setSelected(false);
    } 
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel24.getText(),String.valueOf(qty)};
    td.addRow(data);

        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
int qty  = Integer.parseInt(jSpinner23.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox5.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*3000.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel30.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox5.setSelected(false);
    }  
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel30.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
int qty  = Integer.parseInt(jSpinner24.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox7.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*600.00;
        total+=price;
        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel42.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox7.setSelected(false);
    } 
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel42.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
int qty  = Integer.parseInt(jSpinner22.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox8.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*1500.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel48.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox8.setSelected(false);
    }  
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel48.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void jCheckBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox9ActionPerformed
int qty  = Integer.parseInt(jSpinner21.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox9.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*4000.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel54.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox9.setSelected(false);
    }     
 DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel54.getText(),String.valueOf(qty)};
    td.addRow(data);    // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox9ActionPerformed

    private void jCheckBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox10ActionPerformed
int qty  = Integer.parseInt(jSpinner25.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox10.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*900.00;
        total+=price;
        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel60.getText()+"\t\t\t"+ price + "\n");
         dudate();
     } else{
     jCheckBox10.setSelected(false);
    }   
    
 DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel60.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox10ActionPerformed

    private void jCheckBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox11ActionPerformed
int qty  = Integer.parseInt(jSpinner26.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox11.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*2500.00;
        total+=price;
        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel21.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox11.setSelected(false);
    }  
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel21.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox11ActionPerformed

    private void jCheckBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox12ActionPerformed
int qty  = Integer.parseInt(jSpinner27.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox12.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*250.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel72.getText()+ "\t\t\t"+price+ "\n");
         dudate();
     } else{
     jCheckBox12.setSelected(false);
    }   
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel72.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    
    
    
    
    }//GEN-LAST:event_jCheckBox12ActionPerformed
    
    private void jCheckBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox13ActionPerformed
int qty  = Integer.parseInt(jSpinner5.getValue().toString());
    if (quantityIsAZero(qty) && jCheckBox13.isSelected()) { 
        x++;
        if(x==1){
            PAUMART();
        }
        
        double price = qty*800.00;
        total+=price;
                        getTax((int) total);

        jTextArea.setText(jTextArea.getText()+  x +". "+ jLabel78.getText()+ "\t\t\t"+price + "\n");
         dudate();
     } else{
     jCheckBox13.setSelected(false);
    }  
     DefaultTableModel td = (DefaultTableModel) jTable2.getModel();
    String data[] = {jLabel78.getText(),String.valueOf(qty)};
    td.addRow(data);// TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox13ActionPerformed

    private void btnTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTotalActionPerformed
     if(total == 0.0){
        JOptionPane.showMessageDialog(null,"You are yet to select any item.");
     } else{ 
         jTextArea.setText(jTextArea.getText()
         +"\n*************************************************************\n"
         + "Tax:  \t\t\t "+ tax +"\n"
         +"Sub Total: \t\t\t"+ total +"\n"
         +"Total: \t\t\t"+(total+tax)+"\n\n" 
         +"*************************PAUMART************************\n"
         +"*************************Thank You************************\n"

           
         
         );
         btnTotal.setEnabled(false);
     
     }
     
     
     
  try{
DefaultTableModel tableMode1 = (DefaultTableModel) jTable2.getModel();
int rowCount = tableMode1.getRowCount();
String productNames[] = new String[rowCount];
int quantity[] = new int[rowCount];
for(int i = 0; i < tableMode1.getRowCount(); i++){
    productNames[i] = tableMode1.getValueAt(i,0).toString();
    quantity[i] = Integer.parseInt(tableMode1.getValueAt(i, 1).toString());
}
LocalDateTime now = LocalDateTime.now();
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String sqlDateTime = now.format(formatter);

String insertQuery = "INSERT INTO transaction VALUE(?, ?, ?, ?)";
Class.forName("com.mysql.cj.jdbc.Driver");
String path = "jdbc:mysql://localhost:3306/exam";
String user = "root";
String pass = "KAROL2005";
Connection con = DriverManager.getConnection(path, user, pass);
for(int i = 0; i < quantity.length; i++){
    PreparedStatement preparedStatement = con.prepareStatement(insertQuery);
preparedStatement.setString(1, userId);
preparedStatement.setString(2, productNames[i]);
preparedStatement.setInt(3, quantity[i]);
preparedStatement.setTimestamp(4, Timestamp.valueOf(sqlDateTime));
int rs = preparedStatement.executeUpdate();


}


JOptionPane.showMessageDialog(rootPane, "Record Inserted");
tableMode1.setRowCount(0);
}   catch(Exception e){
JOptionPane.showMessageDialog(rootPane, e);
    }  

        // TODO add your handling code here:
    }//GEN-LAST:event_btnTotalActionPerformed
    public void getTax(int t) {
        if (t >= 1000.0 && t <= 2000.0) {
            tax = 0.5;
        } else if (t > 20000.0 && t < 4000.0) {
            tax = 1.0;
        } else if (t > 4000.0 && t < 6000.0) {
            tax = 2.0;
        } else if (t > 6000.0 && t < 8000.0) {
            tax = 3.0;
        } else if (t > 8000.0 && t < 10000.0) {
            tax = 4.0;
        }  else if (t > 10000.0 && t < 15000.0) {
            tax = 8.0;
        }  else if (t > 15000.0 && t < 20000.0) {
            tax = 10.0;
        }else if(t< 200.0){
            tax = 15.0;

        }

    }
    
    private void btnReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiptActionPerformed
        if (total != 0 ) {
            if(!btnTotal.isEnabled()){
                  try {
                jTextArea.print();
            } catch (PrinterException ex) {
                Logger.getLogger(salesPage.class.getName()).log(Level.SEVERE, null, ex);
            }

        }else{
            JOptionPane.showMessageDialog(null, "First calculate the total price ");

        
        } }else{
            JOptionPane.showMessageDialog(null, "You are yet to purchase any products ");
            }
            
            
            
            
          

// TODO add your handling code here:
    }//GEN-LAST:event_btnReceiptActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextFieldTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTaxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTaxActionPerformed

    private void jTextFieldSubTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSubTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSubTotalActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
                 btnTotal.setEnabled(true);
           total = 0.0;
           x = 0;
           tax = 0.0;
        jSpinner1.setValue(0);
        jSpinner2.setValue(0);
        jSpinner3.setValue(0);
        jSpinner4.setValue(0);
        jSpinner5.setValue(0);
        jSpinner21.setValue(0);
        jSpinner22.setValue(0);
        jSpinner23.setValue(0);
        jSpinner24.setValue(0);
        jSpinner25.setValue(0);
        jSpinner26.setValue(0);
        jSpinner27.setValue(0);
        jTextFieldTax.setText(" ₦0.00");
        jTextFieldSubTotal.setText(" ₦0.00");
        jTextFieldTotal.setText(" ₦0.00");
        jTextArea.setText(" ");
     jCheckBox1.setSelected(false);
     jCheckBox2.setSelected(false);
     jCheckBox3.setSelected(false);
      jCheckBox4.setSelected(false);
     jCheckBox9.setSelected(false);
     jCheckBox8.setSelected(false);
     jCheckBox5.setSelected(false);
     jCheckBox7.setSelected(false);
     jCheckBox10.setSelected(false);
     jCheckBox11.setSelected(false);
     jCheckBox12.setSelected(false);
     jCheckBox13.setSelected(false);
     DefaultTableModel table = (DefaultTableModel) jTable2.getModel();
     table.setRowCount(0);

     


        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
DefaultTableModel dt = (DefaultTableModel) jTable1.getModel();
TableRowSorter<DefaultTableModel> dtj = new TableRowSorter<>(dt);
jTable1.setRowSorter(dtj);
dtj.setRowFilter(RowFilter.regexFilter(jTextField1.getText()));

        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
DefaultTableModel dt = (DefaultTableModel) jTable1.getModel();
TableRowSorter<DefaultTableModel> dtj =  new TableRowSorter<>(dt);
jTable1.setRowSorter(dtj);
dtj.setRowFilter(RowFilter.regexFilter(jTextField1.getText()));
// TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
startContinuousStockChecking();  
  startContinuousBirthdayChecking();
          checkExpiryDate();

// TODO add your handling code here:
    }//GEN-LAST:event_jButton19ActionPerformed

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
            java.util.logging.Logger.getLogger(salesPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(salesPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(salesPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(salesPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new salesPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReceipt;
    private javax.swing.JButton btnTotal;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelimage;
    private javax.swing.JLabel jLabelimage1;
    private javax.swing.JLabel jLabelimage10;
    private javax.swing.JLabel jLabelimage11;
    private javax.swing.JLabel jLabelimage12;
    private javax.swing.JLabel jLabelimage2;
    private javax.swing.JLabel jLabelimage3;
    private javax.swing.JLabel jLabelimage4;
    private javax.swing.JLabel jLabelimage6;
    private javax.swing.JLabel jLabelimage7;
    private javax.swing.JLabel jLabelimage8;
    private javax.swing.JLabel jLabelimage9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner21;
    private javax.swing.JSpinner jSpinner22;
    private javax.swing.JSpinner jSpinner23;
    private javax.swing.JSpinner jSpinner24;
    private javax.swing.JSpinner jSpinner25;
    private javax.swing.JSpinner jSpinner26;
    private javax.swing.JSpinner jSpinner27;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldSubTotal;
    private javax.swing.JTextField jTextFieldTax;
    private javax.swing.JTextField jTextFieldTotal;
    private javax.swing.JLabel jTxTime;
    private javax.swing.JLabel jTxtDate;
    // End of variables declaration//GEN-END:variables
}
