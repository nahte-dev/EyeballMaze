package eyeBallMaze;

public class BlankSquare extends Square {
	
	public BlankSquare() {
		this.color = Color.BLANK;
		this.shape = Shape.BLANK;
		this.isBlank = true;
	}
	
	@Override
	public final void setBlank() {
		this.isBlank = true;
	}
}
