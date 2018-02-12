import java.util.BitSet;
import java.util.Scanner;

public class ConnectAI {

    public static short userValue;
    public static String userName;
    public static short aiValue;
    public static short lastColumn, lastRow;
    public static final short DATASIZE = 56;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        short currentPlayer;


        short [][] board = {  {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
                {  0,  0, 0, 0, 0, 0},
        };    // [column] [row]

        setup();
        printBoard(board);


        game:
        for(int i =0; i <= 42; i++){



            if(i%2 == 0) currentPlayer = 1;
            else currentPlayer = 2;

            if(currentPlayer == userValue) board = userTurn(board);
            else board = aiTurn(board);

            printBoard(board);//update board
            System.out.println("");
            if(checkWin(board)){
                if(currentPlayer == userValue){
                    System.out.println(userName + " won!!!");
                } else{
                    System.out.println("Artificial Intelligence " +
                            "takes the win. Better luck next time.");
                }
                break game;
            }


            /*BitSet poop = encodeBoard(board);
            printBitSet(poop);
            System.out.println("");
            System.out.println(" copy board");
            printBoard(decodeBoard(poop));
            System.out.println("");
*/
        }


        System.out.println(Runtime.getRuntime().maxMemory()/ 1000000);
        long result = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println(Runtime.getRuntime().totalMemory() / 1000000 + " MB available" );
        System.out.println(Runtime.getRuntime().freeMemory() / 1000000 + " MB not used");
        System.out.println(result / 1000000 + " MB used");
    }


    public static short[][] userTurn(short [][] board){
        Scanner input = new Scanner(System.in);
        char command;
        short column, nextSpot;
        boolean redo;

        nextSpot = -1;

        do {
            System.out.println("User turn, please enter a valid column");

            command = input.next().toLowerCase().charAt(0);
            column = (short)(command - 97);
            if ((column >= 0) && (column <= 6)) {
                nextSpot = findNextSpot(board[column]);
                if(nextSpot < 6){
                    redo = false;
                } else {
                    redo = true;
                    System.out.println("Invalid command");
                }
            } else {
                redo = true;
                System.out.println("Invalid command");
            }
        } while (redo);

        lastColumn = column;
        lastRow = nextSpot;
        board[column][nextSpot] = userValue;


        return board;
    }


    public static short [][] aiTurn(short [][] board){




        Scanner input = new Scanner(System.in);
        char command;
        short column, nextSpot;
        boolean redo;

        nextSpot = -1;

        do {
            System.out.println("ai turn, please enter a valid column");

            command = input.next().toLowerCase().charAt(0);
            column = (short)(command - 97);
            if ((column >= 0) && (column <= 6)) {
                nextSpot = findNextSpot(board[column]);
                if(nextSpot < 6){
                    redo = false;
                } else {
                    redo = true;
                    System.out.println("Invalid command");
                }
            } else {
                redo = true;
                System.out.println("Invalid command");
            }
        } while (redo);

        lastColumn = column;
        lastRow = nextSpot;
        board[column][nextSpot] = aiValue;


        return board;
    }

