import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {
    static final int screen_height = 700;
    static final int screen_width = 700;
    Image backroundImage;
    public Menu(){
        this.setPreferredSize(new Dimension(screen_width,screen_height));
        this.setLayout(null);
        backroundImage = new ImageIcon("space_backround1.png").getImage();
        JButton start = new JButton();
        start.setBounds(350,300,150,80);
        start.setText("Start");
        start.addActionListener(e -> {// e , (event)
            closeWindow();
            new GameFrame(new GamePanel());
        });
        this.add(start);
        JButton info = new JButton();
        info.setBounds(200,300,150,80);
        info.setText("?");
        info.addActionListener(e ->{
            closeWindow();
            new GameFrame(new infoPage());
        });
        this.add(info);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        g.drawImage(backroundImage,0,0,null);
    }
    public void closeWindow(){
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose();
    }


}
