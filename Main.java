package C4FX;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.BitSet;

public class Main extends Application {
    public static Pane root = new Pane();
    public static Pane gameBoardPane=new Pane();
    public static Pane chipPane = new Pane();
    public static int box=100;
    public static int gameCount;
    public static Button aiFirstButton, userFirstButton, resetButton;
    public static Text currentTurnText;
    public static boolean isUserTurn;
    public static short [][] board = new short [7][6];
    public static short userValue, aiValue;
    public static final short DATASIZE = 56;
    public static PauseTransition pause;

    @Override
    public void start(Stage primaryStage) throws Exception{

        isUserTurn = false;

        makeLines();
        aiFirstButton = new Button("AI First");
        aiFirstButton.relocate(20, 630);
        userFirstButton = new Button("User First");
        userFirstButton.relocate(120, 630);
        resetButton = new Button("RESET");
        resetButton.relocate(220, 630);
        currentTurnText = new Text("Turn: ");
        currentTurnText.relocate(320, 630);
        root.getChildren().addAll(aiFirstButton,userFirstButton,resetButton,currentTurnText);
        root.getChildren().add(chipPane);


        Scene mainScene=new Scene(root,720,680);
        root.setStyle("-fx-background-color: #0000FF");//#797D7F
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(mainScene);
        primaryStage.show();

        gameCount = 0;



        aiFirstButton.setOnAction((ActionEvent e) -> {
            if(!isUserTurn){  //This is not robust!!!! relies on a new scene. These buttons should not be visible within this scene.
                aiValue = 1;
                userValue = 2;
                pause = new PauseTransition(Duration.millis(1000));
                pause.play();
                pause.setOnFinished(event ->
                        aiTurn()
                );

            }
        });
        userFirstButton.setOnAction((ActionEvent e) -> {
            if(!isUserTurn){   //This is not robust!!!! relies on a new scene
                userValue = 1;
                aiValue = 2;
                isUserTurn = true;
            }
        });
        resetButton.setOnAction((ActionEvent e) -> {
            //broken
            board = new short[7][6];
            chipPane.getChildren().removeAll(chipPane.getChildren());
        });



        mainScene.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double mouseClickX, mouseClickY;
                mouseClickX = mouseEvent.getSceneX();
                mouseClickY = mouseEvent.getSceneY();
                System.out.println(mouseClickY);
                if (mouseClickY < 610) {
                    if (mouseClickX < 15) ;
                    else if (mouseClickX < 110)
                        takeUserInput(0);
                    else if (mouseClickX < 210)
                        takeUserInput(1);
                    else if (mouseClickX < 310)
                        takeUserInput(2);
                    else if (mouseClickX < 410)
                        takeUserInput(3);
                    else if (mouseClickX < 510)
                        takeUserInput(4);
                    else if (mouseClickX < 610)
                        takeUserInput(5);
                    else if (mouseClickX < 710)
                        takeUserInput(6);
                }
            }
        });

    }

    public static void takeUserInput(int command){
        short nextSpot;
        short [][] temp;
        temp = board.clone();
        if(isUserTurn){
            nextSpot = findNextSpot(temp[command]);
            if(nextSpot < 6){
                temp[command][nextSpot] = userValue;
                board = temp;
                gameCount++;
                updateBoardSituation((short)command, nextSpot);
                isUserTurn = false;
                pause = new PauseTransition(Duration.millis(1000));
                pause.play();
                pause.setOnFinished(event ->
                        aiTurn()
                );

            }
        }
    }
    public static void aiTurn(){
        short column, row;
        short [][] temp;
        temp = board.clone();
        column = 0;
        row = 0;


        System.out.println("AI turn");

        row = findNextSpot(board[column]);
        temp[column][row] = aiValue;
        board = temp;
        gameCount++;
        updateBoardSituation(column, row);
        isUserTurn = true;
    }
    public static BitSet encodeBoard(short [][] temp){
        BitSet results;
        short count;
        boolean isZero;

        results = new BitSet(DATASIZE);

        count = 0;

        for(int column= 0; column < 7; column++){
            isZero = false;
            for(int row = 0; row < 6; row++){
                if(isZero){
                    results.set(count,(boolean)results.get(count - 1));
                } else if(temp[column][row] == aiValue){
                    results.set(count, true);
                    isZero = false;
                } else if(temp[column][row] == userValue){
                    results.set(count, false);
                    isZero = false;
                } else if(row == 0 && temp[column][0] == 0){
                    results.set(count,false);
                    isZero = true;
                } else{
                    results.set(count, !(boolean)results.get(count - 1));
                    isZero = true;
                }
                count++;
            }
            if(isZero) results.set(count,results.get(count - 1));
            else results.set(count, !results.get(count - 1));
            count++;
        }
        return results;
    }
    public static short [][] decodeBoard(BitSet command){
        short [][] temp;
        boolean current;
        short count, bitCount;

        temp = new short [7][6];

        for(int i = 0; i < 7; i++){    //7 columns

            bitCount = (short)(7 * (i + 1) - 1);
            count = 1;
            current = command.get(bitCount);


            while (current == command.get(bitCount - count) && count < 7) {
                count++;
            }
            while (count < 7){
                current = command.get(bitCount - count);
                if (current) {                    //ai is true
                    temp[i][6 - count] = aiValue;
                }
                else {                             //user is false
                    temp[i][6 - count] = userValue;
                }
                count++;
            }
        }
        return temp;
    }
    public static void updateBoardSituation(short column, short row){
        Chip temp;
        temp = new Chip(board[column][row]);
        temp.placeChip(column, row);


        if(checkWin(column, row, board)){
            if(isUserTurn)System.out.println("User won!");
            else System.out.println("AI won");
            //save game
        }else if(gameCount >= 42){
            System.out.println("Draw");
            //save game
        }
    }
    public static boolean checkWin(short column, short row, short [][] temp){
        short sum;
        sum = 0;

        sum += checkHorizontal(column, row, temp);
        sum += checkVertical(column, row, temp);
        sum += checkForwardSlash(column, row, temp);
        sum += checkBackwardSlash(column, row, temp);
        if(sum > 0) return true;
        else return false;
    }
    public static short checkHorizontal(short column, short row, short[][] temp){
        int sum;
        sum = 0;

        for(int i = 1; (column - i) >= 0; i++){
            if (temp [column - i][row] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        for(int k = 1; (column + k) <= 6; k++){
            if (temp [column + k][row] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        if (sum >=3) return 1;
        else return 0;
    }
    public static short checkVertical(short column, short row, short temp[][] ){
        int sum;
        sum = 0;

        for(int i = 1; (row - i) >= 0; i++){
            if (temp [column][row - i] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        for(int k = 1; (row + k) <= 5; k++){
            if (temp [column][row + k] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        if (sum >=3) return 1;
        else return 0;
    }
    public static short checkForwardSlash(int column, int row, short temp[][] ){
        int sum;
        sum = 0;

        for(int i = 1; ((column - i) >= 0) && ((row + i) <= 5); i++){
            if (temp [column - i][row + i] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        for(int k = 1; ((column + k) <= 6) && ((row - k) >= 0); k++){
            if (temp [column + k][row - k] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        if (sum >=3) return 1;
        else return 0;
    }
    public static short checkBackwardSlash(int column, int row, short temp[][] ){
        int sum;
        sum = 0;

        for(int i = 1; ((column - i) >= 0) && ((row - i) >= 0); i++){
            if (temp [column - i][row - i] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        for(int k = 1; ((column + k) <= 6) && ((row + k) <= 5); k++){
            if (temp [column + k][row + k] == (temp[column][row])){
                sum++;
            }
            else break;
        }
        if (sum >=3) return 1;
        else return 0;
    }
    public static short findNextSpot( short[] column){
        short results;
        results = -1;

        for(int i = 0; i < column.length; i++ ){
            if(column[i] == 0){
                results = (short)i;
                break;
            } else results = (short)column.length;
        }
        return results;
    }
    public static void makeLines() {
        Line temp;
        Circle temp0, temp1;

        for (int i = 0; i < 9; i++) {
            int j=0;
            while (j< 7) {
                temp0 = new Circle(60 + box * j, 60, 45, Color.LIGHTGRAY);
                gameBoardPane.getChildren().addAll(temp0);
                int k=0;
                while(k < 6) {
                    temp1 = new Circle(60+box*j, 60 + box * k, 45, Color.LIGHTGRAY);
                    gameBoardPane.getChildren().addAll(temp1);
                    k++;
                }
                j++;
            }
        }
        root.getChildren().add(gameBoardPane);
    }
    public static void main (String[]args){
        launch(args);
    }
        /*
        public class Cell{
            public void Cell(String name, int x){
            }
        }*/
}