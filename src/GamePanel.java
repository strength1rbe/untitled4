import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
public  class GamePanel extends JPanel {
    static final int screen_height = 800;
    static final int screen_width = 700;
    static int unit_size =100;
    ArrayList<Enemies> enemies;
    static ArrayList<Bullet> bullets;
    Jet jet ;
    Controller controller = new Controller();
    Thread gameLoopThread;
    Thread shootingThread;
    volatile boolean running = true;
    Image backroundImage;
    infoPanel infoPanel;
    GamePanel()  {
        this.setPreferredSize(new Dimension(screen_width,screen_height));
        this.setBackground(Color.CYAN);
        this.setLayout(null);
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(controller);
        jet = new Jet(this);
        infoPanel = new infoPanel(this);
        backroundImage = new ImageIcon("space_backround2.png").getImage();
        bullets = new ArrayList<>();
        startGame();
    }
    public void startGame() {
        spawnEnemies();
        startGameLoop();
        startShooting();

    }
    public void startGameLoop() {
        gameLoopThread = new Thread(() -> {
            while (running) {
                synchronized (bullets) {
                    for (int i = bullets.size() - 1; i >= 0; i--) {
                        Bullet bullet = bullets.get(i);
                        bullet.move();
                        if (!bullet.visible) {
                            bullets.remove(i);
                        }
                    }
                }
                checkCollisions();
                repaint();
                if (enemies.isEmpty()) {
                    endGame();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameLoopThread.start();
    }
    public void startShooting() {
        System.out.println();
        shootingThread = new Thread(() -> {
            while (running) {
                jet.shootBullet();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        shootingThread.start();
    }
    public void spawnEnemies(){
        enemies = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Enemies enemy = new Enemies(this,i*unit_size,100);
            this.add(enemy.enemy_label);
            enemies.add(enemy);
            enemy.startMoving(i*unit_size,unit_size);
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        g.drawImage(backroundImage,0,100,null);
//        for (int i = 0; i < screen_width/unit_size; i++) {
//            g.drawLine(i*unit_size,0,i*unit_size,screen_height);
//        }
//        for (int i = 0; i < screen_height/unit_size; i++) {
//            g.drawLine(0,i*unit_size,screen_width,i*unit_size);
//        }
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }
    public void endGame() {
        displayEndGamePanel();
    }
    public void closeWindow(){
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose();
    }
    public void displayEndGamePanel() {
        JPanel endGamePanel = new JPanel();
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.Y_AXIS));
        endGamePanel.setBounds(screen_width / 4, screen_height / 4, screen_width / 2, screen_height / 2);
        endGamePanel.setBackground(Color.LIGHT_GRAY);

        JLabel endLabel = new JLabel("Game Over");
        endLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton replayButton = new JButton("Replay");
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Restart the game
                running = false; // Stop current game loop
                Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
                window.dispose(); // Close current window
                JFrame newGameFrame = new JFrame("Jet Game");
                newGameFrame.add(new GamePanel());
                newGameFrame.pack();
                newGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                newGameFrame.setVisible(true);
            }
        });

        JButton finishButton = new JButton("Finish");
        finishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        endGamePanel.add(Box.createRigidArea(new Dimension(0, 50)));
        endGamePanel.add(endLabel);
        endGamePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        endGamePanel.add(replayButton);
        endGamePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        endGamePanel.add(finishButton);

        this.setLayout(null);
        this.add(endGamePanel);
        this.revalidate();
        this.repaint();
    }

    public void checkCollisions() {
        synchronized (bullets) {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                Rectangle bulletBounds = new Rectangle(bullet.x, bullet.y, bullet.bullet_width, bullet.bullet_height);
                for (int j = 0; j < enemies.size(); j++) {
                    Enemies enemy = enemies.get(j);
                    Rectangle enemyBounds = new Rectangle(enemy.enemy_label.getBounds());
                    if (bulletBounds.intersects(enemyBounds)) {
                        bullet.visible = false;
                        enemies.get(j).life--;
                        enemies.get(j).enemy_label.setText("" + enemies.get(j).life);
                        if (enemies.get(j).life <= 0) {
                            this.remove(enemy.enemy_label);
                            enemies.remove(j);
                            j--;
                            jet.score+=100;
                            infoPanel.score.setText("score "+jet.score);
                        }
                    }
                }
            }
        }
        synchronized (enemies){
            for (int j = 0; j < enemies.size(); j++) {
                Enemies enemy = enemies.get(j);
                Rectangle enemyBounds = new Rectangle(enemy.enemy_label.getBounds());
                Rectangle jetBounds = new Rectangle(Jet.Jet_label.getBounds());
                if (enemyBounds.intersects(jetBounds)) {
                    this.remove(enemy.enemy_label);
                    enemies.remove(j);
                    j--;
                    jet.life--;
                    if(jet.score >=50){
                        jet.score -= 50;
                    }
                    infoPanel.score.setText("score "+jet.score);
                    infoPanel.life.setText("life  "+jet.life);
                } else if (enemy.enemy_Y>700) {
                    this.remove(enemy.enemy_label);
                    enemies.remove(j);
                    j--;
                    jet.life--;
                    if(jet.score >=50){
                        jet.score -= 50;
                    }
                    infoPanel.score.setText("score "+jet.score);
                    infoPanel.life.setText("life  "+jet.life);
                }
                if(jet.life<=0){
                    endGame();
                }
            }
        }
    }
    //////////////////////////////////////////////////////
////////////////////////////////////////////////////// sub_class
//////////////////////////////////////////////////////
    class Controller implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {

        }
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_RIGHT:
                    if(Jet.Jet_label.getX()+GamePanel.unit_size+100 <= GamePanel.screen_width){
                        Jet.Jet_label.setLocation(Jet.Jet_label.getX() + 100, Jet.Jet_label.getY());
                    }break;
                case KeyEvent.VK_LEFT:
                    if(Jet.Jet_label.getX()-100 >= 0){
                        Jet.Jet_label.setLocation(Jet.Jet_label.getX() -100, Jet.Jet_label.getY());
                    }break;
//                case KeyEvent.VK_SPACE:
//                    jet.shootBullet();
//                    break;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
    /////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////// sub_class
/////////////////////////////////////////////////////////////
    public class Jet {
        int jetX = (GamePanel.screen_width / GamePanel.unit_size / 2) * (GamePanel.unit_size);
        int jetY = GamePanel.screen_height - GamePanel.unit_size;
        static JLabel Jet_label;
        JLabel sss;
        static ImageIcon jet_Image;
        int bullet_stack = 7;
        int life = 3;
        int score = 0;

        Jet(GamePanel panel) {
            Jet_label = new JLabel();
            jet_Image = new ImageIcon("jet.png");
            Jet_label.setIcon(jet_Image);
            Jet_label.setBounds(jetX, jetY, GamePanel.unit_size, GamePanel.unit_size);
            panel.add(Jet_label);
        }

        public void shootBullet() {
            synchronized (bullets) {
                if (bullets.size() < bullet_stack) {
                    int bulletX = Jet.Jet_label.getX() + GamePanel.unit_size / 2 - 5;
                    int bulletY = Jet.Jet_label.getY();
                    Bullet bullet = new Bullet(bulletX, bulletY);
                    bullets.add(bullet);
                }
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////sub_class
///////////////////////////////////////////////////////////////////////////////////////////////////
    public class Bullet {
        int x;
        int y;
        boolean visible;
        int bullet_speed = 6 ;
        int bullet_width = 10;
        int bullet_height = 20;
        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
            visible = true;
        }
        public void move() {
            y -= bullet_speed;
            if (y < 100) {
                visible = false;
            }
        }
        public void draw(Graphics g) {
            if (visible) {
                g.setColor(Color.red);
                g.fillRect(x, y, bullet_width, bullet_height);
            }
        }
    }
    //////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////sub_class
//////////////////////////////////////////////////////////////////////
    public class Enemies {
        int enemy_X = GamePanel.unit_size/4;
        int enemy_Y;
        JLabel enemy_label;
        static ImageIcon enemy_Image;
        int life = 3;
        //GamePanel panel;  ////////////////////////////////  impo
        volatile boolean moving = true;
        Enemies(GamePanel panel,int x,int y){
            //this.panel = panel;    /////////////////////////////  impo
            enemy_label = new JLabel();
            enemy_label.setText(""+life);
            enemy_label.setForeground(Color.red);
            enemy_Image = new ImageIcon("enemy_jet.png");
            enemy_label.setIcon(enemy_Image);
            enemy_label.setBounds(x+enemy_X,y,GamePanel.unit_size-25,GamePanel.unit_size-25);
        }
        public void startMoving(int x,int y) {
            new Thread(() -> {
                try {
                    int delay = 3000 + (int)(Math.random() * 5000);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (moving ) {
                    move(x,y);
                    try {
                        Thread.sleep(14);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        public void move(int x,int y) {
            enemy_Y += 2;
            enemy_label.setLocation(enemy_X+x, enemy_Y+y);
        }
    }
    public  class infoPanel{

        JLabel score;
        JLabel life;
        ImageIcon heart;
        infoPanel(GamePanel panel){
            score = new JLabel();
            life = new JLabel();
            life.setBounds(200,25,60,50);
            life.setText("life  "+jet.life);
            heart = new ImageIcon("heart_icon.png");
            life.setIcon(heart);
            panel.add(life);
            score.setBounds(350,25,60,50);
            score.setText("score "+jet.score);
            panel.add(score);
        }

    }
}

