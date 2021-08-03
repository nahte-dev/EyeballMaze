package eyeBallMaze;

import java.util.*;

public class Game implements ILevelHolder, ISquareHolder, IGoalHolder, IEyeballHolder, IMoving {
	int levelCount;
	protected List<Level> allLevels = new ArrayList<Level>();
	protected Level currentLevel;
	
	public Game() {

	}
	
	@Override
	public void addLevel(String name, int height, int width) {
		Level level = new Level(name, height, width);
		this.allLevels.add(level);
		this.currentLevel = level;
	}

	public String getLevelName() { return this.currentLevel.getLevelName(); }

	@Override
	public int getLevelWidth() {
		return this.currentLevel.getLevelWidth();
	}
	
	@Override
	public int getLevelHeight() {
		return this.currentLevel.getLevelHeight();
	}

	@Override
	public void setLevel(int levelNumber) {		
		if (levelNumber > getLevelCount()) {
			throw new IllegalArgumentException("Level does not exist!");
		} else {
			Level selectedLevel = this.allLevels.get(levelNumber);
			this.currentLevel = selectedLevel;
		}
	}

	@Override
	public int getLevelCount() {
		this.levelCount = this.allLevels.size();
		return levelCount;	
	}

	@Override
	public void addSquare(Square square, int row, int column) {
		if (column > getLevelWidth()) {
			throw new IllegalArgumentException("Outside level width!");
		}
		else if (row > getLevelHeight()) {
			throw new IllegalArgumentException("Outside level height!");
		}
		else {
			this.currentLevel.addSquare(square, row, column);
		}
	}

	@Override
	public Color getColorAt(int row, int column) {
		return this.currentLevel.getColorAt(row, column);
	}

	@Override
	public Shape getShapeAt(int row, int column) {
		return this.currentLevel.getShapeAt(row, column);
	}

	@Override
	public void addGoal(int row, int column) {
		if (column > getLevelWidth()) {
			throw new IllegalArgumentException("Outside level width!");
		}
		else if (row > getLevelHeight()) {
			throw new IllegalArgumentException("Outside level height!");
		}
		else {
			this.currentLevel.addGoal(row, column);
		}
	}

	@Override
	public int getGoalCount() {
		return this.currentLevel.getGoalCount();
	}

	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		return this.currentLevel.hasGoalAt(targetRow, targetColumn);
	}

	@Override
	public int getCompletedGoalCount() {
		return this.currentLevel.getCompletedGoalCount();
	}

	@Override
	public void addEyeball(int row, int column, Direction direction) {
		if (column > getLevelWidth()) {
			throw new IllegalArgumentException("Outside level width!");
		}
		else if (row > getLevelHeight()) {
			throw new IllegalArgumentException("Outside level height!");
		}
		else {
			this.currentLevel.addEyeball(row, column, direction);
		}
	}

	public int getMoveCount() { return this.currentLevel.getMoveCount(); }

	@Override
	public int getEyeballRow() {
		return this.currentLevel.getEyeballRow();
	}

	@Override
	public int getEyeballColumn() {
		return this.currentLevel.getEyeballColumn();
	}

	public int getEyeballPreviousRow() { return this.currentLevel.getEyeballPreviousRow(); }

	public int getEyeballPreviousColumn() { return this.currentLevel.getEyeballPreviousColumn(); }

	@Override
	public Direction getEyeballDirection() {
		return this.currentLevel.getEyeballDirection();
	}

	public boolean hasPlayerAt(int targetRow, int targetColumn) {
		return this.currentLevel.hasPlayerAt(targetRow, targetColumn);
	}

	@Override
	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		return this.currentLevel.canMoveTo(destinationRow, destinationColumn);
	}

	@Override
	public Message MessageIfMovingTo(int destinationRow, int destinationColumn) {
		return this.currentLevel.MessageIfMovingTo(destinationRow, destinationColumn);
	}

	@Override
	public boolean isDirectionOK(int destinationRow, int destinationColumn) {
		return this.currentLevel.isDirectionOK(destinationRow, destinationColumn);
	}

	@Override
	public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
		return this.currentLevel.checkDirectionMessage(destinationRow, destinationColumn);
	}

	@Override
	public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
		return this.currentLevel.hasBlankFreePathTo(destinationRow, destinationColumn);
	}

	@Override
	public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
		return this.currentLevel.checkMessageForBlankOnPathTo(destinationRow, destinationColumn);
	}

	@Override
	public void moveTo(int destinationRow, int destinationColumn) {
		this.currentLevel.moveTo(destinationRow, destinationColumn);
	}

	public void undoMove() {
		this.currentLevel.undoMove();
	}

	public ArrayList<MoveRecord> getMoveRecords() {
		return this.currentLevel.getPlayerMoves();
	}

	public void setReplayPositions(int row, int column, Direction direction) {
		this.currentLevel.setPosition(row, column);
		this.currentLevel.setDirection(direction);
	}
}
