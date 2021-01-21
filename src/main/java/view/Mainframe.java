package view;


import javax.swing.*;
import java.awt.*;

public class Mainframe extends JFrame {

    Header header;
    Content content;
    Footer footer;

    public Mainframe() {

        //Frame stuff (i'm stuff)
        this.setTitle("RuneChange");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(250, 450));

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        GridBagLayout gr = new GridBagLayout();
        this.setLayout(gr);

        GridBagConstraints headerCons = new GridBagConstraints();
        GridBagConstraints footerCons = new GridBagConstraints();
        GridBagConstraints contentCons = new GridBagConstraints();

        //header
        headerCons.gridy = 0;
        headerCons.fill = GridBagConstraints.HORIZONTAL;

        //content
        contentCons.gridy = 1;

        //footer
        footerCons.gridy = 2;


        //panels
        header = new Header();
        content = new Content();
        footer = new Footer();

        this.add(header, headerCons);
        this.add(content, contentCons);
        this.add(footer, footerCons);


        //finally...
        this.setVisible(true);
        pack();

    }


    public Header getHeader() {
        return header;
    }

    public Content getContent() {
        return content;
    }

    public Footer getFooter() {
        return footer;
    }

}
