import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Rock Paper Scissors GUI game with multiple computer strategies.
 * Uses the Strategy Design Pattern with both external and inner class implementations.
 * External strategies: Cheat, RandomStrategy
 * Inner class strategies: LeastUsed, MostUsed, LastUsed
 *
 * @author Lukas Pestifanos
 */
public class RockPaperScissorsFrame extends JFrame
{
    /** Text area to display game results */
    private JTextArea resultsArea;

    /** Text fields to display running stats */
    private JTextField playerWinsField;
    private JTextField computerWinsField;
    private JTextField tiesField;

    /** Running totals for player wins, computer wins, and ties */
    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    /** Tracks how many times the player has chosen each move */
    private int playerRockCount = 0;
    private int playerPaperCount = 0;
    private int playerScissorsCount = 0;

    /** Tracks the player's last move for the LastUsed strategy */
    private String lastPlayerMove = "";

    /** Total number of rounds played */
    private int totalRounds = 0;

    /** External strategy instances */
    private Strategy cheatStrategy = new Cheat();
    private Strategy randomStrategy = new RandomStrategy();

    /** Inner class strategy instances (initialized in constructor) */
    private Strategy leastUsedStrategy;
    private Strategy mostUsedStrategy;
    private Strategy lastUsedStrategy;

    /** Random number generator for strategy selection */
    private Random rand = new Random();

    // =========================================================================
    // Inner Class Strategies
    // These must be inner classes because they need access to the player's
    // move tracking data (playerRockCount, playerPaperCount, etc.)
    // =========================================================================

    /**
     * Inner class: Least Used Strategy.
     * Keeps track of the number of times the player uses each symbol.
     * Assumes the player will pick the symbol they have used the least.
     * Computer picks the symbol that beats the player's least used symbol.
     */
    class LeastUsed implements Strategy
    {
        /**
         * Returns the move that beats the player's least used move.
         *
         * @param playerMove the player's current move
         * @return the computer's counter move
         */
        @Override
        public String getMove(String playerMove)
        {
            String leastUsed;

            if (playerRockCount <= playerPaperCount && playerRockCount <= playerScissorsCount)
            {
                leastUsed = "R";
            }
            else if (playerPaperCount <= playerRockCount && playerPaperCount <= playerScissorsCount)
            {
                leastUsed = "P";
            }
            else
            {
                leastUsed = "S";
            }

            return beatMove(leastUsed);
        }
    }

    /**
     * Inner class: Most Used Strategy.
     * Keeps track of the number of times the player uses each symbol.
     * Assumes the player will pick the symbol they have used the most.
     * Computer picks the symbol that beats the player's most used symbol.
     */
    class MostUsed implements Strategy
    {
        /**
         * Returns the move that beats the player's most used move.
         *
         * @param playerMove the player's current move
         * @return the computer's counter move
         */
        @Override
        public String getMove(String playerMove)
        {
            String mostUsed;

            if (playerRockCount >= playerPaperCount && playerRockCount >= playerScissorsCount)
            {
                mostUsed = "R";
            }
            else if (playerPaperCount >= playerRockCount && playerPaperCount >= playerScissorsCount)
            {
                mostUsed = "P";
            }
            else
            {
                mostUsed = "S";
            }

            return beatMove(mostUsed);
        }
    }

    /**
     * Inner class: Last Used Strategy.
     * Uses the symbol that the player used on the last round (tit-for-tat).
     * This is the solution to the famous Prisoner's Dilemma in computer science.
     * On the first round, falls back to a random move.
     */
    class LastUsed implements Strategy
    {
        /**
         * Returns the symbol the player used on the last round.
         * On the first round, returns a random move since there is no history.
         *
         * @param playerMove the player's current move
         * @return the player's last move, or a random move on round one
         */
        @Override
        public String getMove(String playerMove)
        {
            if (lastPlayerMove.isEmpty())
            {
                // First round: no last move, fall back to random
                return randomStrategy.getMove(playerMove);
            }
            return lastPlayerMove;
        }
    }

    // =========================================================================
    // Constructor and GUI Setup
    // =========================================================================

