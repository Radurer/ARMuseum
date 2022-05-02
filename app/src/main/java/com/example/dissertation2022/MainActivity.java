package com.example.dissertation2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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

    private boolean isImageDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        externalTexture = new ExternalTexture();
//        mediaPlayer = MediaPlayer.create(this, R.raw.vid1);
//        mediaPlayer.setSurface(externalTexture.getSurface());
//        mediaPlayer.setLooping(true);
//
//        ModelRenderable
//                .builder()
//                .setSource(this, Uri.parse("video_screen.sfb"))
//                .build()
//                .thenAccept(modelRenderable -> {
//                    modelRenderable.getMaterial().setExternalTexture("videoTexture", externalTexture);
//                    modelRenderable.getMaterial().setFloat4("keyColor", new Color(0.01843f, 1f, 0.098f));
//                    this.modelRenderable = modelRenderable;
//                });
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



        for(AugmentedImage augmentedImage : augmentedImages){
            if(augmentedImage.getTrackingState() == TrackingState.TRACKING){
                String imageResourceName = augmentedImage.getName();
                if(imageResourceName.equals("pic1")){
                    isImageDetected = true;
                    renderAndPlayVideo(augmentedImage, R.raw.vid1);
                    break;
                }
                else if(imageResourceName.equals("pic2")){
                    isImageDetected = true;
                    renderAndPlayVideo(augmentedImage, R.raw.vid2);
                    break;
                }
            }
        }

    }

    private void renderAndPlayVideo(AugmentedImage augmentedImage, int videoResourceID){
        externalTexture = new ExternalTexture();
        mediaPlayer = MediaPlayer.create(this, videoResourceID);//R.raw.vid1);
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

        Anchor imageAnchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
        float extentX = augmentedImage.getExtentX();
        float extentZ = augmentedImage.getExtentZ();

        AnchorNode anchorNode = new AnchorNode(imageAnchor);
        externalTexture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(modelRenderable);
            externalTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 0f, extentZ));

        scene.addChild(anchorNode);
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ) {
    }
}
