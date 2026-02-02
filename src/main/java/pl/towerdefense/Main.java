package pl.towerdefense;

import pl.towerdefense.Enemy;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {
    public static final int width = 800;
    public static final int height = 600;

    public static final int tile_size = 40;
    public static final int cols = width / tile_size;
    public static final int rows = height / tile_size;
    //tworzymy siatke GRID po 40px (20columns / 15rows)

    private int gold = 100;
    private int wave = 1;
    private int enemiesToSpawn = 5;
    private int enemiesSpawned = 0;
    private double spawnTimer = 0;

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Tower> towers = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private boolean[][] buildable = new boolean[cols][rows];


    private void update(double delta){
        //logika gry
        // delta - czas od poprzedniej klatki (sekundy), każdy który używa czasu, RUCHU albo TIMERA powinien dostać delta

        int startColEnemy = 0;
        int pathRow = rows / 2;

        double startX = startColEnemy * tile_size +tile_size /2.0;
        double startY = pathRow * tile_size + tile_size /2.0;
        //CENTRUJE BUDOWLE W KAFELKU GRIDA!!!
        spawnTimer += delta;
        if(enemiesSpawned < enemiesToSpawn && spawnTimer >= 1.5){
            //spawnTimer>=1 == wrog co sekunde
            enemies.add(new Enemy(startX,startY));
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
//ZMIENIC NA WIEKSZA ILOSC FAL
    }

    private void render(GraphicsContext gc){
        // czyszczenie / rysowanie
        gc.clearRect(0, 0, width, height);

        gc.fillText("Wave: "+ wave, 380, 20);
        gc.fillText("Gold: " + gold, 10,20);
        gc.fillText("Tower cost: "+ Tower.cost, 10,500);

        gc.setStroke(Color.LIGHTGRAY);
        //nadajemy kolor
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++){
                gc.strokeRect(
                  x * tile_size,
                  y * tile_size,
                        tile_size,
                        tile_size
                );
                //rysuje obramowanie prostokąta (pozycja x,y, szerokosc, wysokosc)
            }
        }
        //rysujemy linie pomocnicze GRIDA

        int pathRow = rows / 2;
        gc.setFill(Color.rgb(101,67,33));
        for(int x = 0; x < cols; x++){
            gc.fillRect(
                    x * tile_size,
                    pathRow * tile_size,
                    tile_size,
                    tile_size

            );
        }
        //malowanie sciezki


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

        int pathRow = rows/2;
        //siezka wrogow

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

        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++){
                buildable[x][y] = true;
            }
        }
        //rozlozenie Grida
        for(int x = 0; x < cols; x++){
            buildable[x][pathRow] = false;
        }
        //ograniczenie budowania na sciezce


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

        scene.setOnMouseClicked(event ->{
            //nasluchiwanie klikniecia myszki
            double mouseX = event.getX();
            double mouseY = event.getY();

            tryBuildTower(mouseX,mouseY);
        });

        //enemies.add(new Enemy(0,300));
        //dodanie obiektu (wroga)


        int gridX = 8; //wybrany kafelek GRID
        int gridY = 6;
        double x =  gridX * tile_size + tile_size / 2.0; // zrzut na pixele
        double y = gridY * tile_size + tile_size / 2.0;
        towers.add(new Tower(x,y));
        //dodanie wiezy w kafelku grida
        buildable[gridX][gridY] = false;
    }


    private void tryBuildTower(double mouseX, double mouseY){
        int gridX = (int)(mouseX/tile_size);
        int gridY = (int)(mouseY/tile_size);
        // Przelicza dokładną pozycję myszki na numer kolumny/wiersza grida

        if(gold < Tower.cost){
            return;

        }
        //brak pieniedzy

        if(gridX <0 || gridY < 0 || gridX >= cols || gridY >= rows){
            return;
        }
        //wyklucza budowanie poza mapą

        if(!buildable[gridX][gridY]){
            return;
        }
        //miejsca gdzie nie mozna budowac

        double x = gridX * tile_size + tile_size / 2.0;
        double y = gridY * tile_size + tile_size / 2.0;
        //CENTRUJE BUDOWLE W KAFELKU GRIDA!!!

        towers.add(new Tower(x,y));
        gold -= Tower.cost;

        buildable[gridX][gridY] = false;
        //blokuje dane pole
    }


    public static void main(String[] args) {
        launch();
        //uruchamia javaFX ,wywoluje start()
    }
}