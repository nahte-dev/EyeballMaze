package eyeBallMaze;

public class Goal extends Square {
	protected int row;
	protected int col;
	
	public Goal(int goalRow, int goalColumn) {
		super.isGoal = true;
		this.row = goalRow;
		this.col = goalColumn;
	}

	@Override
	public void setBlank() {
		this.isBlank = false;
	}
}
