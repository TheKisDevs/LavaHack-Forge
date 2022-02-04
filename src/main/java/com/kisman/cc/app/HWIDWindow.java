package com.kisman.cc.app;

import com.kisman.cc.util.protect.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class HWIDWindow {
    public static Frame frame = new Frame();

    public static void init() {
        frame.setVisible(true);
//        throw new NoStackTraceThrowable("Verification was unsuccessful!");
    }

    public static class Frame extends JFrame {
        public Frame() {
            this.setTitle("Verification failed.");
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(null);
            copyToClipboard();
            String message = "Sorry, you are not on the HWID list." + "\n" + "HWID: " + HWID.getHWID() + "\n(Copied to clipboard.)";
            JOptionPane.showMessageDialog(this, message, "Could not verify your HWID successfully.", JOptionPane.PLAIN_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));
        }

        public static void copyToClipboard() {
            StringSelection selection = new StringSelection(HWID.getHWID());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }
}
