package pl.towerdefense;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
    public static final int width = 800;
    public static final int height = 600;

    private void update(){
        //logika gry

    }

    private void render(GraphicsContext gc){
        // czyszczenie / rysowanie
        gc.clearRect(0, 0, width, height);
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
    }

    public static void main(String[] args) {
        launch();
        //uruchamia javaFX ,wywoluje start()
    }
}