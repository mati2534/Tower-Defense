package pl.towerdefense;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
    private  double x;
    private double y;
    private double speed = 30;
    private int hp = 100;
    private int reward = 10;


    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getReward() {
        return reward;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void update(double delta) {
        x += speed * delta;
        //poruszanie sie (100px na s)
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x,y,20,20);
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isDead(){
        return hp <= 0;
    }
}
