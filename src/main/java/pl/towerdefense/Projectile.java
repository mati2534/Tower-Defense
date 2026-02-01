package pl.towerdefense;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Projectile {
    private double x,y;
    private Enemy  target;
    private int damage;
    private double speed = 1200;
    private boolean active = true;
    //czy pocisk nadal jest w ruchu


    public boolean isActive() {
        return active;
        //Jeśli pole jest private, a chcesz je sprawdzić z innej klasy — potrzebujesz metody. gettera
    }

    public Projectile(double x, double y, Enemy target, int damage) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
    }

    public void update(double delta) {
        if(!active || target == null) return;

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if(distance < 5){
            target.takeDamage(damage);
            active = false;
        }else{
            x += dx/distance*speed * delta;
            y += dy/distance*speed * delta;
        }
    }

    public void render(GraphicsContext gc){
        if(!active)return;
        gc.setFill(Color.BLACK);
        gc.fillOval(x-3,y-3,6,6);
    }
}
