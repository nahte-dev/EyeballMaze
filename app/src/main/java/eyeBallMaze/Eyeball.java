package eyeBallMaze;

public class Eyeball {
	protected int currentRow;
	protected int currentColumn;
	protected int previousRow;
	protected int previousColumn;
	protected Direction currentDirection;
	
	public Eyeball(int row, int column, Direction direction) {
		this.currentRow = row;
		this.currentColumn = column;
		this.currentDirection = direction;
	}
}
