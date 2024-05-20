/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package examprojectkarol;

import static examprojectkarol.loginPage.photo;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.internet.ParseException;
import javax.swing.SwingUtilities;

/**
 *
 * @author LENOVO
 */
public class inventoryPage extends javax.swing.JFrame {
    private static final int CHECK_INTERVAL_MINUTES = 5; // Adjust as needed
    private static final int LOW_STOCK_THRESHOLD = 15;
    private static int SCALE_SMOOTH;
    //private boolean alertSent = false;
    private final ScheduledExecutorService executorService;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private TrayIcon trayIcon;
    private static final ArrayList<String> storedBirthdayNotifications = new ArrayList<>();
    private static final ArrayList<String> storedLowStockNotifications = new ArrayList<>();
    private static final ArrayList<String> storedAlmostExpiredNotifications = new ArrayList<>();
    private static final ArrayList<String> storedExpiredNotifications = new ArrayList<>();
    
    /**
     * Creates new form inventoryPage
     */
    public inventoryPage() {
        initComponents();
        
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
//    private inventoryPage() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jComboBox4 = new javax.swing.JComboBox<>();
        jDateChooser4 = new com.toedter.calendar.JDateChooser();
        jLabel31 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Product No.");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Product Name");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Manufacturing Date");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Manufacturer");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Generic", "Exported" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Quantities");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select quantities", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", " " }));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Expiry Date");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Price");

        jButton1.setText("Import Stock");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Search");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton18.setText("🔔");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton15.setText("Browse");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton15))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField1)
                                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 136, Short.MAX_VALUE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addGap(15, 15, 15))))
                                    .addComponent(jLabel6))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(jLabel7)
                                        .addGap(39, 39, 39)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(67, 67, 67)
                                        .addComponent(jButton2))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(191, 191, 191)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(159, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addComponent(jButton15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(36, 36, 36))
        );

        jTabbedPane1.addTab("Add stock", jPanel1);

        jPanel2.setBackground(new java.awt.Color(153, 255, 255));
        jPanel2.setForeground(new java.awt.Color(153, 255, 255));

        jButton19.setText("🔔");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Product No");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Manufacturing Date");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Mnaufacturer");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Generic", "Exported" }));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Expiry Date");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Product Name");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Quantities");

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select quantities", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", " " }));

        jButton4.setText("Browse");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Price");

        jButton6.setText("Update");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton19)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(39, 39, 39)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(37, 37, 37)
                                        .addComponent(jButton3))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.Alignment.LEADING, 0, 146, Short.MAX_VALUE)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.LEADING))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDateChooser4, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)))))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton6)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(127, 127, 127))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(37, 37, 37)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(34, 34, 34))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(32, 32, 32)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jDateChooser4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addGap(20, 20, 20))
        );

        jTabbedPane1.addTab("Search /Update/ Delete", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      try {
            String productNo= jTextField1.getText();
            String productName = jTextField2.getText();
            Date manuDate = jDateChooser1.getDate();
            java.sql.Date sqlmanuDate = new java.sql.Date(manuDate.getTime());
            
            String manufacturers = jComboBox1.getSelectedItem().toString();
            String quantities = jComboBox2.getSelectedItem().toString();
            Date expDate = jDateChooser2.getDate();
            java.sql.Date sqlexpDate = new java.sql.Date(expDate.getTime());
            String price = jTextField3.getText();
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306 " + "/exam", "root", "KAROL2005");
            PreparedStatement ps = con.prepareStatement("insert INTO inventory (productNo, productName, manuDate, manufacturers, quantities, expdate, price, photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, productNo);
            ps.setString(2, productName);
            ps.setDate(3, sqlmanuDate);
            ps.setString(4, manufacturers);
            ps.setString(5, quantities);
            ps.setDate(6, sqlexpDate);
            ps.setString(7, price);
            ps.setBytes(8, photo);

            
            int rs = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "inserted into database");

           

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);

        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
 try {
            String productNo = jTextField1.getText();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306" + "/exam", "root", "KAROL2005");
            PreparedStatement ps = con.prepareStatement("select * from inventory where productNo=?");
            ps.setString(1, productNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jTextField2.setText(rs.getString(2));
                jTextField3.setText(rs.getString(7));
                jDateChooser1.setDate(rs.getDate(3));
                jComboBox1.setSelectedItem(rs.getString(4));
                jComboBox2.setSelectedItem(rs.getString(5));
                 jDateChooser2.setDate(rs.getDate(6));
                 
                  byte[] images = rs.getBytes(8);
                if(images != null){
                ImageIcon originalIcon = new ImageIcon(images);
                Image scaledImage = originalIcon.getImage().getScaledInstance(jLabel30.getWidth(), jLabel30.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                jLabel30.setIcon(scaledIcon);
                }else{
                    jLabel30.setIcon(null);
                }
                
    
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
      startContinuousStockChecking();
      startContinuousBirthdayChecking();
              checkExpiryDate();


        // TODO add your handling code here:
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
    try {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("inventoryPage", "jpg");
            jFileChooser1.setAcceptAllFileFilterUsed(false);
            jFileChooser1.addChoosableFileFilter(filter);
            jFileChooser1.showOpenDialog(null);
            File f = jFileChooser1.getSelectedFile();
        String filename = f.getAbsolutePath();
            String path = f.getAbsolutePath();
            jTextField4.setText(path);
            Image im = Toolkit.getDefaultToolkit().createImage(path);
            im = im.getScaledInstance(jLabel30.getWidth(), jLabel30.getHeight(), loginPage.SCALE_SMOOTH);
            ImageIcon ic = new ImageIcon(im);
            jLabel30.setIcon(ic);
            File image = new File(filename);
            FileInputStream fis = new FileInputStream(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] Byte = new byte[1024];
            for(int i; (i = fis.read(Byte)) != -1;){
                baos.write(Byte,0,i);
            }
            photo = baos.toByteArray();
        } catch (Exception e) {
        }  

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    try {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("inventoryPage", "jpg");
            jFileChooser1.setAcceptAllFileFilterUsed(false);
            jFileChooser1.addChoosableFileFilter(filter);
            jFileChooser1.showOpenDialog(null);
            File f = jFileChooser1.getSelectedFile();
        String filename = f.getAbsolutePath();
            String path = f.getAbsolutePath();
            jTextField4.setText(path);
            Image im = Toolkit.getDefaultToolkit().createImage(path);
            im = im.getScaledInstance(jLabel31.getWidth(), jLabel31.getHeight(), inventoryPage.SCALE_SMOOTH);
            ImageIcon ic = new ImageIcon(im);
            jLabel31.setIcon(ic);
            File image = new File(filename);
            FileInputStream fis = new FileInputStream(image);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] Byte = new byte[1024];
            for(int i; (i = fis.read(Byte)) != -1;){
                baos.write(Byte,0,i);
            }
            photo = baos.toByteArray();
        } catch (Exception e) {
        }           // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
 try {
            String productNo = jTextField5.getText();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306" + "/exam", "root", "KAROL2005");
            PreparedStatement ps = con.prepareStatement("select * from inventory where productNo=?");
            ps.setString(1, productNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jTextField6.setText(rs.getString(2));
                jTextField7.setText(rs.getString(7));
                jDateChooser3.setDate(rs.getDate(3));
                jComboBox4.setSelectedItem(rs.getString(4));
                jComboBox4.setSelectedItem(rs.getString(5));
                 jDateChooser4.setDate(rs.getDate(6));
                 
                  byte[] images = rs.getBytes(8);
                if(images != null){
                ImageIcon originalIcon = new ImageIcon(images);
                Image scaledImage = originalIcon.getImage().getScaledInstance(jLabel31.getWidth(), jLabel31.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                jLabel31.setIcon(scaledIcon);
                }else{
                    jLabel31.setIcon(null);
                }
                
    
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }             // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
 try {

             String productNo= jTextField5.getText();
            String productName = jTextField6.getText();
            Date manuDate = jDateChooser3.getDate();
            java.sql.Date sqlmanuDate = new java.sql.Date(manuDate.getTime());
            
            String manufacturers = jComboBox3.getSelectedItem().toString();
            String quantities = jComboBox4.getSelectedItem().toString();
            Date expDate = jDateChooser4.getDate();
            java.sql.Date sqlexpDate = new java.sql.Date(expDate.getTime());
            String price = jTextField3.getText();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306 " + "/exam", "root", "KAROL2005");
            PreparedStatement ps = con.prepareStatement("UPDATE inventory set  productName=?, manuDate=?, manufacturers=?, quantities=?, expdate=?, price=?, photo=? where productNo= ? ");
            
            ps.setString(1, productName);
            ps.setDate(2, sqlmanuDate);
            ps.setString(3, manufacturers);
            ps.setString(4, quantities);
            ps.setDate(5, sqlexpDate);
            ps.setString(6, price);
            ps.setBytes(7, photo);
            ps.setString(8, productNo);

             int rowsUpdated
                    = ps.executeUpdate();
             
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(rootPane, "product info update successfully");
            } else {
                JOptionPane.showMessageDialog(rootPane, "No product found with this ID:" + productNo);
            }
            jTextField6.setText("");
            jTextField7.setText("");
            jDateChooser3.cleanup();
            jDateChooser4.cleanup();
            jLabel31.setIcon(null);

            jComboBox3.setSelectedIndex(0);
            jComboBox4.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);

        }         // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

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
            java.util.logging.Logger.getLogger(inventoryPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(inventoryPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(inventoryPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(inventoryPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new inventoryPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private com.toedter.calendar.JDateChooser jDateChooser4;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