    /**
     * Constructs the Rock Paper Scissors GUI frame.
     * Sets up the button panel, stats panel, and results display area.
     */
    public RockPaperScissorsFrame()
    {
        super("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Initialize inner class strategy instances
        leastUsedStrategy = new LeastUsed();
        mostUsedStrategy = new MostUsed();
        lastUsedStrategy = new LastUsed();

        // =====================================================================
        // Button Panel - Rock, Paper, Scissors, Quit with ImageIcons
        // =====================================================================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Choose Your Move"));

        JButton rockButton = createMoveButton("Rock", "rock.png", "R");
        JButton paperButton = createMoveButton("Paper", "paper.png", "P");
        JButton scissorsButton = createMoveButton("Scissors", "scissors.png", "S");
        JButton quitButton = createQuitButton();

        // Single ActionListener for the R, P, S buttons
        ActionListener moveListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String playerMove = ae.getActionCommand();
                playRound(playerMove);
            }
        };

        rockButton.addActionListener(moveListener);
        paperButton.addActionListener(moveListener);
        scissorsButton.addActionListener(moveListener);

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        // =====================================================================
        // Stats Panel - Player Wins, Computer Wins, Ties
        // =====================================================================
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 6, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Stats"));

        playerWinsField = createStatsField();
        computerWinsField = createStatsField();
        tiesField = createStatsField();

        statsPanel.add(new JLabel("Player Wins:"));
        statsPanel.add(playerWinsField);
        statsPanel.add(new JLabel("Computer Wins:"));
        statsPanel.add(computerWinsField);
        statsPanel.add(new JLabel("Ties:"));
        statsPanel.add(tiesField);

        // =====================================================================
        // Top panel combining buttons and stats
        // =====================================================================
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        // =====================================================================
        // Results Panel - JTextArea with JScrollPane
        // =====================================================================
        resultsArea = new JTextArea(16, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Results"));

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a move button with text and an ImageIcon.
     * Images are loaded from the classpath (src directory).
     *
     * @param text the button label text
     * @param iconFile the image file name (e.g. "rock.png")
     * @param actionCommand the action command string ("R", "P", or "S")
     * @return the configured JButton with icon
     */
    private JButton createMoveButton(String text, String iconFile, String actionCommand)
    {
        JButton button = new JButton(text);
        button.setActionCommand(actionCommand);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        try
        {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconFile));
            Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        catch (Exception e)
        {
            // If image file not found, button still works with text only
            System.out.println("Image not found: " + iconFile + " (button will display text only)");
        }

        return button;
    }

