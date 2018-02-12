package C4FX;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;

import static C4FX.Main.box;
import static C4FX.Main.chipPane;

public class Chip {
    Circle image;
    Chip(short value){
        if(value == 1)image = new Circle(40, Color.RED);
        else image = new Circle(40, Color.YELLOW);
    }
    void placeChip(short column, short row){
        image.relocate(20 + column*box, 520 - row*box);
        chipPane.getChildren().add(image);
    }
}
