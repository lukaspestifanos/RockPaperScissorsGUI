/**
 * Cheat strategy for Rock Paper Scissors.
 * Always picks the move that beats the player's move.
 * Implemented as an external class since it does not require
 * access to additional game data.
 *
 * @author Lukas Pestifanos
 */
public class Cheat implements Strategy
{
    /**
     * Returns the move that beats the player's move.
     *
     * @param playerMove the player's move ("R", "P", or "S")
     * @return the computer's winning move
     */
    @Override
    public String getMove(String playerMove)
    {
        String computerMove = "";
        switch (playerMove)
        {
            case "R":
                computerMove = "P";
                break;
            case "P":
                computerMove = "S";
                break;
            case "S":
                computerMove = "R";
                break;
            default:
                computerMove = "X";
                break;
        }
        return computerMove;
    }
}