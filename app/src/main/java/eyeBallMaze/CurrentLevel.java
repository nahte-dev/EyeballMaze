package eyeBallMaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CurrentLevel extends AppCompatActivity {
    EyeballController controller;
    TableLayout levelLayoutTable;
    ImageView player;
    ImageView goal;
    int levelIndex;
    MediaPlayer victorySoundFX;
    MediaPlayer moveDeniedSoundFX;
    boolean audioToggle;
    boolean pauseToggle;
    long systemTime;
    long elapsedTime;
    long currentTime;
    long previousTime;
    Handler timerHandler;
    FrameLayout pauseMenu;
    int replayIndex;
    boolean timerStarted = false;

    // Color variables
    int blue = android.graphics.Color.BLUE;
    int red = android.graphics.Color.RED;
    int green = android.graphics.Color.GREEN;
    int yellow = android.graphics.Color.YELLOW;
    int purple = android.graphics.Color.argb(255,147,112,219);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.controller = getIntent().getParcelableExtra("controller");
        this.levelIndex = getIntent().getIntExtra("levelNumber", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_level);

        this.timerHandler = new Handler();
        this.systemTime = System.currentTimeMillis();
        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                currentTime = (System.currentTimeMillis() - systemTime) / 1000;
                if (timerStarted) {
                    TextView elapsedTimeTxt = findViewById(R.id.txt_timerNo);

                    elapsedTime += currentTime - previousTime;
                    elapsedTimeTxt.setText(String.format("%02d:%02d", elapsedTime / 60, elapsedTime % 60));
                }
                timerHandler.postDelayed(this, 1000L);
                previousTime = currentTime;
            }
        };
        this.stopTimer();
        timerHandler.postDelayed(timerRunnable, 500L);

        this.assemblePauseMenu();

        this.victorySoundFX = MediaPlayer.create(getApplicationContext(), R.raw.victory_sound);
        this.moveDeniedSoundFX = MediaPlayer.create(getApplicationContext(), R.raw.denied);

        this.createLevels();
        this.displayLevel(levelIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.undo_move:
                this.controller.undoMove();
                this.updatePlayerPosition(this.controller.getEyeballRow(), this.controller.getEyeballColumn());
                this.updatePlayerRotation();
                this.updateMoveCount();
                return true;
            case R.id.restart_game:
                this.restartLevel();
                return true;
            case R.id.pause_game:
                this.togglePause();
                if (pauseToggle) {
                    item.setIcon(R.drawable.ic_play_arrow_icon);
                }
                else {
                    item.setIcon(R.drawable.ic_pause_icon);
                }
                return true;
//            case R.id.main_menu:
//                return true;
//            case R.id.quit_game:
//                return true;
            case R.id.mute_audio:
                this.toggleAudio(getApplicationContext());
                if (audioToggle) {
                    item.setIcon(R.drawable.ic_baseline_volume_up_24);
                }
                else {
                    item.setIcon(R.drawable.ic_mute_icon);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void assemblePauseMenu() {
        LayoutInflater inflater = getLayoutInflater();
        this.pauseMenu = findViewById(R.id.layout_pause);
        View inflatedPause = inflater.inflate(R.layout.pause_menu, null);
        this.pauseMenu.addView(inflatedPause);
        this.pauseMenu.setVisibility(View.INVISIBLE);
        this.pauseMenu.setBackgroundResource(R.color.material_on_background_emphasis_high_type);
    }

    public void displayLevel(int levelNumber) {
        TextView levelHeader = findViewById(R.id.txt_levelName);
        TextView goalsLeft = findViewById(R.id.txt_goalsLeftNo);
        TextView goalsCompleted = findViewById(R.id.txt_goalsCompletedNo);

        this.controller.setLevel(levelNumber);

        levelHeader.setText(this.controller.getLevelName());
        goalsLeft.setText(String.valueOf(this.controller.getGoalCount()));
        goalsCompleted.setText(String.valueOf(this.controller.getCompletedGoalCount()));

        this.levelLayoutTable = findViewById(R.id.tbl_levelLayout);
        LayoutInflater inflater = getLayoutInflater();

        TableRow row;
        for (int r = 0; r < controller.getLevelHeight(); r++) {
            row = new TableRow(levelLayoutTable.getContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            for (int c = 0; c < controller.getLevelWidth(); c++) {
                View newSquare = inflater.inflate(R.layout.level_linear_layout, null);
                ImageButton button = newSquare.findViewById(R.id.btn_Square);
                this.goal = newSquare.findViewById(R.id.img_goal);
                this.player = newSquare.findViewById(R.id.img_player);

                int finalRow = r;
                int finalColumn = c;
                Shape currentShape = controller.getShapeAt(finalRow, finalColumn);
                Color currentColor = controller.getColorAt(finalRow, finalColumn);

                this.populateShapesAndColors(button, currentShape, currentColor);

                if (this.controller.hasGoalAt(r, c)) {
                    this.goal.setVisibility(View.VISIBLE);
                }

                if (this.controller.hasPlayerAt(r, c)) {
                    this.player.setVisibility(View.VISIBLE);
                }

                View.OnClickListener btnListener = v -> {
                    if (this.validateMove(finalRow, finalColumn)) {
                        controller.moveTo(finalRow, finalColumn);
                        this.updatePlayerPosition(finalRow, finalColumn);
                        this.updatePlayerRotation();
                        this.updateGoalPosition(controller.getEyeballPreviousRow(), controller.getEyeballPreviousColumn());
                        this.checkWinCondition();
                        this.updateMoveCount();

                        if (!this.timerStarted) {
                            this.startTimer();
                        }

                        goalsLeft.setText(String.valueOf(this.controller.getGoalCount()));
                        goalsCompleted.setText(String.valueOf(this.controller.getCompletedGoalCount()));
                    }
                };
                button.setOnClickListener(btnListener);
                row.addView(newSquare);
            }
            levelLayoutTable.addView(row);
        }
    }

    @SuppressLint("NewApi")
    public void populateShapesAndColors(ImageButton currentSquare, Shape currentShape, Color currentColor) {
        switch(currentShape) {
            case DIAMOND:
                currentSquare.setImageResource(R.drawable.diamond);
                break;
            case CROSS:
                currentSquare.setImageResource(R.drawable.cross);
                break;
            case FLOWER:
                currentSquare.setImageResource(R.drawable.flower);
                break;
            case LIGHTNING:
                currentSquare.setImageResource(R.drawable.lightning);
                break;
            case STAR:
                currentSquare.setImageResource(R.drawable.star);
                break;
            case BLANK:
                currentSquare.setImageResource(0);
                break;
        }

        switch(currentColor) {
            case BLUE:
                currentSquare.setImageTintList(ColorStateList.valueOf(this.blue));
                break;
            case RED:
                currentSquare.setImageTintList(ColorStateList.valueOf(this.red));
                break;
            case YELLOW:
                currentSquare.setImageTintList(ColorStateList.valueOf(this.yellow));
                break;
            case GREEN:
                currentSquare.setImageTintList(ColorStateList.valueOf(this.green));
                break;
            case PURPLE:
                currentSquare.setImageTintList(ColorStateList.valueOf(this.purple));
                break;
            case BLANK:
                currentSquare.setImageResource(0);
                break;
        }
    }

    public void updateMoveCount() {
        TextView moveCount = findViewById(R.id.txt_moveCountNo);
        moveCount.setText(String.valueOf(this.controller.getMoveCount()));
    }

    public boolean validateMove(int destRow, int destColumn) {
        Boolean validMove;
        Message moveMessage = controller.MessageIfMovingTo(destRow, destColumn);
        Message directionMessage = controller.checkDirectionMessage(destRow, destColumn);
        Message blankMessage = controller.checkMessageForBlankOnPathTo(destRow, destColumn);

        if (moveMessage != Message.OK) {
            validMove = false;
            this.displayRules(moveMessage);
        }
        else if (directionMessage != Message.OK) {
            validMove = false;
            this.displayRules(directionMessage);
        }
        else if (blankMessage != Message.OK) {
            validMove = false;
            this.displayRules(blankMessage);
        }
        else {
            validMove = true;
        }
        return validMove;
    }

    public void displayRules(Message errorMessage) {
        AlertDialog.Builder rulesDialog = new AlertDialog.Builder(this);
        rulesDialog.setCancelable(true);
        rulesDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        rulesDialog.setNeutralButton("Show Rules", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        switch (errorMessage) {
            case DIFFERENT_SHAPE_OR_COLOR:
                rulesDialog.setTitle("Incompatible Square!");
                rulesDialog.setMessage("You can only move to squares with the same color or shape");
                break;
            case BACKWARDS_MOVE:
                rulesDialog.setTitle("Backwards Move!");
                rulesDialog.setMessage("You cannot move backwards");
                break;
            case MOVING_DIAGONALLY:
                rulesDialog.setTitle("Diagonal Move!");
                rulesDialog.setMessage("You cannot move diagonally");
                break;
            case MOVING_OVER_BLANK:
                rulesDialog.setTitle("Blank Path!");
                rulesDialog.setMessage("You cannot move over blank spaces");
                break;
        }
        if (!this.audioToggle) {
            this.moveDeniedSoundFX.start();
        }

        rulesDialog.create();
        rulesDialog.show();
    }

    public void updateGoalPosition(int playerPreviousRow, int playerPreviousColumn) {
        if (this.controller.hasGoalAt(playerPreviousRow, playerPreviousColumn)) {
            TableRow row = (TableRow) this.levelLayoutTable.getChildAt(playerPreviousRow);
            View button = row.getChildAt(playerPreviousColumn);
            ImageView goalIcon = button.findViewById(R.id.img_goal);
            ImageButton square = button.findViewById(R.id.btn_Square);

            square.setImageResource(0);
            goalIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void updatePlayerPosition(int playerRow, int playerColumn) {
        for (int r = 0; r < controller.getLevelHeight(); r++) {
            TableRow row = (TableRow) this.levelLayoutTable.getChildAt(r);
            for (int c = 0; c < controller.getLevelWidth(); c++) {
                View button = row.getChildAt(c);
                button.findViewById(R.id.img_player).setVisibility(View.INVISIBLE);
            }
        }

        if (this.controller.hasPlayerAt(playerRow, playerColumn)) {
            TableRow row = (TableRow) this.levelLayoutTable.getChildAt(playerRow);
            View button = row.getChildAt(playerColumn);
            this.player = button.findViewById(R.id.img_player);
            this.player.setVisibility(View.VISIBLE);
        }
    }

    public void updatePlayerRotation() {
        Direction currentDirection = this.controller.getEyeballDirection();

        this.setSpriteDirection(currentDirection);
    }

    public void checkWinCondition() {
        if (this.hasPlayerWon()) {
            AlertDialog.Builder winDialog = new AlertDialog.Builder(this);
            winDialog.setCancelable(true);
            winDialog.setTitle("You Won!");
            winDialog.setMessage("Congratulations. You can load the next level or go back to the main menu");
            winDialog.setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadNextLevel();
                }
            });
            winDialog.setNegativeButton("Restart Level", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartLevel();
                }
            });
            winDialog.setNeutralButton("Watch Replay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startPlayback();
                }
            });
            if (!this.audioToggle) {
                this.victorySoundFX.start();
            }

            winDialog.create();
            winDialog.show();
        }
        else if (this.hasPlayerLost()) {
            AlertDialog.Builder lostDialog = new AlertDialog.Builder(this);
            lostDialog.setCancelable(true);
            lostDialog.setTitle("Game Over!");
            lostDialog.setMessage("There are no squares you can move to from here");
            lostDialog.setPositiveButton("Restart Level", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restartLevel();
                }
            });
            lostDialog.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            lostDialog.create();
            lostDialog.show();
        }
    }

    public void startPlayback() {
        Handler replayHandler = new Handler();

        final Runnable replayDelay = new Runnable() {
            @Override
            public void run() {
                ArrayList<MoveRecord> playerMoves = controller.getMoveRecords();
                MoveRecord currentMove = playerMoves.get(replayIndex);

                controller.setReplayPositions(currentMove.getRowRecord(),
                        currentMove.getColumnRecord(), currentMove.getDirectionRecord());

                updatePlayerPosition(currentMove.getRowRecord(), currentMove.getColumnRecord());
                setSpriteDirection(currentMove.getDirectionRecord());

                if (replayIndex < playerMoves.size() -1) {
                    replayIndex++;
                    replayHandler.postDelayed(this, 500);
                }
            }
        };
        replayHandler.post(replayDelay);
    }

    public void setSpriteDirection(Direction direction) {
        switch (direction) {
            case UP:
                this.player.setRotation(360);
                break;
            case DOWN:
                this.player.setRotation(180);
                break;
            case LEFT:
                this.player.setRotation(270);
                break;
            case RIGHT:
                this.player.setRotation(90);
                break;
            default:
                break;
        }
    }

    public void loadNextLevel() {
        this.levelIndex++;

        finish();
        Intent intent = getIntent();
        intent.putExtra("levelNumber", this.levelIndex);
        startActivity(getIntent());
    }

    public void restartLevel() {
        finish();
        Intent intent = getIntent();
        intent.putExtra("levelNumber", this.levelIndex);
        startActivity(getIntent());
    }

    public boolean hasPlayerWon() {
        if (this.controller.getGoalCount() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hasPlayerLost() {
        int movableOptions = calculateMovableOptions();

        if (movableOptions == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public int calculateMovableOptions() {
        int upMove = 0;
        int downMove = 0;
        int rightMove = 0;
        int leftMove = 0;
        int sumMovable;

        for (int row = controller.getEyeballRow() - 1; row >= 0; row--) {
            if (controller.canMoveTo(row, controller.getEyeballColumn())) {
                upMove++;
            }
        }

        for (int row = controller.getEyeballRow() + 1; row < controller.getLevelHeight(); row++) {
            if (controller.canMoveTo(row, controller.getEyeballColumn())) {
                downMove++;
            }
        }

        for (int column = controller.getEyeballColumn() - 1; column >= 0; column--) {
            if (controller.canMoveTo(controller.getEyeballRow(), column)) {
                leftMove++;
            }
        }

        for (int column = controller.getEyeballColumn() + 1; column < controller.getLevelWidth(); column++) {
            if (controller.canMoveTo(controller.getEyeballRow(), column)) {
                rightMove++;
            }
        }

        switch (controller.getEyeballDirection()) {
            case UP:
                downMove = 0;
                break;
            case DOWN:
                upMove = 0;
                break;
            case RIGHT:
                leftMove = 0;
                break;
            case LEFT:
                rightMove = 0;
                break;
        }

        sumMovable = upMove + downMove + rightMove + leftMove;
        return sumMovable;
    }

    public void pauseLevel() {
        if (this.pauseMenu.getVisibility() == View.VISIBLE) {
            this.pauseMenu.setVisibility(View.INVISIBLE);
            this.startTimer();
        }
        else {
            this.pauseMenu.setVisibility(View.VISIBLE);
            this.stopTimer();
        }
    }

    public void toggleAudio(Context context) {
        this.audioToggle = !this.audioToggle;
    }

    public void togglePause() {
        this.pauseLevel();
        this.pauseToggle = !this.pauseToggle;
    }

    public void startTimer() {
        timerStarted = true;
    }

    public void stopTimer() {
        timerStarted = false;
    }

    public void createLevel(String name, int row, int column) {
        this.controller.addLevel(name, row, column);
    }

    public void createLevels() {
        this.assembleLevelOne();
        this.assembleLevelTwo();
        this.assembleLevelThree();
    }

    public void assembleLevelOne() {
        this.createLevel("Level One",6, 4);
        this.controller.setLevel(0);

        // Row 1
        this.controller.addSquare(new BlankSquare(), 0, 0);
        this.controller.addSquare(new BlankSquare(), 0, 1);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 0, 2);
        this.controller.addSquare(new BlankSquare(), 0, 3);
        // Row 2
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 1, 0);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 1, 1);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 1, 2);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 1, 3);
        // Row 3
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 2, 0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 2, 1);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.STAR), 2, 2);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 2, 3);
        // Row 4
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 3, 0);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 3, 1);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 3, 2);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 3, 3);
        // Row 5
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.STAR), 4, 0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 4, 1);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 4, 2);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 4, 3);
        // Row 6
        this.controller.addSquare(new BlankSquare(), 5, 0);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 5, 1);
        this.controller.addSquare(new BlankSquare(), 5, 2);
        this.controller.addSquare(new BlankSquare(), 5, 3);
        // Spawn Player
        this.controller.addEyeball(5, 1, Direction.UP);
        // Spawn Goal
        this.controller.addGoal(0, 2);
    }

    public void assembleLevelTwo() {
        this.createLevel("Level Two",6, 4);
        this.controller.setLevel(1);

        // Row 1
        this.controller.addSquare(new BlankSquare(), 0, 0);
        this.controller.addSquare(new BlankSquare(), 0, 1);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 0, 2);
        this.controller.addSquare(new BlankSquare(), 0, 3);
        // Row 2
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 1, 0);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 1, 1);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 1, 2);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 1, 3);
        // Row 3
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 2, 0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 2, 1);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.STAR), 2, 2);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 2, 3);
        // Row 4
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 3, 0);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.DIAMOND), 3, 1);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 3, 2);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.STAR), 3, 3);
        // Row 5
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 4, 0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 4, 1);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 4, 2);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.DIAMOND), 4, 3);
        // Row 6
        this.controller.addSquare(new BlankSquare(), 5, 0);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 5, 1);
        this.controller.addSquare(new BlankSquare(), 5, 2);
        this.controller.addSquare(new BlankSquare(), 5, 3);
        // Spawn Player
        this.controller.addEyeball(5, 1, Direction.UP);
        // Spawn Goal
        this.controller.addGoal(0, 2);
    }

    public void assembleLevelThree() {
        this.createLevel("Level Three", 6, 5);
        this.controller.setLevel(2);

        // Row 1
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 0,0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 0,1);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 0,2);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.STAR), 0,3);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.CROSS), 0,4);
        // Row 2
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.CROSS), 1,0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 1,1);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.STAR), 1,2);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 1,3);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 1,4);
        // Row 3
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.CROSS), 2,0);
        this.controller.addSquare(new BlankSquare(), 2,1);
        this.controller.addSquare(new BlankSquare(), 2,2);
        this.controller.addSquare(new BlankSquare(), 2,3);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 2,4);
        // Row 4
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 3,0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.CROSS), 3,1);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 3,2);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 3,3);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.CROSS), 3,4);
        // Row 5
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 4,0);
        this.controller.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 4,1);
        this.controller.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 4,2);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 4,3);
        this.controller.addSquare(new PlayableSquare(Color.YELLOW, Shape.CROSS), 4,4);
        // Row 6
        this.controller.addSquare(new BlankSquare(), 5,0);
        this.controller.addSquare(new BlankSquare(), 5,1);
        this.controller.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 5,2);
        this.controller.addSquare(new BlankSquare(), 5,3);
        this.controller.addSquare(new BlankSquare(), 5,4);
        // Spawn Player
        this.controller.addEyeball(5,2, Direction.UP);
        // Spawn Goal
        this.controller.addGoal(0, 1);
        this.controller.addGoal(0, 3);
    }
}