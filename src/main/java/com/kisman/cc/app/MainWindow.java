package com.kisman.cc.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow implements ActionListener {
    public static String d1 = "discord.com";
    public static String d2 = "api/webhooks";

    public JFrame frame;
    public JButton button = new JButton("Register");
    public JTextField text = new JTextField();
    public JLabel label = new JLabel("");
    public MainWindow(boolean visible) {
        frame = new JFrame("HWIDUtil");
        frame.setSize(100, 100);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        button.addActionListener(this);
        button.setSize(100, 100);
        frame.add(BorderLayout.CENTER, button);
//        frame.add(BorderLayout.CENTER, text);
//        frame.add(BorderLayout.NORTH, label);
        frame.setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Register")) {
            if(!text.getText().isEmpty()) {

            } else {

            }
        }
    }
}
