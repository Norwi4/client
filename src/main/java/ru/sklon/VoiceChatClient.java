package ru.sklon;


import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.util.*;


import javax.sound.sampled.*;
/**
 * @author Abaev Evgeniy
 */
public class VoiceChatClient {
    private static final String SERVER_ADDRESS = "51.250.45.140"; // Измените на IP-адрес сервера
    private static final int PORT = 50005;
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        Mixer selectedMixer = selectMixer();
        new Thread(new AudioSender(socket, selectedMixer)).start();
        new Thread(new AudioReceiver(socket)).start();
    }
    private static Mixer selectMixer() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (int i = 0; i < mixers.length; i++) {
            System.out.println(i + ": " + mixers[i].getName());
        }

        System.out.print("Выберите номер микрофона: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        return AudioSystem.getMixer(mixers[choice]);
    }
}
