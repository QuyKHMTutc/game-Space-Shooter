import java.awt.*;

public class Player extends GameObject {
    private boolean left, right;
    private int speed = 8;
    private int shootCooldown = 0;

    // Thêm cấp độ súng (Mặc định là 1)
    private int weaponLevel = 1;

    public Player(int x, int y) {
        super(x, y, 50, 50, Color.CYAN);
    }

    @Override
    public void update() {
        // Di chuyển trái
        if (left && x > 0) {
            x -= speed;
        }
        // Di chuyển phải (Sử dụng biến tĩnh WIDTH từ main class)
        if (right && x < SpaceShooterGame.WIDTH - width) {
            x += speed;
        }

        // Giảm thời gian chờ bắn
        if (shootCooldown > 0) shootCooldown--;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        // Vẽ phi thuyền hình tam giác
        int[] xPoints = {x, x + width / 2, x + width};
        int[] yPoints = {y + height, y, y + height};
        g.fillPolygon(xPoints, yPoints, 3);

        // Vẽ lửa động cơ
        g.setColor(Color.ORANGE);
        g.fillRect(x + 15, y + height, 6, 10);
        g.fillRect(x + 29, y + height, 6, 10);
    }

    // --- Logic nâng cấp súng ---
    public void upgradeWeapon() {
        if (weaponLevel < 5) { // Tăng giới hạn lên Level 5
            weaponLevel++;
        }
    }

    public int getWeaponLevel() {
        return weaponLevel;
    }

    // Các phương thức điều khiển
    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }

    public boolean canShoot() { return shootCooldown == 0; }

    public void resetCooldown() { shootCooldown = 15; }
}