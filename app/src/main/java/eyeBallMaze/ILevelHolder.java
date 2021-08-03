package eyeBallMaze;

public interface ILevelHolder {
	public int getLevelWidth();
	public int getLevelHeight();
	public void setLevel(int levelNumber);
	public int getLevelCount();
	public void addLevel(String name, int height, int width);
}
