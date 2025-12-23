import java.awt.*;

/**
 * GameObject: Lớp trừu tượng quản lý vị trí và kích thước cơ bản
 * Tất cả các vật thể trong game (Player, Enemy, Bullet) sẽ kế thừa lớp này.
 */
public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected Color color;

    public GameObject(int x, int y, int w, int h, Color color) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.color = color;
    }

    public abstract void update();

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}