package pl.towerdefense;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tower {
        private double x,y;
        private double range= 150;
        private int damage = 10;
        private double fireRate = 0.7;
        private double lastShotTime = 0;

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.fillRect(x-15,y-25,30,50);
    }

    public Projectile tryShoot(double now, Enemy target){
    //now - czas aktualny sekundy
        if(target == null) return null;


        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if(distance <= range && now - lastShotTime >= 1/fireRate){
        //Jeśli wróg w zasięgu i minął czas od ostatniego strzału -> tworzymy Projectile.
            lastShotTime = now;
            return new Projectile(x,y,target,damage);
        }
        return null;
    }






}
