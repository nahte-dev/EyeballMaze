package eyeBallMaze;

public interface IMovingExt extends IMoving {
	default void calculateDirection(Eyeball player, int destinationRow, int destinationColumn) {
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
	};
}
