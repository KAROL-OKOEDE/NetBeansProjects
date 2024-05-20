/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pau_chat.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author New
 */
public class MessageAlert {

    String filename = null;
    InputStream music;
    File audiofile;
    AudioInputStream song;
    Clip clip;

    void TextMsg() {
        try {
            audiofile = new File("C:\\Users\\LENOVO\\Documents\\NetBeansProjects\\PAU_CHAT\\src\\pau_chat\\Client\\2.wav"); 
            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    void FileMsg() {
        try {
            audiofile = new File("C:\\Users\\LENOVO\\Documents\\NetBeansProjects\\PAU_CHAT\\src\\pau_chat\\Client\\1.wav"); 

            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
       
    void PrivateMsg() {
        try {
            audiofile = new File("C:\\Users\\LENOVO\\Documents\\NetBeansProjects\\PAU_CHAT\\src\\pau_chat\\Client\\3.wav"); 

            music = new FileInputStream(audiofile);
            song = AudioSystem.getAudioInputStream(audiofile);
            clip = AudioSystem.getClip();
            clip.open(song);
            clip.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
