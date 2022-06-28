package com.example.dissertation2022;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.text.LineBreaker;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private Scene scene;
    private ModelRenderable modelRenderable;
    private ExternalTexture externalTexture;
    private MediaPlayer mediaPlayer;
    private MuseumArFragment museumArFragment;
    private Button playPauseButton;
    private Button exitButton;
    private Button recordingButton;
    private VideoRecorder videoRecorder;
    private Button infoButton;
    private PopupWindow infoPopup;
    private Button closeInfoButton;
    private TextView artworkDescription;
    private String artworkDescriptionString;

    private String TAG = "OCV";

    private String imageResourceName;
    private Anchor imageAnchor;
    private boolean isImageDetected = false;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseButton = findViewById(R.id.playPauseButton);
        exitButton = findViewById(R.id.exitButton);
        recordingButton = findViewById(R.id.recordingButton);
        infoButton = findViewById(R.id.infoButton);
        
        museumArFragment = (MuseumArFragment) getSupportFragmentManager().findFragmentById(R.id.museumArFragment);
        scene = museumArFragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);
    }

    private void onUpdate(FrameTime frameTime) {

        if(isImageDetected){
            return;
        }

        Frame frame = museumArFragment.getArSceneView().getArFrame();
        museumArFragment.getArSceneView().getPlaneRenderer().setVisible(false);

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        label:
        for(AugmentedImage augmentedImage : augmentedImages){
            if(augmentedImage.getTrackingState() == TrackingState.TRACKING &&
                    augmentedImage.getTrackingMethod() == AugmentedImage.TrackingMethod.FULL_TRACKING){
                imageResourceName = augmentedImage.getName();
                switch (imageResourceName) {
                    case "cat.png":
                        isImageDetected = true;
                        artworkDescriptionString = "This artwork depicts a colourful cat, painted by " +
                                "artist J. M. Schindier, who spent a large part of his creative years " +
                                "living in a flat that was owned by a vast amount of feline species.";
                        renderAndPlayVideo(augmentedImage, R.raw.cat);
                        break label;
                    case "boat.png":
                        isImageDetected = true;
                        artworkDescriptionString = "Fishing Boats on the Beach at Les Saintes-Maries-de-la-Mer " +
                                "was painted by Vincent van Gogh in June 1888. He would have preferred to make " +
                                "this painting on the beach, but he couldn't, because the fishermen put out to sea " +
                                "very early every morning. He did draw the boats there, however, and later made this painting at home.";
                        renderAndPlayVideo(augmentedImage, R.raw.boat);
                        break label;
                    case "city.png":
                        isImageDetected = true;
                        artworkDescriptionString = "The Yellow House (The Street) was painted in September 1888 " +
                                "by Vincent van Gogh. In May 1888, Van Gogh rented four rooms in a house in Southern " +
                                "France. The green shutters in this painting of the square show where he lived. " +
                                "His plan was to turn the yellow corner-building into an artists’ house, where like-minded " +
                                "painters could live and work together.";
                        renderAndPlayVideo(augmentedImage, R.raw.city);
                        break label;
                    case "house.png":
                        isImageDetected = true;
                        artworkDescriptionString = "The White House at Night was painted by Vincent van Gogh " +
                                "just six weeks before his death in July 1890.";
                        renderAndPlayVideo(augmentedImage, R.raw.walking);
                        break label;
                    case "kid.png":
                        isImageDetected = true;
                        artworkDescriptionString = "First Steps, after Millet was painted by Vincent van Gogh " +
                                "in 1890, which is inspired after the painting with the same name by the artist " +
                                "Jean-François Millet.";
                        renderAndPlayVideo(augmentedImage, R.raw.kid);
                        break label;
                    case "starry.png":
                        isImageDetected = true;
                        artworkDescriptionString = "The Starry Night is probably one of van Gogh's most recognized " +
                                "paintings, and was done in June 1889.";
                        renderAndPlayVideo(augmentedImage, R.raw.starry);
                        break label;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exitButton.performClick();
    }

    private void showARExperienceButtons(AnchorNode anchorNode){

        playPauseButton.setOnClickListener(view -> togglePlayPauseVideo());
        playPauseButton.setVisibility(View.VISIBLE);
        infoButton.setVisibility(View.VISIBLE);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setOnClickListener(view -> {
            hideARExperience(anchorNode);
        });
        infoButton.setOnClickListener(view -> {

            View popupView = getLayoutInflater().inflate(R.layout.info_popup, null);

            infoPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, 900);
            infoPopup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
            infoPopup.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

            closeInfoButton = popupView.findViewById(R.id.popupClose);
            artworkDescription = popupView.findViewById(R.id.artworkDescription);
            artworkDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            artworkDescription.setText(artworkDescriptionString);

            closeInfoButton.setOnClickListener(innerView -> {
                infoPopup.dismiss();
            });

        });

        recordingButton.setVisibility(View.VISIBLE);
        playPauseButton.setText("PAUSE");
        scene.addChild(anchorNode);

        recordingButton.setOnClickListener(view -> {
            if(videoRecorder == null){
                videoRecorder = new VideoRecorder();
                videoRecorder.setSceneView(museumArFragment.getArSceneView());
                int orientation = getResources().getConfiguration().orientation;
                videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation);
            }

            isRecording = videoRecorder.onToggleRecord();
            if(isRecording) {
                recordingButton.setText("Stop Recording");
                Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();
            }
            else{
                recordingButton.setText("Start Recording");
                Toast.makeText(this, "Recording stopped.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideARExperience(AnchorNode anchorNode){
        playPauseButton.setVisibility(View.INVISIBLE);
        exitButton.setVisibility(View.INVISIBLE);
        recordingButton.setVisibility(View.INVISIBLE);
        infoButton.setVisibility(View.INVISIBLE);

        mediaPlayer.reset();
        mediaPlayer.release();

        museumArFragment.getArSceneView().getScene().removeChild(anchorNode);

        isImageDetected = false;

    }

    private void togglePlayPauseVideo(){
        if(mediaPlayer.isPlaying()){
            playPauseButton.setText("PLAY");
            mediaPlayer.pause();
        }
        else{
            playPauseButton.setText("PAUSE");
            mediaPlayer.start();
        }
    }

    private void renderAndPlayVideo(AugmentedImage augmentedImage, int videoResourceID){

        externalTexture = new ExternalTexture();
        mediaPlayer = MediaPlayer.create(this, videoResourceID);
        mediaPlayer.setSurface(externalTexture.getSurface());
        mediaPlayer.setLooping(true);

        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("video_screen.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    modelRenderable.getMaterial().setExternalTexture("videoTexture", externalTexture);
                    modelRenderable.getMaterial().setFloat4("keyColor", new Color(0.01843f, 1f, 0.098f));
                    this.modelRenderable = modelRenderable;
                });

        mediaPlayer.start();

        imageAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
        float extentX = augmentedImage.getExtentX();
        float extentZ = augmentedImage.getExtentZ();

        AnchorNode anchorNode = new AnchorNode(imageAnchor);

        externalTexture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            Handler handler = new Handler();
            Runnable runnable = () -> {
                anchorNode.setRenderable(modelRenderable);
                externalTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                showARExperienceButtons(anchorNode);
            };
            handler.postDelayed(runnable, 500);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 0f, extentZ));
    }
}