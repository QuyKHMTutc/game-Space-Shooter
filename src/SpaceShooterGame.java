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

    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;

    private boolean isRunning = false;
    private boolean isGameOver = false;
    private int score = 0;
    private Timer gameLoop;

    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Enemy> enemies;
    private ArrayList<Star> stars;
    private ArrayList<PowerUp> powerUps;
    private Random random;

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
        powerUps = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            stars.add(new Star(random.nextInt(WIDTH), random.nextInt(HEIGHT)));
        }

        player = new Player(WIDTH / 2 - 25, HEIGHT - 100);
        gameLoop = new Timer(16, this);
        startGame();
    }

    public void startGame() {
        isRunning = true;
        isGameOver = false;
        score = 0;
        bullets.clear();
        enemies.clear();
        powerUps.clear();
        player.setX(WIDTH / 2 - 25);
        // player = new Player(WIDTH / 2 - 25, HEIGHT - 100); // Uncomment nếu muốn reset level khi chết
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (Star s : stars) s.draw(g2d);

        if (isRunning) {
            player.draw(g2d);
            for (Bullet b : bullets) b.draw(g2d);
            for (Enemy e : enemies) e.draw(g2d);
            for (PowerUp p : powerUps) p.draw(g2d);
            drawUI(g2d);
        } else if (isGameOver) {
            drawGameOver(g);
        }
    }

    private void drawUI(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 40);

        g.setColor(Color.GREEN);
        g.drawString("Weapon Lv: " + player.getWeaponLevel(), 20, 70);

        if (player.getWeaponLevel() >= 5) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString("(MAX)", 170, 70);
        }
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
        for (Star s : stars) s.update();
        player.update();

        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            b.update();
            // Xóa đạn nếu bay ra khỏi màn hình (trên, trái, phải)
            if (b.getY() < 0 || b.getX() < 0 || b.getX() > WIDTH) {
                bIter.remove();
            }
        }

        Iterator<PowerUp> pIter = powerUps.iterator();
        while (pIter.hasNext()) {
            PowerUp p = pIter.next();
            p.update();
            if (p.getBounds().intersects(player.getBounds())) {
                player.upgradeWeapon();
                score += 50;
                pIter.remove();
            } else if (p.getY() > HEIGHT) {
                pIter.remove();
            }
        }

        enemySpawnTimer++;
        if (enemySpawnTimer > 40) {
            enemies.add(new Enemy(random.nextInt(WIDTH - 40), -40));
            enemySpawnTimer = 0;
        }

        Iterator<Enemy> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Enemy enemy = eIter.next();
            enemy.update();

            if (enemy.getBounds().intersects(player.getBounds())) {
                isRunning = false;
                isGameOver = true;
                gameLoop.stop();
            }

            Iterator<Bullet> bulletIter = bullets.iterator();
            while (bulletIter.hasNext()) {
                Bullet b = bulletIter.next();
                if (enemy.getBounds().intersects(b.getBounds())) {
                    score += 10;
                    bulletIter.remove();
                    if (random.nextInt(100) < 20) {
                        powerUps.add(new PowerUp(enemy.getX(), enemy.getY()));
                    }
                    eIter.remove();
                    break;
                }
            }

            if (enemy.getY() > HEIGHT) {
                if(enemies.contains(enemy)) eIter.remove();
            }
        }
    }

    // --- Cập nhật logic bắn đạn chùm (Level 4 & 5) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) player.setLeft(true);
        if (key == KeyEvent.VK_RIGHT) player.setRight(true);

        if (key == KeyEvent.VK_SPACE) {
            if (player.canShoot()) {
                int level = player.getWeaponLevel();
                int px = player.getX();
                int py = player.getY();

                if (level == 1) {
                    bullets.add(new Bullet(px + 22, py));
                } else if (level == 2) {
                    bullets.add(new Bullet(px + 5, py));
                    bullets.add(new Bullet(px + 40, py));
                } else if (level == 3) {
                    bullets.add(new Bullet(px + 5, py));
                    bullets.add(new Bullet(px + 22, py));
                    bullets.add(new Bullet(px + 40, py));
                } else if (level == 4) {
                    // Level 4: 5 viên tỏa nhẹ
                    // Tham số thứ 3 là góc bắn (độ)
                    bullets.add(new Bullet(px + 22, py, -15));
                    bullets.add(new Bullet(px + 22, py, -5));
                    bullets.add(new Bullet(px + 22, py, 0));
                    bullets.add(new Bullet(px + 22, py, 5));
                    bullets.add(new Bullet(px + 22, py, 15));
                } else {
                    // Level 5: 7 viên tỏa rộng (MAX)
                    bullets.add(new Bullet(px + 22, py, -30));
                    bullets.add(new Bullet(px + 22, py, -20));
                    bullets.add(new Bullet(px + 22, py, -10));
                    bullets.add(new Bullet(px + 22, py, 0));
                    bullets.add(new Bullet(px + 22, py, 10));
                    bullets.add(new Bullet(px + 22, py, 20));
                    bullets.add(new Bullet(px + 22, py, 30));
                }
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