package eyeBallMaze;

public class Position {
	private int row;
	private int column;
	
	public Position(int theRow, int theColumn) {
		this.row = theRow;
		this.column = theColumn;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getColumn() {
		return this.column;
	}
}
