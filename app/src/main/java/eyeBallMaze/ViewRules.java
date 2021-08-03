package eyeBallMaze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class ViewRules extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rules);

        VideoView rulesVideo = findViewById(R.id.vid_rules);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.rules_video;
        Uri uri = Uri.parse(videoPath);
        rulesVideo.setVideoURI(uri);


        MediaController rulesMC = new MediaController(this);
        rulesVideo.setMediaController(rulesMC);
        rulesMC.setAnchorView(rulesVideo);

        rulesVideo.start();
    }

    public void returnToMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}