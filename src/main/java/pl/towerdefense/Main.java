package pl.towerdefense;

import pl.towerdefense.Enemy;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {
    public static final int width = 800;
    public static final int height = 600;
    private int gold = 100;
    private int wave = 1;
    private int enemiesToSpawn = 5;
    private int enemiesSpawned = 0;
    private double spawnTimer = 0;

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();


    private void update(double delta){
        //logika gry
        // delta - czas od poprzedniej klatki (sekundy), każdy który używa czasu, RUCHU albo TIMERA powinien dostać delta

        spawnTimer += delta;
        if(enemiesSpawned < enemiesToSpawn && spawnTimer >= 1){
            //spawnTimer>=1 == wrog co sekunde
            enemies.add(new Enemy(0,300));
            enemiesSpawned++;
            spawnTimer = 0;
        }

        for(Enemy enemy : enemies){
            enemy.update(delta);
            //dla każdego wroga w liście wywołujemy update()
        }


        for(Tower tower: towers){
            Enemy target = enemies.isEmpty() ? null : enemies.get(0);
            //strzelamy do wroga
            // if true = null : if false enemies.get(0)
            //DO ZOPTYMALIZOWANIA!!!

            Projectile p = tower.tryShoot(delta, target);
            if(p != null){
                projectiles.add(p);
                //dodajemy do listy projectiles
            }
        }


        for(Projectile p  : projectiles){
            p.update(delta);
        }


        projectiles.removeIf(p-> !p.isActive());
        //usuwa nieaktywny pocisk


        Iterator<Enemy> iterator = enemies.iterator();
        // iterator umożliwia sekwencyjny dostęp do elementów kolekcji (np. List, Set) bez ujawniania ich wewnętrznej struktury.
        while (iterator.hasNext()){
            Enemy enemy = iterator.next();

            if(enemy.isDead()){
                gold += enemy.getReward();
                iterator.remove();
            }
        } //czy istnieje enemy -> przejdz do niego -> czy martwy -> +gold, -enemy

        if(enemies.isEmpty() && enemiesSpawned == enemiesToSpawn){
            wave++;
            enemiesSpawned = 0;
            enemiesToSpawn += 2;
        }
    }

    private void render(GraphicsContext gc){
        // czyszczenie / rysowanie
        gc.clearRect(0, 0, width, height);

        gc.fillText("Wave: "+ wave, 10, 40);

        for(Tower tower: towers){
            tower.render(gc);
        }

        for(Projectile p: projectiles){
            p.render(gc);
        }
        for(Enemy enemy: enemies){
            enemy.render(gc);
            //rysujemy wroga
        }

        gc.fillText("Gold: " + gold, 10,20);


    }

    @Override
    public void start(Stage stage){
        //tworzymy okno gry
        Canvas canvas = new Canvas(width, height);
        //tworzymy płótno
        Pane root = new Pane(canvas);
        //najwyzszy element

        Scene scene = new Scene(root);
        //tworzy scene
        stage.setTitle("Tower Defense");
        stage.setScene(scene);
        //przypisuje scene do okna
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        // gc obiekt do rysowania



        new AnimationTimer() {
            //petla czasu 60FPS
            private long lastTime = 0;
            @Override
            public void handle(long now) {
                //wywolanie co klatke
                if(lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                update(delta);
                render(gc);
            }
        }.start();
        // uruchamia petle



        //enemies.add(new Enemy(0,300));
        //dodanie obiektu (wroga)

        towers.add(new Tower(400,300));
    }


    public static void main(String[] args) {
        launch();
        //uruchamia javaFX ,wywoluje start()
    }
}