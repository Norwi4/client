package ru.sklon;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;

/**
 * @author Abaev Evgeniy
 */
public class AudioSender implements Runnable {
    private Socket socket; //asdf
    private Mixer mixer;
    public AudioSender(Socket socket, Mixer mixer) {
        this.socket = socket;
        this.mixer = mixer;
    }
    @Override
    public void run() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) mixer.getLine(info); // Используем выбранный микрофон
            line.open(format);
            line.start();

            byte[] buffer = new byte[4096];
            OutputStream out = socket.getOutputStream();

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length);
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
