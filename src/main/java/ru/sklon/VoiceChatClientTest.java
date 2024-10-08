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
    private JTextField nicknameField;
    private JButton toggleButton;
    private Socket nicknameSocket;
    private Socket audioSocket;
    private OutputStream nicknameOutputStream;
    private OutputStream audioOutputStream;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean isConnected = false;

    public VoiceChatClientTest() {
        JFrame frame = new JFrame("Voice Chat Client");
        microphoneList = new JComboBox<>(getMicrophoneNames());
        nicknameField = new JTextField("Введите ваш ник", 15);
        toggleButton = new JButton("Подключиться");

        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    connectToServer();
                } else {
                    disconnectFromServer();
                }
            }
        });

        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(nicknameField);
        frame.add(microphoneList);
        frame.add(toggleButton);
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

            // Сначала подключаемся для отправки ника
            nicknameSocket = new Socket(serverAddress, 6789);
            nicknameOutputStream = nicknameSocket.getOutputStream();

            // Отправка ника на сервер
            String nickname = nicknameField.getText();
            nicknameOutputStream.write(nickname.getBytes());
            nicknameOutputStream.flush();

            // Теперь подключаемся для передачи аудио
            audioSocket = new Socket(serverAddress, 6790); // Порт для аудио
            audioOutputStream = audioSocket.getOutputStream();

            audioFormat = new AudioFormat(44100, 16, 1, true, true); // mono
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Запуск потока для передачи аудио
            new Thread(this::sendAudio).start();

            isConnected = true;
            toggleButton.setText("Отключиться");
            System.out.println("Подключено к серверу как: " + nickname);
        } catch (IOException | LineUnavailableException e) {
            System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
        }
    }

    private void sendAudio() {
        byte[] buffer = new byte[4096];

        try {
            while (isConnected) {
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    audioOutputStream.write(buffer, 0, bytesRead);
                    audioOutputStream.flush(); // Убедитесь, что данные отправлены
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отправке аудио: " + e.getMessage());
        }
    }

    private void disconnectFromServer() {
        try {
            isConnected = false;
            targetDataLine.stop();
            targetDataLine.close();

            // Закрываем потоки и сокеты
            audioOutputStream.close();
            audioSocket.close();

            nicknameOutputStream.close(); // Закрываем выходной поток ника
            nicknameSocket.close(); // Закрываем сокет ника

            toggleButton.setText("Подключиться");
            System.out.println("Отключено от сервера");
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VoiceChatClientTest::new);
    }
}
