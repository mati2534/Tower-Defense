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

public class Main extends Application {
    public static final int width = 800;
    public static final int height = 600;

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    private void update(){
        //logika gry
        for(Enemy enemy : enemies){
            enemy.update();
            //dla każdego wroga w liście wywołujemy update()
        }

        double now = System.currentTimeMillis() / 1000.0;
        // czas w sekundach
        for(Tower tower: towers){
            Enemy target = enemies.isEmpty() ? null : enemies.get(0);
            //strzelamy do wroga
            // if true = null : if false enemies.get(0)
            //DO ZOPTYMALIZOWANIA!!!

            Projectile p = tower.tryShoot(now, target);
            if(p != null){
                projectiles.add(p);
                //dodajemy do listy projectiles
            }
        }

        for(Projectile p  : projectiles){
            p.update();
        }

        projectiles.removeIf(p-> !p.isActive());
        //usuwa nieaktywny pocisk
    }

    private void render(GraphicsContext gc){
        // czyszczenie / rysowanie
        gc.clearRect(0, 0, width, height);


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
            //petla czasu
            @Override
            public void handle(long now) {
                //wywoanie co klatke
                update();
                render(gc);
            }
        }.start();
        // uruchamia petle

        enemies.add(new Enemy(0,300));
        //dodanie obiektu (wroga)

        towers.add(new Tower(400,300));
    }


    public static void main(String[] args) {
        launch();
        //uruchamia javaFX ,wywoluje start()
    }
}