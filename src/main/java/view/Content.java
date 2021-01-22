package view;

import javax.swing.*;
import java.awt.*;

public class Content extends JPanel {

    JLabel champNameLabel = new JLabel();
    JLabel progress = new JLabel();
    JLabel listOfRunes = new JLabel();


    public Content() {
        this.setBackground(Color.decode("#90be6d"));
        this.setPreferredSize(new Dimension(250, 350));

        GridBagLayout gr = new GridBagLayout();
        this.setLayout(gr);

        /*
        champ portrait/name - panel
        progress - textfield
        progress - panel
         */
        makeChampPic();
        makeProgress();
        makeRunes();

    }

    private void makeRunes() {
        JPanel runes = new JPanel();
        runes.setPreferredSize(new Dimension(200, 200));
        GridBagConstraints runesCons = new GridBagConstraints();

        listOfRunes.setPreferredSize(new Dimension(180, 180));

        runesCons.gridwidth = 2;

        runesCons.gridx = 0;
        runesCons.gridy = 1;

        runesCons.insets = new Insets(10, 10, 10, 10);

        runes.add(listOfRunes);

        this.add(runes, runesCons);
    }

    private void makeProgress() {
        progress = new JLabel("waiting for League client...");
        GridBagConstraints progressCons = new GridBagConstraints();

        progressCons.gridx = 1;
        progressCons.gridy = 0;

        this.add(progress, progressCons);
    }

    private void makeChampPic() {
        JPanel champPicPanel;
        champPicPanel = new JPanel();

        champPicPanel.setPreferredSize(new Dimension(100, 100));
        GridBagConstraints champCons = new GridBagConstraints();

        champCons.gridx = 0;
        champCons.gridy = 0;

        champNameLabel.setPreferredSize((new Dimension(100, 100)));

        champPicPanel.add(champNameLabel);

        this.add(champPicPanel, champCons);
    }


    public JLabel getChampNameLabel() {
        return champNameLabel;
    }

    public JLabel getProgress() {
        return progress;
    }

    public JLabel getListOfRunes() {
        return listOfRunes;
    }
}
