package eyeBallMaze;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class EyeballController implements Parcelable {
	Game game;
	
	public EyeballController(Game theGame) {
		this.game = theGame;
	}

	protected EyeballController(Parcel in) {
		this.game = new Game();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<EyeballController> CREATOR = new Creator<EyeballController>() {
		@Override
		public EyeballController createFromParcel(Parcel in) {
			return new EyeballController(in);
		}

		@Override
		public EyeballController[] newArray(int size) {
			return new EyeballController[size];
		}
	};

	public String getLevelName() {
		return this.game.getLevelName();
	}

	public int getGoalCount() {
		return this.game.getGoalCount();
	}

	public int getCompletedGoalCount() {
		return this.game.getCompletedGoalCount();
	}

	public void setLevel(int levelNumber) {
		this.game.setLevel(levelNumber);
	}

	public int getLevelHeight() {
		return this.game.getLevelHeight();
	}

	public int getLevelWidth() {
		return this.game.getLevelWidth();
	}

	public Shape getShapeAt(int row, int col) {
		return this.game.getShapeAt(row, col);
	}

	public Color getColorAt(int row, int col) {
		return this.game.getColorAt(row, col);
	}

	public boolean hasGoalAt(int r, int c) {
		return this.game.hasGoalAt(r, c);
	}

	public boolean hasPlayerAt(int r, int c) {
		return this.game.hasPlayerAt(r, c);
	}

	public int getEyeballPreviousColumn() {
		return game.getEyeballPreviousColumn();
	}

	public int getEyeballPreviousRow() {
		return game.getEyeballPreviousRow();
	}

	public void moveTo(int finalRow, int finalColumn) {
		game.moveTo(finalRow, finalColumn);
	}

	public int getMoveCount() {
		return game.getMoveCount();
	}

	public void addLevel(String name, int row, int column) {
		this.game.addLevel(name, row, column);
	}

	public void addSquare(Square square, int row, int column) {
		this.game.addSquare(square, row, column);
	}

	public void addEyeball(int row, int column, Direction direction) {
		this.game.addEyeball(row, column, direction);
	}

	public void addGoal(int row, int column) {
		this.game.addGoal(row, column);
	}

	public Direction getEyeballDirection() {
		return this.game.getEyeballDirection();
	}

	public Message MessageIfMovingTo(int destRow, int destColumn) {
		return this.game.MessageIfMovingTo(destRow, destColumn);
	}

	public Message checkDirectionMessage(int destRow, int destColumn) {
		return this.game.checkDirectionMessage(destRow, destColumn);
	}

	public Message checkMessageForBlankOnPathTo(int destRow, int destColumn) {
		return this.game.checkMessageForBlankOnPathTo(destRow, destColumn);
	}

	public int getEyeballRow() {
		return this.game.getEyeballRow();
	}

	public int getEyeballColumn() {
		return this.game.getEyeballColumn();
	}

	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		return this.game.canMoveTo(destinationRow, destinationColumn);
	}

	public void undoMove() {
		this.game.undoMove();
	}

	public ArrayList<MoveRecord> getMoveRecords() {
		return this.game.getMoveRecords();
	}

	public void setReplayPositions(int row, int column, Direction direction) {
		this.game.setReplayPositions(row, column, direction);
	}
}
