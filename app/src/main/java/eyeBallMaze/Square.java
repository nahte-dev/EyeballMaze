package eyeBallMaze;

public abstract class Square {
	Color color;
	Shape shape;
	boolean isGoal;
	boolean isBlank;
	boolean hasPlayer;
	
	abstract void setBlank();
}
