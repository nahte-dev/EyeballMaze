package eyeBallMaze;

public class MoveRecord {
	private int row;
	private int column;
	private Direction direction;
	
	public MoveRecord(int theRow, int theColumn, Direction theDirection) {
		this.row = theRow;
		this.column = theColumn;
		this.direction = theDirection;
	}
	
	public int getRowRecord() {
		return this.row;
	}
	
	public int getColumnRecord() {
		return this.column;
	}

	public Direction getDirectionRecord() { return this.direction; }
}
