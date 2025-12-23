import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Class chính điều khiển luồng Game
 * Chứa vòng lặp game, xử lý va chạm và input
 */
public class SpaceShooterGame extends JPanel implements ActionListener, KeyListener {

    // Kích thước màn hình (Public static để các class con truy cập)
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    // Trạng thái game
    private boolean isRunning = false;
    private boolean isGameOver = false;
    private int score = 0;
    private Timer gameLoop;

    // Danh sách đối tượng
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private ArrayList<Star> stars;
    private Random random;

    // Bộ đếm thời gian spawn quái
    private int enemySpawnTimer = 0;

    public SpaceShooterGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        random = new Random();
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        stars = new ArrayList<>();

        // Tạo 50 ngôi sao nền
        for (int i = 0; i < 50; i++) {
            stars.add(new Star(random.nextInt(WIDTH), random.nextInt(HEIGHT)));
        }

        // Tạo Player
        player = new Player(WIDTH / 2 - 25, HEIGHT - 100);

        // Game Loop (khoảng 60 FPS)
        gameLoop = new Timer(16, this);
        startGame();
    }

    public void startGame() {
        isRunning = true;
        isGameOver = false;
        score = 0;
        bullets.clear();
        enemies.clear();
        player.setX(WIDTH / 2 - 25);
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Vẽ nền sao
        for (Star s : stars) s.draw(g2d);

        // 2. Vẽ game hoặc màn hình kết thúc
        if (isRunning) {
            player.draw(g2d);
            for (Bullet b : bullets) b.draw(g2d);
            for (Enemy e : enemies) e.draw(g2d);
            drawUI(g);
        } else if (isGameOver) {
            drawGameOver(g);
        }
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
    }

    private void drawGameOver(Graphics g) {
        String msg = "GAME OVER";
        String scoreMsg = "Final Score: " + score;
        String restartMsg = "Press ENTER to Restart";

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (WIDTH - fm.stringWidth(msg)) / 2, HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        fm = g.getFontMetrics();
        g.drawString(scoreMsg, (WIDTH - fm.stringWidth(scoreMsg)) / 2, HEIGHT / 2 + 10);

        g.setFont(new Font("Arial", Font.ITALIC, 20));
        fm = g.getFontMetrics();
        g.drawString(restartMsg, (WIDTH - fm.stringWidth(restartMsg)) / 2, HEIGHT / 2 + 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            updateGame();
            repaint();
        }
    }

    private void updateGame() {
        // Cập nhật sao nền
        for (Star s : stars) s.update();

        // Cập nhật người chơi
        player.update();

        // Cập nhật và xóa đạn ra khỏi màn hình
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            b.update();
            if (b.getY() < 0) bIter.remove();
        }

        // Sinh kẻ địch
        enemySpawnTimer++;
        if (enemySpawnTimer > 40) {
            enemies.add(new Enemy(random.nextInt(WIDTH - 40), -40));
            enemySpawnTimer = 0;
        }

        // Cập nhật kẻ địch và xử lý va chạm
        Iterator<Enemy> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Enemy enemy = eIter.next();
            enemy.update();

            // Va chạm: Địch - Người chơi
            if (enemy.getBounds().intersects(player.getBounds())) {
                isRunning = false;
                isGameOver = true;
                gameLoop.stop();
            }

            // Va chạm: Địch - Đạn
            Iterator<Bullet> bulletIter = bullets.iterator();
            while (bulletIter.hasNext()) {
                Bullet b = bulletIter.next();
                if (enemy.getBounds().intersects(b.getBounds())) {
                    score += 10;
                    bulletIter.remove(); // Xóa đạn
                    eIter.remove();      // Xóa địch
                    break;
                }
            }

            // Xóa địch nếu rơi quá màn hình (và chưa bị bắn chết)
            if (enemy.getY() > HEIGHT) {
                if(enemies.contains(enemy)) {
                    eIter.remove();
                }
            }
        }
    }

    // --- Xử lý phím ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) player.setLeft(true);
        if (key == KeyEvent.VK_RIGHT) player.setRight(true);

        if (key == KeyEvent.VK_SPACE) {
            if (player.canShoot()) {
                bullets.add(new Bullet(player.getX() + 22, player.getY()));
                player.resetCooldown();
            }
        }

        if (key == KeyEvent.VK_ENTER && isGameOver) {
            startGame();
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) player.setLeft(false);
        if (key == KeyEvent.VK_RIGHT) player.setRight(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // --- MAIN METHOD ĐỂ CHẠY GAME ---
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter OOP - Java Project");
        SpaceShooterGame gamePanel = new SpaceShooterGame();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}