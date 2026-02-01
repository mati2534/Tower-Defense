package pl.towerdefense;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tower {
        private double x,y;
        private double range= 150;
        private int damage = 30;
        private double fireRate = 1;
        private double fireTimer = 0; //czas od ostatniego strzalu

    public Tower(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.fillRect(x-15,y-25,30,50);
    }

    public Projectile tryShoot(double delta, Enemy target){
        if(target == null) return null;

        fireTimer += delta; //zbieramy czas

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if(distance <= range && fireTimer >= 1/fireRate){
        //Jeśli wróg w zasięgu i minął czas od ostatniego strzału -> tworzymy Projectile.
            fireTimer = 0;
            return new Projectile(x,y,target,damage);
        }
        return null;
    }






}
