package eyeBallMaze;

import android.util.Log;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Level implements ISquareHolder, IGoalHolder, IEyeballHolder, IMoving {
	private String levelName;
	private int levelWidth;
	private int levelHeight;
	private int goalCount;
	private int goalCompletedCount;
	private int moveCount;
	private ArrayList<MoveRecord> playerMoves;
	
	protected Eyeball player;
	protected Square levelLayout[][];
	protected ISquare layout = (theSquare, theRow, theColumn) -> this.levelLayout[theRow][theColumn] = theSquare;
	
	public Level(String levelName, int height, int width) {
		this.levelName = levelName;
		this.levelHeight = height;
		this.levelWidth = width;
		
		this.levelLayout = new Square[this.levelHeight][this.levelWidth];
		this.playerMoves = new ArrayList<MoveRecord>();
	}

	public int getMoveCount() { return this.moveCount; }

	public String getLevelName() { return this.levelName; }

	public int getLevelWidth() {
		return this.levelWidth;
	}
	
	public int getLevelHeight() {
		return this.levelHeight;
	}

	public ArrayList<MoveRecord> getPlayerMoves() {
		return this.playerMoves;
	}

	@Override 
	public void addSquare(Square square, int row, int column) {
		this.layout.addSquare(square, row, column);
	}

	@Override
	public Color getColorAt(int row, int column) {
		return levelLayout[row][column].color;
	}

	@Override
	public Shape getShapeAt(int row, int column) {
		return levelLayout[row][column].shape;
	}
	
	@Override
	public void addGoal(int row, int column) {
		Goal goal = new Goal(row, column);
		
		if (this.levelLayout[row][column] != null) {
			goal.color = this.levelLayout[row][column].color;
			goal.shape = this.levelLayout[row][column].shape;
		}
		
		this.levelLayout[row][column] = goal;
		this.goalCount++;
	}

	@Override
	public int getGoalCount() {
		return this.goalCount;
	}

	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		if (this.levelLayout[targetRow][targetColumn].isGoal) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int getCompletedGoalCount() {
		return this.goalCompletedCount;
	}

	@Override
	public void addEyeball(int row, int column, Direction direction) {
		this.player = new Eyeball(row, column, direction);
		this.setPlayerOnSquare(row, column);
	}

	@Override
	public int getEyeballRow() {
		return this.player.currentRow;
	}

	@Override
	public int getEyeballColumn() {
		return this.player.currentColumn;
	}

	public int getEyeballPreviousRow() { return this.player.previousRow; }

	public int getEyeballPreviousColumn() { return this.player.previousColumn; }

	@Override
	public Direction getEyeballDirection() {
		return this.player.currentDirection;
	}

	public boolean hasPlayerAt(int targetRow, int targetColumn) {
		if (this.levelLayout[targetRow][targetColumn].hasPlayer) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		if (this.levelLayout[this.player.currentRow][this.player.currentColumn].shape 
				== this.levelLayout[destinationRow][destinationColumn].shape
				|| this.levelLayout[this.player.currentRow][this.player.currentColumn].color
				== this.levelLayout[destinationRow][destinationColumn].color) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public Message MessageIfMovingTo(int destinationRow, int destinationColumn) {
		if (!this.canMoveTo(destinationRow, destinationColumn)) {
			return Message.DIFFERENT_SHAPE_OR_COLOR;
		}
		else {
			return Message.OK;
		}
	}

	@Override
	public boolean isDirectionOK(int destinationRow, int destinationColumn) {
		// Checking for diagonal movement to return correct error message 
		if (this.isDiagonal(destinationRow, destinationColumn)) {
			return false;
		}
		
		if ((destinationRow < this.player.currentRow && this.player.currentDirection != Direction.DOWN) ||
				(destinationRow > this.player.currentRow && this.player.currentDirection != Direction.UP) ||
				(destinationColumn < this.player.currentColumn && this.player.currentDirection != Direction.RIGHT) ||
				(destinationColumn > this.player.currentColumn && this.player.currentDirection != Direction.LEFT))
				{
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isDiagonal(int destinationRow, int destinationColumn) {
		if ((destinationRow != this.player.currentRow) && (destinationColumn != this.player.currentColumn)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
		if (!this.isDirectionOK(destinationRow, destinationColumn)) {
			if (!this.isDiagonal(destinationRow, destinationColumn)) {
				return Message.BACKWARDS_MOVE;
			}
			else {
				return Message.MOVING_DIAGONALLY;
			}
		}
		else {
			return Message.OK;
		}
	}

	@Override
	public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
		boolean blankFree = true;

		switch (getEyeballDirection()) {
			case UP:
				for (int r = this.player.currentRow; r >= destinationRow; r--) {
					if (this.levelLayout[r][this.getEyeballColumn()].isBlank) {
						blankFree = false;
					}
				}
				break;
			case DOWN:
				for (int r = this.player.currentRow; r <= destinationRow; r++) {
					if (this.levelLayout[r][this.getEyeballColumn()].isBlank) {
						blankFree = false;
					}
				}
				break;
			case LEFT:
				for (int c = this.player.currentColumn; c >= destinationColumn; c--) {
					if (this.levelLayout[this.getEyeballRow()][c].isBlank) {
						blankFree = false;
					}
				}
				break;
			case RIGHT:
				for (int c = this.player.currentColumn; c <= destinationColumn; c++) {
					if (this.levelLayout[this.getEyeballRow()][c].isBlank) {
						blankFree = false;
					}
				}
				break;
		}
		return blankFree;
	}

	@Override
	public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
		if (!this.hasBlankFreePathTo(destinationRow, destinationColumn)) {
			return Message.MOVING_OVER_BLANK;
		}
		else {
			return Message.OK;
		}
	}

	@Override
	public void moveTo(int destinationRow, int destinationColumn) {
		this.addMoveToRecord(this.player.currentRow, this.player.currentColumn, this.player.currentDirection);

		this.player.previousRow = this.player.currentRow;
		this.player.previousColumn = this.player.currentColumn;

		this.calculateDirection(this.player, destinationRow, destinationColumn);
		this.player.currentRow = destinationRow;
		this.player.currentColumn = destinationColumn;
		this.moveCount++;

		this.setPlayerOnSquare(this.getEyeballRow(), this.getEyeballColumn());
		this.removePlayerFromSquare(this.player.previousRow, this.player.previousColumn);
		this.checkCurrentGoal();
		this.removeGoal(this.player.previousRow, this.player.previousColumn);
	}

	public void addMoveToRecord(int row, int column, Direction direction) {
		MoveRecord newRecord = new MoveRecord(row, column, direction);

		this.playerMoves.add(newRecord);
	}

	public void undoMove() {
		MoveRecord lastMove = playerMoves.get(playerMoves.size() -1);

		this.removePlayerFromSquare(this.getEyeballRow(), this.getEyeballColumn());
		this.setPosition(lastMove.getRowRecord(), lastMove.getColumnRecord());
		this.setDirection(lastMove.getDirectionRecord());

		this.playerMoves.remove(playerMoves.size() -1);
		this.moveCount--;
	}

	public void setPosition(int row, int column) {
		this.player.currentRow = row;
		this.player.currentColumn = column;

		this.setPlayerOnSquare(row, column);
	}

	public void setDirection(Direction direction) {
		this.player.currentDirection = direction;
	}

	public void calculateDirection(Eyeball player, int destinationRow, int destinationColumn) {
		if (player.currentRow > destinationRow) {
			player.currentDirection = Direction.UP;
		}
		else if (player.currentRow < destinationRow) {
			player.currentDirection = Direction.DOWN;
		}
		else if (player.currentColumn > destinationColumn) {
			player.currentDirection = Direction.LEFT;
		}
		else {
			player.currentDirection = Direction.RIGHT;
		}
	}

	public void checkCurrentGoal() {
		if (this.hasGoalAt(this.player.currentRow, this.player.currentColumn)) {
			this.goalCompletedCount++;
			this.goalCount--;
		}

		if (this.goalCount == 0) {
			MoveRecord finalMove = new MoveRecord(this.getEyeballRow(),
					this.getEyeballColumn(), this.getEyeballDirection());

			this.playerMoves.add(finalMove);
		}
	}
	
	public void removeGoal(int previousRow, int previousColumn) {
		// Checking if player has moved from a goal and then removing the goal
		if (this.levelLayout[previousRow][previousColumn].isGoal) {
			this.levelLayout[previousRow][previousColumn].color = Color.BLANK;
			this.levelLayout[previousRow][previousColumn].shape = Shape.BLANK;
		}
	}

	public void setPlayerOnSquare(int row, int column) {
		// Setting square field to true if player is on it
		this.levelLayout[row][column].hasPlayer = true;
	}

	public void removePlayerFromSquare(int row, int column) {
		if (row == this.getEyeballRow() && column == this.getEyeballColumn()) {
			this.levelLayout[row][column].hasPlayer = true;
		}
		else {
			this.levelLayout[row][column].hasPlayer = false;
		}
	}
}
