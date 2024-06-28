package ultimatechatapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO
 */
public class AudioThread implements Runnable {

    ServerSocket server;
    Server serverform;
    boolean StartServer = true;
    int port;

    public AudioThread(int port, Server serverform) {
        this.port = port;

        try {
            this.serverform = serverform;
            
            server = new ServerSocket(port);
            
            serverform.appendMessage("Server is running. !\n");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            while (StartServer) {
                Socket socket = server.accept();
                
                serverform.jTextArea1.append("Client connected: ");

                try {
// Set up audio capture
                    AudioFormat captureFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                    DataLine.Info captureInfo = new DataLine.Info(TargetDataLine.class, captureFormat);
                    TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(captureInfo);
                    targetLine.open(captureFormat);
                    targetLine.start();

// Set up audio playback
                    AudioFormat playbackFormat = new AudioFormat(8000.0f, 16, 1, true, true);
                    DataLine.Info playbackInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
                    SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(playbackInfo);
                    sourceLine.open(playbackFormat);
                    sourceLine.start();

                    while (!Thread.interrupted()) {

                        OutputStream outputStream = socket.getOutputStream();
                        InputStream inputStream = socket.getInputStream();

                        byte[] usernameBytes = new byte[1024];
                        int bytesRead = inputStream.read(usernameBytes);
                        String username = new String(usernameBytes, 0, bytesRead);
                        serverform.jTextArea1.append(username + " connected\n");

                        Thread clientThread = new Thread(() -> {

                            try {
                                byte[] buffer = new byte[1024];
                                while (true) {
                                    int bytesRead1 = inputStream.read(buffer);

                                    sourceLine.write(buffer, 0, bytesRead1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        );
                        clientThread.start();
                        Thread sendThread = new Thread(() -> {

                            try {
                                byte[] buffer = new byte[1024];
                                while (true) {

                                    int bytesRead1 = targetLine.read(buffer, 0, buffer.length);
                                    outputStream.write(buffer, 0, bytesRead1);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        );
                        sendThread.start();
                    }
                } catch (LineUnavailableException | IOException e) {
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
            StartServer = false;
            JOptionPane.showMessageDialog(null, "Server is now closed..!");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
