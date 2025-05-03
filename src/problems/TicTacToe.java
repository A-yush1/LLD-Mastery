package problems;

import java.util.*;

enum GameStatus{
    IN_PROGRESS, DRAW, PLAYER_WON
}

enum GameSymbol{
    X('X'), Y('Y'), O('O'), Z('Z');
    private final char symbol;
    GameSymbol(char symbol){
        this.symbol = symbol;
    }

    public char getSymbol(){
        return this.symbol;
    }
}


interface Player{
    String getName();
    char getSymbol();
}

class HumanPlayer implements Player{
    private String name;
    private GameSymbol symbol;
    HumanPlayer(String name, GameSymbol symbol){
        this.name = name;
        this.symbol = symbol;
    }

    public String getName(){
        return name;
    }

    public char getSymbol(){
        return symbol.getSymbol();
    }
}

interface PlayerFactory{
    Player createPlayer(String name, GameSymbol symbol);
}

class HumanPlayerFactory implements PlayerFactory{
    public Player createPlayer(String name, GameSymbol symbol){
        return new HumanPlayer(name, symbol);
    }
}

interface GameRule{
    GameStatus checkGameStatus(char grid[][], Player lastPlayer);
    boolean isValidMove(int row, int col, char grid[][]);
}

class StandardRule implements GameRule{

    public GameStatus checkGameStatus(char[][] grid, Player player) {
        int size = grid.length;
        char symbol = player.getSymbol();

        boolean isDraw = true;
        boolean firstDiagonalWin = true;
        boolean secondDiagonalWin = true;

        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean columnWin = true;

            for (int j = 0; j < size; j++) {
                char rowChar = grid[i][j];
                char colChar = grid[j][i];

                if (rowChar != symbol) rowWin = false;
                if (colChar != symbol) columnWin = false;
                if (rowChar == '-') isDraw = false;
            }

            if (rowWin || columnWin) return GameStatus.PLAYER_WON;

            if (grid[i][i] != symbol) firstDiagonalWin = false;
            if (grid[i][size - i - 1] != symbol) secondDiagonalWin = false;
        }

        if (firstDiagonalWin || secondDiagonalWin) return GameStatus.PLAYER_WON;
        if (isDraw) return GameStatus.DRAW;

        return GameStatus.IN_PROGRESS;
    }

    public boolean isValidMove(int row, int col, char grid[][]){
        int size = grid.length;
        return row >= 0 && row < size && col >= 0 && col < size && grid[row][col] == '-';
    }

}

class Board{

    private final int size;
    private final char grid[][];

    Board(int size){
        this.size = size;
        grid = new char[size][size];
        initialize();
    }

    private void initialize(){
        for(char row[] : grid)
            Arrays.fill(row, '-');
    }

    public char[][] getGrid(){
        char copyGrid[][] = new char[size][size];
        for(int i=0; i<size; i++)
            for(int j=0; j<size; j++)
                copyGrid[i][j] = grid[i][j];

        return copyGrid;
    }

    void display(){
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
                System.out.print(grid[i][j] + " ");

            System.out.println();
        }
    }

    void makeMove(int row, int col, char symbol){
        grid[row][col] = symbol;
    }

}

class TicTacToeGame {
    private List<Player> players;
    private Board board;
    private GameRule rule;
    private int currPlayerIdx;
    private GameStatus status;

    TicTacToeGame(int playerCount, int boardSize) {
        this.currPlayerIdx = 0;
        this.status = GameStatus.IN_PROGRESS;
        this.board = new Board(boardSize);
        this.rule = new StandardRule();
        this.players = createPlayers(playerCount);
    }

    private List<Player> createPlayers(int playerCount) {
        PlayerFactory playerFactory = new HumanPlayerFactory();
        List<Player> playerList = new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            String name = "Player" + (i + 1);
            GameSymbol symbol = GameSymbol.values()[i];
            Player player = playerFactory.createPlayer(name, symbol);
            playerList.add(player);
        }

        return playerList;
    }

    void startGame() {
        System.out.println("...............Game Started.........");
        Scanner scanner = new Scanner(System.in);
        while (status == GameStatus.IN_PROGRESS) {
            Player currentPlayer = players.get(currPlayerIdx);
            System.out.println("Player - " + currentPlayer.getName() + "(" + currentPlayer.getSymbol() + ")" + " turn");
            board.display();

            int inputRow, inputCol;
            do {
                System.out.println("Input row and column:-");
                inputRow = scanner.nextInt();
                inputCol = scanner.nextInt();
            } while (!rule.isValidMove(inputRow, inputCol, board.getGrid()));

            board.makeMove(inputRow, inputCol, currentPlayer.getSymbol());
            status = rule.checkGameStatus(board.getGrid(), currentPlayer);

            if (status == GameStatus.PLAYER_WON) {
                System.out.println("Player - " + currentPlayer.getName() + " won!!!!");
            }

            currPlayerIdx = (currPlayerIdx + 1) % players.size();
        }

        if (status == GameStatus.DRAW)
            System.out.println("Game Draw!!!!");

        board.display();
    }
}


public class TicTacToe {
    public static void main(String[] args) {
        int playerCount = 2;
        int boardSize = 3;
        TicTacToeGame game = new TicTacToeGame(playerCount, boardSize);
        game.startGame();
    }
}