    public static short [][] decodeBoard(BitSet command){
        short [][] board;
        boolean current;
        short count, bitCount;

        board = new short [7][6];

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
                    board[i][6 - count] = aiValue;
                }
                else {                             //user is false
                    board[i][6 - count] = userValue;
                }
                count++;
            }
        }
        return board;
    }

    public static BitSet encodeBoard(short [][] board){
        BitSet results;
        short count;
        boolean isZero, temp;

        results = new BitSet(DATASIZE);

        count = 0;

        for(int column= 0; column < 7; column++){
            isZero = false;
            for(int row = 0; row < 6; row++){
                if(isZero){
                    temp = (boolean)results.get(count - 1);
                    results.set(count,temp);
                } else if(board[column][row] == aiValue){
                    results.set(count, true);
                    isZero = false;
                } else if(board[column][row] == userValue){
                    results.set(count, false);
                    isZero = false;
                } else if(row == 0 && board[column][0] == 0){
                    results.set(count,false);
                    isZero = true;
                } else{
                    temp = (boolean)results.get(count - 1);
                    results.set(count, !temp);
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

    public static boolean checkWin(short[][] board){
        short sum;
        sum = 0;

        sum += checkPerpendicular(board, true);
        sum += checkPerpendicular(board, false);
        sum += checkDiagonal(board, true);
        sum += checkDiagonal(board, false);
        if(sum > 0) return true;
        else return false;
    }
    public static short checkPerpendicular(short[][] board, boolean isHorizontal){
        boolean isGoodLower, isGoodUpper;
        short upper, lower, tester, column, row, currentTurn, sum;
        isGoodLower = true;
        isGoodUpper = true;
        currentTurn = board [lastColumn][lastRow];

        column = 0;
        row = 0;
        sum = 0;
        
        if(isHorizontal) {
            upper = 7;
            lower = -1;
            row = lastRow;
            tester = lastColumn;
        }
        else {
            upper = 7;
            lower = -1;
            column = lastColumn;
            tester = lastRow;
        }
        for(short i = 1; i < 4; i++) {
            if (tester + i < upper && isGoodUpper) {
                if (isHorizontal)
                    column = (short) (tester + i);
                else row = (short) (tester + i);
                if (currentTurn != board[column][row]) {
                    isGoodUpper = false;
                    sum += (i - 1);
                }

            } else isGoodUpper = false;
            if (tester - i > lower && isGoodLower) {
                if (isHorizontal)
                    column = (short) (tester - i);
                else row = (short) (tester - i);
                if (currentTurn != board[column][row]) {
                    isGoodLower = false;
                    sum += (i - 1);
                }
            } else isGoodLower = false;
        }
        if(sum > 2 || isGoodUpper || isGoodLower) {
            return 1;
        }
        else return 0;
    }


    public static short checkDiagonal(short[][] board, boolean isForwardSlash){
        boolean isGoodLower, isGoodUpper;
        short column, row, currentTurn, sum;
        isGoodLower = true;
        isGoodUpper = true;
        currentTurn = board [lastColumn][lastRow];

        sum = 0;

        row = lastRow;
        column = lastColumn;
        for(short i = 1; i < 5; i++) {
            if (isForwardSlash && column + 1 < 6 && row + 1 < 5 && isGoodUpper) {
                column = (short) (column + 1);
                row = (short) (row + 1);
            } else if (!isForwardSlash && column + 1 < 7 && row + 1 < 6 && isGoodUpper) {
                row = (short) (row + 1);
                column = (short) (column + 1);
            } else isGoodUpper = false;
            if (currentTurn != board[column][row] && isGoodUpper) {
                isGoodUpper = false;
                sum += (i - 1);
            }
        }
        for(short i = 1; i < 5; i++) {
            if (isForwardSlash && column - 1 > - 1 && row - 1 > -1 && isGoodLower) {
                column = (short) (column - 1);
                row = (short) (row - 1);
            } else if(!isForwardSlash && column + 1 < 7 && row + 1 < 7 && isGoodLower){
                row = (short) (row - 1);
                column = (short) (column + 1);
            }else isGoodLower = false;
            if (currentTurn != board[column][row] && isGoodLower) {
                isGoodLower = false;
                sum += (i - 1);
            }
        }
        if(sum > 2 || isGoodUpper || isGoodLower) {
            System.out.println("diagonal");
            return 1;
        }
        else return 0;
    }

    public static void printBitSet(BitSet command){
        short a;
        for(int i = 0; i < DATASIZE; i++){
            if(command.get(i))a = 1;
            else a = 0;
            System.out.print(a);

            if((i+1)%7 == 0)System.out.print(" - ");
        }
        System.out.println("");
        System.out.println(command.size());
    }

    public static void printBoard(short [][] board){
        for(int row = 5; row >= 0; row--){
            for(int column = 0; column <= 6; column++){
                System.out.print(" " + board[column][row] + " ");
            }
            System.out.println("");
        }
        System.out.println(" A  B  C  D  E  F  G");
        System.out.println("");
    }

    public static void setup(){
        Scanner input = new Scanner(System.in);
        String command;
        boolean redo;

        System.out.println("Welcome to Connect4");
        System.out.println("What is the name of our potential winner?");
        userName = input.next();


        do {
            System.out.println("Who is first, user or ai?");
            command = input.next();
            switch (command){
                case "user":
                    userValue = 1;
                    aiValue = 2;
                    redo = false;
                    break;
                case "ai":
                    aiValue = 1;
                    userValue = 2;
                    redo = false;
                    break;
                default:
                    System.out.println("Not a valid command");
                    redo =true;
                    break;
            }
        }while(redo);
    }
}
