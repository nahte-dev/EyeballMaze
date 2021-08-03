package eyeBallMaze;

public class PlayableSquare extends Square {
	
	public PlayableSquare(Color theColor, Shape theShape) {
		this.color = theColor;
		this.shape = theShape;
	}

	@Override
	public void setBlank() {
		this.isBlank = false;
	}
}
