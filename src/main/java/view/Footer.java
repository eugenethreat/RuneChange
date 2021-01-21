package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Footer extends JPanel {

    public Footer() {
        this.setBackground(Color.decode("#EA9010"));
        this.setPreferredSize(new Dimension(250, 50));

        JLabel contactInfo = new JLabel("made by eugenethreat@github.com");
        contactInfo.setBorder(new EmptyBorder(10, 0, 0, 0));
        contactInfo.setFont(new Font("Helvetica", Font.PLAIN, 14));
        contactInfo.setForeground(Color.decode("#1A1A0F"));

        this.add(contactInfo);

    }

}
