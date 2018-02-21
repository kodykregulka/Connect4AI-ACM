package C4FX;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import static C4FX.Main.box;
import static C4FX.Main.chipPane;

public class Chip {
    Circle image;
    Chip(short value){
        if(value == 1)image = new Circle(40, Color.RED);
        else image = new Circle(40, Color.YELLOW);
    }
    TranslateTransition placeChip(short column, short row){
        TranslateTransition results;
        chipPane.getChildren().add(image);
        results = new TranslateTransition(Duration.millis(800),image);
        results.setFromX(60 + column*box);
        results.setFromY(0);
        results.setToY(560 - row*box);
        return results;
    }

}
