package ultimatechatapp;

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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author LENOVO
 */
public class VideoThread implements Runnable {

    Socket sok;
    private ServerSocket server;
    private Server serverform;
    private boolean startServer = true;
    private int port;
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

    public VideoThread(int port, Server serverform) {
        this.port = port;
        this.serverform = serverform;

        try {
            server = new ServerSocket(port);
            serverform.appendMessage("Server is running. !\n" + port);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            while (startServer) {
                Socket socket = server.accept();
                serverform.jTextArea1.append("Client connected: ");

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

//                    // Setup socket connection
//                    sok = new Socket(port);  // Replace with actual server address and port
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
                    JFrame callFrame = new JFrame("Video Call with client");
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
//                            try {
//                                DataOutputStream dos = new DataOutputStream(outputStream);
////                                dos.writeUTF("CMD_END_CALL " + sender);
//                            } catch (IOException ex) {
//                                ex.printStackTrace();
//                            }
                            callFrame.dispose();
                        }
                    });

                    callFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    callFrame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Thread(new SocketThread(socket, serverform)).start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void stop() {
        try {
            server.close();
            startServer = false;
            JOptionPane.showMessageDialog(null, "Server is now closed..!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
