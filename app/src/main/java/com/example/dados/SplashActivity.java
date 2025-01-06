package com.example.dados;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar el diseño
        setContentView(R.layout.activity_splash);

        // Referenciar el VideoView
        VideoView videoView = findViewById(R.id.splashVideoView);

        // Cargar el video desde los recursos
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video);
        videoView.setVideoURI(videoUri);

        // Listener para ajustar el tamaño del video y evitar bordes negros
        videoView.setOnPreparedListener(mediaPlayer -> {
            // Hacer que el video ocupe toda la pantalla sin bordes
            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                videoView.setScaleX((float) videoView.getWidth() / width);
                videoView.setScaleY((float) videoView.getHeight() / height);
            });
        });

        // Listener para navegar al MainActivity al terminar el video
        videoView.setOnCompletionListener(mediaPlayer -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); // Finalizar la actividad de Splash
        });

        // Iniciar la reproducción del video
        videoView.start();
    }
}