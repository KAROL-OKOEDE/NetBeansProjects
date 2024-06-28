/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ourchatapplicationproject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import static java.time.Clock.system;

/**
 *
 * @author LENOVO
 */
public class OnlineListThread implements Runnable {

    Server serverform;

    public OnlineListThread(Server serverform) {
        this.serverform = serverform;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                String msg = "";
                for (int x = 0; x < serverform.clientList.size(); x++) {
                    msg = msg + " " + serverform.clientList.elementAt(x);
                }
                for (int x = 0; x < serverform.clientList.size(); x++) {
                    Socket tsoc = (Socket) serverform.socketList.elementAt(x);
                    DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());

                    if (msg.length() > 0) {
                        dos.writeUTF("CMD_ONLINE" + msg);
                        system.out.println("CMD_ONLINE" + msg);
                    }
                }
                Thread.sleep(1900);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        }
    }

}
