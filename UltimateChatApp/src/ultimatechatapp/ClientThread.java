package ultimatechatapp;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.*;
import javax.swing.JOptionPane;

public class ClientThread implements Runnable {

    Socket sok;
    //Socket soc;
    DataInputStream dis;
    DataOutputStream dos;
    Client clientFrame;
    StringTokenizer token;
//     MessageAlert alert;
    private TargetDataLine targetLine;
    private SourceDataLine sourceLine;
    private Thread captureThread;
    private Thread playbackThread;
    private Thread videoCaptureThread;
    private Thread videoPlaybackThread;

    Login loginform;
    private OutputStream outputStream;
    private InputStream inputStream;
    register registerform;
    private static Webcam webcam;
    private static boolean running;

    public ClientThread(Socket socket, Client ClientFrame, Login loginform) {
        this.clientFrame = ClientFrame;
        this.loginform = loginform;
        this.sok = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {

        }
    }

    public ClientThread(Socket socket, Client ClientFrame, register registerform) {
        this.clientFrame = ClientFrame;
        this.registerform = registerform;
        this.sok = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String data = dis.readUTF();
                token = new StringTokenizer(data);
                /**
                 * Get Message CMD *
                 */
                String CMD = token.nextToken();

                switch (CMD) {
                    case "CMD_LOGINCONFIRMED":
                        String Username = token.nextToken();
                        String socket = token.nextToken();
                        clientFrame.setVisible(true);
                        clientFrame.username = Username;
                        clientFrame.socket = this.sok;
                        clientFrame.setTitle(Username);
                        loginform.dispose();
                        JOptionPane.showMessageDialog(clientFrame, "Welcome " + Username);
                        break;
                    case "CMD_LOGINNOTCONFIRMED":
                        JOptionPane.showMessageDialog(loginform, "Wrong Login Details");
                        break;
                    case "CMD_REGISTERCONFIRMED":
                        JOptionPane.showMessageDialog(registerform, "Registration Successful");
                        break;

                    case "CMD_REGISTRATIONNOTCONFIRMED":
                        JOptionPane.showMessageDialog(registerform, "Issue with Registration");
                        break;

                    case "CMD_ONLINE":
                        Vector online = new Vector();
                        while (token.hasMoreTokens()) {
                            String list = token.nextToken();

                            if (!list.equalsIgnoreCase(clientFrame.username)) {

                                online.add(list);

                            }
                        }
                        System.out.println(online);

                        clientFrame.appendOnlineList(online);
                        break;

                    case "CMD_CHAT":

                        String msgs = "";
                        String from = token.nextToken();
                        while (token.hasMoreTokens()) {
                            msgs = msgs + " " + token.nextToken();
                        }

                        String active = clientFrame.jLabel2.getText();
                        // JOptionPane.showMessageDialog(clientFrame, "Host " + clientFrame.username + " from" + from + " active" + active);
                        if (active.equalsIgnoreCase(from)) {
                            clientFrame.appendMessage("\n" + from + ": " + msgs);
                        }
                        break;

                    case "CMD_FETCHMSG":

                        String msg = "";

                        while (token.hasMoreTokens()) {
                            msg = msg + " " + token.nextToken();
                        }
                        clientFrame.appendMessage(msg + "\n");

                        break;

                    //  This will inform the client that there's a file receive, Accept or Reject the file  
                    case "CMD_FILE_XD":  // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                        String sender = token.nextToken();
                        String receiver = token.nextToken();
                        String fname = token.nextToken();

                        int confirm = JOptionPane.showConfirmDialog(clientFrame,
                                "From: " + sender + "\nFilename: " + fname + "\nwould you like to Accept.?");
//                        alert = new MessageAlert();
//                        alert.FileMsg();
                        if (confirm == 0) {

                            clientFrame.openFolder();
                            try {

                                dos = new DataOutputStream(sok.getOutputStream());
                                // Format:  CMD_SEND_FILE_ACCEPT [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ACCEPT " + sender + " accepted";
                                dos.writeUTF(format);

                                /*  this will create a filesharing socket to handle incoming file
                                and this socket will automatically closed when it's done.  */
                                Socket fSoc = new Socket(clientFrame.getMyHost(), clientFrame.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                fdos.writeUTF("CMD_SHARINGSOCKET " + clientFrame.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSoc, clientFrame)).start();
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        } else { // client rejected the request, then send back result to sender
                            try {

                                dos = new DataOutputStream(sok.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR " + sender + " Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        }
                        break;
//  This will inform the client that there's a file receive, Accept or Reject the file  
                    case "CMD_AUDIO_XD":  // Format:  CMD_AUDIO_XD [sender] [receiver]
                        sender = token.nextToken();
                        receiver = token.nextToken();

                        confirm = JOptionPane.showConfirmDialog(clientFrame,
                                "From: " + sender + "\nWould you like to accept?");

                        if (confirm == 0) {
                            try {
                                // Setup audio format
                                AudioFormat captureFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                                DataLine.Info captureInfo = new DataLine.Info(TargetDataLine.class, captureFormat);
                                targetLine = (TargetDataLine) AudioSystem.getLine(captureInfo);
                                targetLine.open(captureFormat);
                                targetLine.start();

                                AudioFormat playbackFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                                DataLine.Info playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
                                sourceLine = (SourceDataLine) AudioSystem.getLine(playbackInfo);
                                sourceLine.open(playbackFormat);
                                sourceLine.start();

                                // Setup socket connection
                                sok = new Socket("localhost", 1245);  // Replace with actual server address and port
                                outputStream = sok.getOutputStream();
                                inputStream = sok.getInputStream();

                                // Create and start capture thread
                                captureThread = new Thread(() -> {
                                    try {
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int bytesRead = targetLine.read(buffer, 0, buffer.length);
                                            outputStream.write(buffer, 0, bytesRead);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                captureThread.start();

                                // Create and start playback thread
                                playbackThread = new Thread(() -> {
                                    try {
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int bytesRead = inputStream.read(buffer);
                                            sourceLine.write(buffer, 0, bytesRead);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                playbackThread.start();

                                // Create and show the audio call control frame
                                JFrame controlFrame = new JFrame("Audio Call with " + sender);
                                controlFrame.setSize(300, 200);
                                controlFrame.setLayout(new BoxLayout(controlFrame.getContentPane(), BoxLayout.Y_AXIS));

                                JButton muteButton = new JButton("Mute");
                                JButton endCallButton = new JButton("End Call");

                                controlFrame.add(muteButton);
                                controlFrame.add(endCallButton);

                                boolean[] isMuted = {false};

                                muteButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (isMuted[0]) {
                                            targetLine.start();
                                            muteButton.setText("Mute");
                                        } else {
                                            targetLine.stop();
                                            muteButton.setText("Unmute");
                                        }
                                        isMuted[0] = !isMuted[0];
                                    }
                                });

                                endCallButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        captureThread.interrupt();
                                        playbackThread.interrupt();
                                        targetLine.close();
                                        sourceLine.close();
                                        try {
                                            dos.writeUTF("CMD_END_CALL " + sender);
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        controlFrame.dispose();
                                    }
                                });

                                controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                controlFrame.setVisible(true);

                            } catch (LineUnavailableException | IOException e) {
                                e.printStackTrace();
                            }

                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(sok.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR " + sender + " Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        }
                        break;

                    case "CMD_VIDEO_CALL":
                        sender = token.nextToken();
                        receiver = token.nextToken();

                        confirm = JOptionPane.showConfirmDialog(clientFrame,
                                "From: " + sender + "\nWould you like to accept?");

                        if (confirm == 0) {
                            try {
                                // Setup audio format
                                AudioFormat captureFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                                DataLine.Info captureInfo = new DataLine.Info(TargetDataLine.class, captureFormat);
                                targetLine = (TargetDataLine) AudioSystem.getLine(captureInfo);
                                targetLine.open(captureFormat);
                                targetLine.start();

                                AudioFormat playbackFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                                DataLine.Info playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
                                sourceLine = (SourceDataLine) AudioSystem.getLine(playbackInfo);
                                sourceLine.open(playbackFormat);
                                sourceLine.start();

                                // Setup socket connection
                                sok = new Socket("localhost", 5000);  // Replace with actual server address and port
                                outputStream = sok.getOutputStream();
                                inputStream = sok.getInputStream();

                                // Create and start capture thread for audio
                                captureThread = new Thread(() -> {
                                    try {
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int bytesRead = targetLine.read(buffer, 0, buffer.length);
                                            outputStream.write(buffer, 0, bytesRead);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                captureThread.start();

                                // Create and start playback thread for audio
                                playbackThread = new Thread(() -> {
                                    try {
                                        byte[] buffer = new byte[1024];
                                        while (true) {
                                            int bytesRead = inputStream.read(buffer);
                                            sourceLine.write(buffer, 0, bytesRead);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                playbackThread.start();

                                // Setup webcam for video capture
                                webcam = Webcam.getDefault();
                                webcam.setViewSize(WebcamResolution.VGA.getSize());
                                WebcamPanel webcamPanel = new WebcamPanel(webcam);

                                // Create and start capture thread for video
                                videoCaptureThread = new Thread(() -> {
                                    try {
                                        while (true) {
                                            BufferedImage image = webcam.getImage();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            ImageIO.write(image, "jpg", baos);
                                            byte[] buffer = baos.toByteArray();
                                            outputStream.write(buffer);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                videoCaptureThread.start();

                                // Create and start playback thread for video
                                videoPlaybackThread = new Thread(() -> {
                                    try {
                                        while (true) {
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            byte[] buffer = new byte[4096];
                                            int bytesRead;
                                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                baos.write(buffer, 0, bytesRead);
                                            }
                                            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                                            BufferedImage image = ImageIO.read(bais);
                                            Graphics g = webcamPanel.getGraphics();
                                            g.drawImage(image, 0, 0, webcamPanel.getWidth(), webcamPanel.getHeight(), null);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                videoPlaybackThread.start();

                                // Create and show the call control frame
                                JFrame callFrame = new JFrame("Video Call with " + sender);
                                callFrame.setSize(800, 600);
                                callFrame.setLayout(new BorderLayout());

                                JPanel controlPanel = new JPanel();
                                controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

                                JButton muteButton = new JButton("Mute");
                                JButton endCallButton = new JButton("End Call");

                                controlPanel.add(muteButton);
                                controlPanel.add(endCallButton);

                                callFrame.add(webcamPanel, BorderLayout.CENTER);
                                callFrame.add(controlPanel, BorderLayout.SOUTH);

                                boolean[] isMuted = {false};

                                muteButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        if (isMuted[0]) {
                                            targetLine.start();
                                            muteButton.setText("Mute");
                                        } else {
                                            targetLine.stop();
                                            muteButton.setText("Unmute");
                                        }
                                        isMuted[0] = !isMuted[0];
                                    }
                                });

                                endCallButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        captureThread.interrupt();
                                        playbackThread.interrupt();
                                        videoCaptureThread.interrupt();
                                        videoPlaybackThread.interrupt();
                                        targetLine.close();
                                        sourceLine.close();
                                        webcam.close();
                                        try {
                                            DataOutputStream dos = new DataOutputStream(outputStream);
                                            dos.writeUTF("CMD_END_CALL " + sender);
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        callFrame.dispose();
                                    }
                                });

                                callFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                callFrame.setVisible(true);

                            } catch (LineUnavailableException | IOException e) {
                                e.printStackTrace();
                            }

                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(sok.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR " + sender + " Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: " + e.getMessage());
                            }
                        }
                        break;

                    case "CMD_NOTIFICATION":

                        msg = "";

                        while (token.hasMoreTokens()) {
                            msg = msg + " " + token.nextToken();
                        }
                        clientFrame.appendNotification(msg + "\n");

                        break;
                    default:
                        clientFrame.appendMessage("[CMDException]: Unknown Command " + CMD);
                        break;

                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
