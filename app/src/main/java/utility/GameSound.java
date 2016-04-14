package utility;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.ThreadPoolExecutor;

import coe.com.c0r0vans.R;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * @author Shadilan
 */
public class GameSound {
    private static GameSound instance;
    private SoundPool soundPool;
    private Context context;
    private float volume=1;
    private AudioManager audioManager;
    private boolean music_on=false;
    private boolean sound_on=false;
    private int soundStream=-1;
    private int musicStream=-1;
    public static void init(Context ctx){
        Log.d("Timing", "Sound-Start");
        if (instance==null) instance=new GameSound();
        Log.d("Timing", "Sound-instance");
        instance.context=ctx;
        instance.soundPool=instance.buildSoundPool();
        Log.d("Timing", "Sound-buildPool");
        instance.loadSamples();
        Log.d("Timing", "Sound-loadSample");
        instance.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == instance.MUSIC) {
                    if (instance.music_on)
                        instance.musicStream = soundPool.play(instance.MUSIC, 1, 1, 1, -1, 1);
                }
            }
        });
        Log.d("Timing", "Sound-listener");
        updateSettings();
        Log.d("Timing", "Sound-settings");
        GameSettings.addSettingsListener(new SettingsListener() {
            @Override
            public void onSettingsSave() {

            }

            @Override
            public void onSettingsLoad() {

            }

            @Override
            public void onSettingChange(String setting) {
                switch (setting) {
                    case "MUSIC_ON":
                        instance.music_on = GameSettings.getInstance().get("MUSIC_ON").equals("Y");
                        if (instance.music_on && instance.musicStream == -1) {
                            playMusic();
                        } else if (!instance.music_on && instance.musicStream != -1) {
                            stopMusic();
                        }
                        break;
                    case "SOUND_ON":
                        instance.sound_on = GameSettings.getInstance().get("SOUND_ON").equals("Y");
                        break;
                }
            }
        });
        Log.d("Timing", "Sound-settings");
    }
    private int MUSIC=-1;
    public static int SET_AMBUSH=-1;
    public static int BUY_SOUND=-1;
    public static int KILL_SOUND=-1;
    public static int START_ROUTE_SOUND=-1;
    public static int FINISH_ROUTE_SOUND=-1;
    public static int REMOVE_AMBUSH=-1;
    public static int GATE_OPEN=-1;
    private Runnable task=new Runnable() {
        @Override
        public void run() {


            SET_AMBUSH=soundPool.load(context,R.raw.set_ambush,0);

            BUY_SOUND=soundPool.load(context,R.raw.coins,0);

            KILL_SOUND=soundPool.load(context,R.raw.kill,0);

            START_ROUTE_SOUND=soundPool.load(context,R.raw.writing,0);

            FINISH_ROUTE_SOUND=soundPool.load(context,R.raw.horse,0);

            REMOVE_AMBUSH=soundPool.load(context,R.raw.remove_ambush,0);
            GATE_OPEN=soundPool.load(context,R.raw.gate,0);

        }
    };
    private void loadSamples(){
        Log.d("Timing", "Sound");
        MUSIC=soundPool.load(context, R.raw.drums,0);
        Log.d("Timing", "Sound-music");
        new Thread(task).start();
        Log.d("Timing", "Sound-thread");

    }
    private SoundPool buildSoundPool() {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(25)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volume = actVolume / maxVolume;

            int counter = 0;

            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        return soundPool;
    }
    public static void setVolumeControlStream(Activity act){
        act.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    public static void playMusic(){
        if (instance.music_on && instance.musicStream==-1) {

            instance.musicStream = instance.soundPool.play(instance.MUSIC, 1, 1, 1, -1, 1);
        }
    }
    public static void stopMusic(){
        if (instance.musicStream!=-1) {
            instance.soundPool.stop(instance.musicStream);
            instance.musicStream=-1;
        }

    }
    public static void updateSettings(){
        instance.music_on= GameSettings.getInstance().get("MUSIC_ON").equals("Y");
        instance.sound_on= GameSettings.getInstance().get("SOUND_ON").equals("Y");
        if (instance.music_on && instance.musicStream==-1) {playMusic();}
        else if (!instance.music_on && instance.musicStream!=-1) {stopMusic();}
    }
    public static void playSound(int soundId){
        if (soundId!=-1) return;
        if (instance.sound_on) {
            if (instance.soundStream!=-1) {
                instance.soundPool.stop(instance.soundStream);
                instance.soundStream=-1;
            }
            instance.soundStream=instance.soundPool.play(soundId,1,1,1,0,1);
        }
    }


}
