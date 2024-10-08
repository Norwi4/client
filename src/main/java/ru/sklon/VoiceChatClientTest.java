package ru.sklon;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
/**
 * @author Abaev Evgeniy
 */
public class VoiceChatClientTest {
    private JComboBox<String> microphoneList;
    private JButton connectButton, disconnectButton;
    private Socket socket;
    private OutputStream outputStream;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    public VoiceChatClientTest() {
        JFrame frame = new JFrame("Voice Chat Client");
        microphoneList = new JComboBox<>(getMicrophoneNames());
        connectButton = new JButton("Подключиться");
        disconnectButton = new JButton("Отключиться");

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnectFromServer();
            }
        });

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(microphoneList);
        frame.add(connectButton);
        frame.add(disconnectButton);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private String[] getMicrophoneNames() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        String[] micNames = new String[mixers.length];

        for (int i = 0; i < mixers.length; i++) {
            micNames[i] = mixers[i].getName();
        }

        return micNames;
    }

    private void connectToServer() {
        try {
            String serverAddress = "localhost"; // Замените на адрес вашего сервера
            socket = new Socket(serverAddress, 6789);
            outputStream = socket.getOutputStream();

            audioFormat = new AudioFormat(44100, 16, 2, true, true);
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Запуск потока для передачи аудио
            new Thread(this::sendAudio).start();

            System.out.println("Подключено к серверу");
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void sendAudio() {
        byte[] buffer = new byte[4096];

        try {
            while (true) {
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnectFromServer() {
        try {
            targetDataLine.stop();
            targetDataLine.close();
            outputStream.close();
            socket.close();
            System.out.println("Отключено от сервера");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VoiceChatClientTest::new);
    }
}
