/**
 * Strategy interface for Rock Paper Scissors game.
 * Defines a single method for determining the computer's move.
 * This is an implementation of the Strategy Design Pattern.
 *
 * @author Lukas Pestifanos
 */
public interface Strategy
{
    /**
     * Determines the computer's move based on the player's move.
     *
     * @param playerMove the player's move ("R", "P", or "S")
     * @return the computer's move ("R", "P", or "S")
     */
    public String getMove(String playerMove);
}