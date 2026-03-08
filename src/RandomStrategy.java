import java.util.Random;

/**
 * Random strategy for Rock Paper Scissors.
 * Randomly selects Rock, Paper, or Scissors with equal probability.
 * Implemented as an external class since it does not require
 * access to additional game data.
 *
 * @author Lukas Pestifanos
 */
public class RandomStrategy implements Strategy
{
    /** Random number generator */
    private Random rand = new Random();

    /**
     * Returns a random move regardless of the player's move.
     *
     * @param playerMove the player's move (not used in random selection)
     * @return a random move ("R", "P", or "S")
     */
    @Override
    public String getMove(String playerMove)
    {
        int choice = rand.nextInt(3);
        switch (choice)
        {
            case 0:
                return "R";
            case 1:
                return "P";
            case 2:
                return "S";
            default:
                return "R";
        }
    }
}