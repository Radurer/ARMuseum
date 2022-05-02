package com.example.dissertation2022;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class MuseumArFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
        aid.addImage("pic1", img);

        Bitmap img2 = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
        aid.addImage("pic2", img2);

        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);
        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);


        return frameLayout;
    }
}
