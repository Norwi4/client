package ru.sklon;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
/**
 * @author Abaev Evgeniy
 */
public class AudioReceiver implements Runnable {
    private Socket socket;
    public AudioReceiver(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            byte[] buffer = new byte[4096];
            InputStream in = socket.getInputStream();

            // Настройка формата аудио для воспроизведения
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            while (true) {
                int bytesRead = in.read(buffer);
                if (bytesRead == -1) break; // Конец потока
                line.write(buffer, 0, bytesRead); // Воспроизведение полученных данных
            }
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