    /**
     * Creates the Quit button with a confirmation dialog.
     *
     * @return the configured Quit JButton
     */
    private JButton createQuitButton()
    {
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Arial", Font.BOLD, 14));

        try
        {
            ImageIcon icon = new ImageIcon(getClass().getResource("quit.png"));
            Image scaled = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            quitButton.setIcon(new ImageIcon(scaled));
            quitButton.setHorizontalTextPosition(SwingConstants.CENTER);
            quitButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        catch (Exception e)
        {
            // If image file not found, button still works with text only
        }

        quitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                int result = JOptionPane.showConfirmDialog(
                        RockPaperScissorsFrame.this,
                        "Are you sure you want to quit?",
                        "Quit Game",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                }
            }
        });

        return quitButton;
    }

    /**
     * Creates a read-only JTextField for the stats panel.
     *
     * @return a non-editable JTextField initialized to "0"
     */
    private JTextField createStatsField()
    {
        JTextField field = new JTextField("0", 5);
        field.setEditable(false);
        field.setFont(new Font("Arial", Font.BOLD, 16));
        field.setHorizontalAlignment(JTextField.CENTER);
        return field;
    }

    // =========================================================================
    // Game Logic
    // =========================================================================

    /**
     * Plays a single round of Rock Paper Scissors.
     * Updates player move counts, selects a strategy based on probability,
     * determines the result, and updates the display.
     *
     * @param playerMove the player's move ("R", "P", or "S")
     */
    private void playRound(String playerMove)
    {
        // Update player move counts before strategy selection
        switch (playerMove)
        {
            case "R":
                playerRockCount++;
                break;
            case "P":
                playerPaperCount++;
                break;
            case "S":
                playerScissorsCount++;
                break;
        }

        totalRounds++;

        // Select strategy based on probability (1-100 inclusive)
        //  1 - 10  Cheat
        // 11 - 30  Least Used
        // 31 - 50  Most Used
        // 51 - 70  Last Used
        // 71 - 100 Random
        int prob = rand.nextInt(100) + 1;
        String computerMove;
        String strategyName;

        if (prob <= 10)
        {
            computerMove = cheatStrategy.getMove(playerMove);
            strategyName = "Cheat";
        }
        else if (prob <= 30)
        {
            computerMove = leastUsedStrategy.getMove(playerMove);
            strategyName = "Least Used";
        }
        else if (prob <= 50)
        {
            computerMove = mostUsedStrategy.getMove(playerMove);
            strategyName = "Most Used";
        }
        else if (prob <= 70)
        {
            computerMove = lastUsedStrategy.getMove(playerMove);
            strategyName = "Last Used";
        }
        else
        {
            computerMove = randomStrategy.getMove(playerMove);
            strategyName = "Random";
        }

        // Determine the result and build the display string
        String resultLine = determineResult(playerMove, computerMove, strategyName);

        // Update last player move AFTER strategy selection (for LastUsed next round)
        lastPlayerMove = playerMove;

        // Append result to display area and auto-scroll to bottom
        resultsArea.append(resultLine + "\n");
        resultsArea.setCaretPosition(resultsArea.getDocument().getLength());

        // Update stats fields
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    /**
     * Determines the result of a round and updates win/loss/tie counts.
     * Returns a formatted result string matching the required display format:
     * "Rock breaks Scissors. (Player wins! Computer: Least Used)"
     *
     * @param playerMove the player's move ("R", "P", or "S")
     * @param computerMove the computer's move ("R", "P", or "S")
     * @param strategyName the name of the strategy used by the computer
     * @return a formatted result string for the display area
     */
    private String determineResult(String playerMove, String computerMove, String strategyName)
    {
        String playerName = moveName(playerMove);
        String computerName = moveName(computerMove);

        // Check for tie
        if (playerMove.equals(computerMove))
        {
            ties++;
            return playerName + " vs " + computerName + ". (Tie! Computer: " + strategyName + ")";
        }

        boolean playerWon = false;
        String action = "";

        if (playerMove.equals("R") && computerMove.equals("S"))
        {
            playerWon = true;
            action = "Rock breaks Scissors";
        }
        else if (playerMove.equals("P") && computerMove.equals("R"))
        {
            playerWon = true;
            action = "Paper covers Rock";
        }
        else if (playerMove.equals("S") && computerMove.equals("P"))
        {
            playerWon = true;
            action = "Scissors cuts Paper";
        }
        else if (computerMove.equals("R") && playerMove.equals("S"))
        {
            action = "Rock breaks Scissors";
        }
        else if (computerMove.equals("P") && playerMove.equals("R"))
        {
            action = "Paper covers Rock";
        }
        else if (computerMove.equals("S") && playerMove.equals("P"))
        {
            action = "Scissors cuts Paper";
        }

        if (playerWon)
        {
            playerWins++;
            return action + ". (Player wins! Computer: " + strategyName + ")";
        }
        else
        {
            computerWins++;
            return action + ". (Computer wins! Computer: " + strategyName + ")";
        }
    }

    /**
     * Returns the full name of a move from its abbreviation.
     *
     * @param move the move abbreviation ("R", "P", or "S")
     * @return the full move name ("Rock", "Paper", or "Scissors")
     */
    private String moveName(String move)
    {
        switch (move)
        {
            case "R":
                return "Rock";
            case "P":
                return "Paper";
            case "S":
                return "Scissors";
            default:
                return "Unknown";
        }
    }

    /**
     * Returns the move that beats the given move.
     * Used by the inner class strategies (LeastUsed, MostUsed).
     *
     * @param move the move to beat ("R", "P", or "S")
     * @return the winning counter-move
     */
    private String beatMove(String move)
    {
        switch (move)
        {
            case "R":
                return "P";    // Paper beats Rock
            case "P":
                return "S";    // Scissors beats Paper
            case "S":
                return "R";    // Rock beats Scissors
            default:
                return "R";
        }
    }

    /**
     * Main method. Launches the Rock Paper Scissors GUI
     * using SwingUtilities.invokeLater as required.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new RockPaperScissorsFrame();
            }
        });
    }
}