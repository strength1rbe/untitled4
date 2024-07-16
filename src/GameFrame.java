import javax.swing.*;

public class GameFrame extends JFrame {

    GameFrame(JPanel panel) {
        this.setTitle("Fighter Jet");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        //////////////////////////////
        this.add(panel);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }
}
