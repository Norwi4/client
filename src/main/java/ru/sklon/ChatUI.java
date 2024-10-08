package ru.sklon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * @author Abaev Evgeniy
 */
public class ChatUI extends JFrame {
    private JButton startButton;
    private JButton stopButton;

    public ChatUI() {
        setTitle("Voice Chat");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        startButton = new JButton("Начать");
        stopButton = new JButton("Остановить");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логика для начала передачи голоса
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Логика для остановки передачи голоса
            }
        });

        add(startButton);
        add(stopButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatUI::new);
    }
}
