package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Header extends JPanel {

    public Header(){

        //https://coolors.co/eaefbd-c9e3ac-90be6d-ea9010-37371f
        this.setBackground(Color.decode("#C9E3AC"));

        /*
         An alternative to other layouts, might be to put your panel with the GridLayout,
         inside another panel that is a FlowLayout. That way your spacing will be intact
         but will not expand across the entire available space.

        https://stackoverflow.com/questions/4699892/how-to-set-the-component-size-with-gridlayout-is-there-a-better-way
         */

        this.setPreferredSize(new Dimension(250, 50));

        GridLayout gr = new GridLayout(1,3);
        this.setLayout(gr);

        //upper left corner
        JLabel appName = new JLabel("RuneChange");
        appName.setBorder(new EmptyBorder(0, 25, 0, 0));
        appName.setFont(new Font("Helvetica", Font.PLAIN, 20));

        this.add(appName);

    }


}
