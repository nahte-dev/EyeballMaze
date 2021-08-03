package eyeBallMaze;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    EyeballController controller = new EyeballController(new Game());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, CurrentLevel.class);
        intent.putExtra("controller", this.controller);
        intent.putExtra("levelNumber", 2);
        startActivity(intent);
    }

    public void viewRules(View view) {
        Intent intent = new Intent(this, ViewRules.class);
        startActivity(intent);
    }
}