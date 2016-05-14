package utility;

import android.content.Context;
import android.os.Vibrator;

import utility.settings.GameSettings;

/**
 * Класс для контроля и запуска вибрации
 */
public class GameVibrate {
    private static Vibrator vibrator;
    private static Context context;
    private static long[] pattern=new long[]{1000,500,1000};
    public static void init(Context pContext){
        context=pContext;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    public static void vibrate(){
        if ("Y".equals(GameSettings.getValue("VIBRATE_ON")))
            vibrator.vibrate(500);
    }
}
