import java.util.*;
import java.io.*;

/**
 * Word Search generator/command line puzzle game.
 * 
 * @author Andy
 * @version 11/04/2020
 */
public class WordSearch {
    public static final Random RNG;
    public static final char EMPTY_CHARACTER;
    public static final String CHARACTER_WEIGHT;
    public static final Scanner INPUT;
    public static final String ALPHA;

    static {
        RNG = new Random((new Date()).getTime());
        EMPTY_CHARACTER = ' ';
        CHARACTER_WEIGHT = "aaabbccddeeeffgghhiijjkkllmmnnooppqrrrssstttuuvvwwxyyz";
        INPUT = new Scanner(System.in);
        ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    public String[] words;
    public char[][] board;
    public ArrayList<String> foundWords;
    public long startTime;

    /**
     * Primary constructor; called everytime.
     * @param words Word list
     */
    public WordSearch(String[] words) {
        this.words = words;
        foundWords = new ArrayList<>();
        startTime = (new Date()).getTime();
    }
    /**
     * Constructs the class given a file.
     * @param file File object representing the file
     */
    public WordSearch(File file) {
        this(extractWords(file));
    }
    /**
     * Constructs the class given a filename.
     * @param filename Name of the file being used
     */
    public WordSearch(String filename) {
        this(new File(filename));
    }
    /**
     * Extracts the list of words from a file.
     * @param file File object to use
     * @return A list of words
     */
    public static String[] extractWords(File file) {
        ArrayList<String> wordArray = new ArrayList<>();
        Scanner scanner;
        
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        while (scanner.hasNextLine()) {
            wordArray.add(scanner.nextLine());
        }
        String[] words = new String[wordArray.size()];
        for (int i = 0; i < wordArray.size(); i++) {
            words[i] = wordArray.get(i);
        }
        scanner.close();
        return words;
    }
    /**
     * Generates a randomized version of the wordlist.
     * @return A scrambled wordlist
     */
    public String[] getScrambled() {
        String[] scrambled = words.clone();
        int length = words.length;
        for (int i = 0; i < length; i++) {
            int j = RNG.nextInt(length);
            String t = scrambled[length - 1];
            scrambled[length - 1] = scrambled[j];
            scrambled[j] = t;
        }
        return scrambled;
    }
    /**
     * Generates the board, adjusting the size as needed.
     * @param minSize minimum size of the board
     */
    public void generate(int boardSize) {
        String[] words = getScrambled();
        
        initBoard(boardSize);
        for (String word : words) {
            boolean matched = placeWord(word);
            if (!matched) {
                generate(boardSize + 1);
                return;
            }
        }
        fillEmpty();
    }

    /**
     * Tries to place a word on the board.
     * 
     * @param word Word to attempt to place
     * @return True if the word is placed, false if it can't be placed after 100 tries.
     */
    public boolean placeWord(String word) {
        boolean matched = false;
        for (int i = 0; i < 1000; i++) {
            int x = RNG.nextInt(board.length);
            int y = RNG.nextInt(board.length);
            // solution to get random directions to try
            int rx = RNG.nextInt(10);
            int ry = RNG.nextInt(10);
            matched = true;

            for (int s = 0; s < 3; s++) {
                for (int t = 0; t < 3; t++) {
                    int ux = (ry + s) % 3 - 1;
                    int uy = (rx + t) % 3 - 1;
                    
                    if (ux == 0 && uy == 0) {
                        continue;
                    }
                    for (int j = 0; j < word.length(); j++) {   
                        // new x and new y
                        int nx = x + j * ux;
                        int ny = y + j * uy;
                        if (!isEmpty(nx, ny)) {
                            matched = false;
                            break;
                        }
                    }
                    if (matched) {
                        for (int j = 0; j < word.length(); j++) {
                            int nx = x + j * ux;
                            int ny = y + j * uy;
                            board[ny][nx] = word.charAt(j);
                        }
                        break;
                    }
                }
                if (matched)
                    break;
            }
            if (matched)
                break;
        }
        return matched;
    }

    /**
     * Initializes an empty board
     * @param boardSize size used for both width and height of the board
     */
    public void initBoard(int boardSize) {
        board = new char[boardSize][boardSize];
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[y][x] = EMPTY_CHARACTER;
            }
        }
    }

    /**
     * Checks if a board cell is "valid"
     * @param x index of column
     * @param y index of row
     */
    public boolean isValid(int x, int y) {
        if (x < 0 || y < 0 ||
        x >= board[0].length || y >= board.length) {
            return false;
        }
        return true;
    }

    /**
     * Checks to see if a cell is unoccupied by a letter.
     * @param x index of column
     * @param y index of row
     * @return True if a cell is empty, false otherwise
     */
    public boolean isEmpty(int x, int y) {
        return isValid(x, y) && board[y][x] == EMPTY_CHARACTER;
    }

    /**
     * Generates assuming the longest word's length as the minimum board size.
     */
    public void generate() {
        int maxLength = -1;
        for (String word : words) {
            if (word.length() > maxLength) {
                maxLength = word.length();
            }
        }
        generate(maxLength);
    }

    /**
     * Prints the board out using newlines.
     *
     * @return A string representation of the board
     */
    public String toString() {
        String result = "";
        for (int y = 0; y < board.length; y++) {
            result += getRow(y) + "\n";
        }
        return result;
    }

    /**
     * Fills empty cells with random letters.
     */
    public void fillEmpty() {
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (isEmpty(x, y)) {
                    int i = RNG.nextInt(CHARACTER_WEIGHT.length());
                    board[y][x] = CHARACTER_WEIGHT.charAt(i);
                }
            }
        }
    }
    /**
     * Gets row to print.
     * @param y index of row
     * @return Row joined by spaces
     */
    public String getRow(int y) {
        String line = "";
        for (int x = 0; x < board[y].length; x++) {
            line += board[y][x] + " ";
        }
        return line;
    }

    /**
     * Gets a board with labeled rows and columns.
     * @return board labeled at the top and left sides
     */
    public String getLabeled() {
        String result = "  ";
        for (int i = 0; i < board.length; i++) {
            result += ALPHA.charAt(i) + "_";
        }
        result = result.substring(0, result.length() - 1);
        result += "\n";
        for (int y = 0; y < board.length; y++) {
            result += ALPHA.charAt(y) + "|" + getRow(y) + "\n";
        }
        return result;
    }
    /**
     * Deals with all input and passes it onto answer() to evaluate.
     */
    public void promptAnswer() {
        System.out.print("First coords (eg. AF, DB) (?,L): ");
        String answer1 = INPUT.nextLine().toUpperCase();
        String answer2 = null;
        if (answer1.length() == 2) {
            System.out.print("Second coords: ");
            answer2 = INPUT.nextLine().toUpperCase();
        } else {
            if (answer1.equals("?")) {
                System.out.println(getHelp());
            } else if (answer1.equals("L")) {
                System.out.println(getList());
            }
            enterToContinue();
            return;
        }

        if (answer2.length() != 2) {
            System.out.println("Invalid input");
            return;
        }

        int x1 = ALPHA.indexOf(answer1.charAt(1));
        int y1 = ALPHA.indexOf(answer1.charAt(0));
        int x2 = ALPHA.indexOf(answer2.charAt(1));
        int y2 = ALPHA.indexOf(answer2.charAt(0));

        boolean isCorrect = answer(x1, y1, x2, y2);

        if (isCorrect) {
            System.out.println("Nice job! You found one!");
        } else {
            System.out.println("No dice.");
        }
        enterToContinue();
    }

    /**
     * Pauses execution and asks the user to continue when ready.
     */
    public static void enterToContinue() {
        System.out.println("Please press enter to continue.");
        INPUT.nextLine();
    }

    /**
     * Gets list of words, along with a found identifier.
     * @return List of words, each marked by an X if found, O if not
     */
    public String getList() {
        String result = "";
        for (String word : words) {
            if (foundWords.contains(word)) {
                result += "X ";
            } else {
                result += "O ";
            }
            result += word + "\n";
        }
        return result;
    }
    /**
     * Checks the board to see whether the given coordinates spell a word,
     * capitalize that word, and append it to found words.
     * Caution coders: I use an atrocious method of fixing an issue below. You have been forewarned.
     * @return True if the user input a correct word, false otherwise
     */
    public boolean answer(int x1, int y1, int x2, int y2) {
        if (!isValid(x1, y1) || !isValid(x2, y2)) {
            return false;
        }
        String spelled = "";
        int x = x1;
        int y = y1;
        while (x != x2 || y != y2) {
            spelled += board[y][x];
            x += Math.signum(x2 - x);
            y += Math.signum(y2 - y);
        }
        spelled += board[y][x];
        spelled = spelled.toLowerCase();

        for (String word : words) {
            String reverse = "";
            for (int i = spelled.length() - 1; i >= 0; i--) {
                reverse += spelled.charAt(i);
            }
            if (spelled.equals(word) || reverse.equals(word)) {
                x = x1;
                y = y1;
                while (x != x2 || y != y2) {
                    board[y][x] = Character.toUpperCase(board[y][x]);
                    x += Math.signum(x2 - x);
                    y += Math.signum(y2 - y);
                }
                board[y][x] = Character.toUpperCase(board[y][x]);
                foundWords.add(word);
                return true;
            }
        }
        return false;
    }

    /**
     * Starts the game and finishes it when you win.
     */
    public void start() {
        while (true) {
            System.out.println();
            System.out.println(getLabeled());
            promptAnswer();
            if (foundWords.size() == words.length) {
                long now = (new Date()).getTime();
                long time = now - startTime;
                System.out.println("\n***You win!***");
                System.out.println("Difficulty: " + getDifficulty());
                System.out.println("Words completed: " + words.length);
                System.out.println("Time: " + (int) (time / 1000) + " seconds");
                break;
            }
        }
    }

    /**
     * Keeps generating a new board until the player is satisfied.
     */
    public void getDesiredBoard() {
        String answer = "r";
        while (answer.equals("r")) {
            generate();
            System.out.println("\n" + toString());
            System.out.print(getInfo());
            System.out.println("Press Enter to start or enter R to generate a new board.");
            answer = INPUT.nextLine().toLowerCase();
        }
    }

    /**
     * Prints info about board.
     * @return size, difficulty
     */
    public String getInfo() {
        String result = "";
        int size = board.length;

        result += "Size: " + size + "x" + size + "\n";
        result += "Difficulty: " + getDifficulty() + "\n";
        return result;
    }

    /**
     * Gets difficulty ranging from Very Easy to Extremely Hard.
     * @return difficulty
     */
    // TODO: include number of words
    public String getDifficulty() {
        int size = board.length;
        String difficulty = null;
        if (size <= 6) {
            difficulty = "Very Easy";
        } else if (size <= 9) {
            difficulty = "Easy";
        } else if (size <= 15) {
            difficulty = "Medium";
        } else if (size <= 18) {
            difficulty = "Hard";
        } else if (size <= 21) {
            difficulty = "Very Hard";
        } else {
            difficulty = "Extremely Hard";
        }
        return difficulty;
    }

    /**
     * Gets instructions in the form of a string to print.
     * @return Instructions
     */
    public String getHelp() {
        return "When asked for an answer, type in the coordinate pair of one end of a word you find in the puzzle.\n"
        + "Coordinates are done row, then column. For a character in row D and column C, the answer would be DC.\n"
        + "Then type the pair of the letter at the end of the word, and if it's right, you will have completed that word.";
    }

    /**
     * Initializes and starts the game.
     * 
     * @param args Optionally give a filename argument to override the default wordlist file
     */
    public static void main(String[] args) {
        String filename = "default.txt";
        if (args.length > 0) {
            filename = args[0];
        }
        WordSearch game = new WordSearch(filename);
        game.generate();
        game.getDesiredBoard();
        game.start();
    }
}