package utility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * @author Shadilan
 *         Class to show messages that system return
 */
public class Essages {
    public static Essages instance = new Essages();
    private ArrayList<Essage> essageList;

    /**
     * Constructor
     */
    public Essages() {
        essageList = new ArrayList<>();
    }

    /**
     * Add Text to list
     *
     * @param Text Text to add
     */
    public void AddEssage(String Text) {
        essageList.add(new Essage(Text, 30));
    }

    /**
     * Decrease all essage counts
     */
    public void Tick() {
        ArrayList<Essage> remList = new ArrayList<>();
        for (Essage essage : essageList) {
            if (essage.Tick() < 0) {
                remList.add(essage);
            }
        }
        essageList.removeAll(remList);
    }

    /**
     * Return list of essage as Image
     *
     * @return Image of essage
     */
    public Bitmap getEssagesImg() {
        Bitmap result = Bitmap.createBitmap(400, essageList.size() * 10 + 10, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.YELLOW);
        int i = 0;
        for (Essage essage : essageList) {
            i++;
            canvas.drawText(essage.getText(), 0, i * 10, mPaint);
        }
        return result;


    }

    /**
     * Return essage list as strings
     *
     * @return String ArrayList
     */
    public ArrayList<String> getEssages() {
        ArrayList<String> essageStrings = new ArrayList<>();
        for (Essage essage : essageList) {

            essageStrings.add(essage.getText());
        }
        return essageStrings;
    }

    class Essage {
        private String Text;
        private int Count;

        /**
         * Constructor
         *
         * @param Text  Text
         * @param Count Life count
         */
        public Essage(String Text, int Count) {
            this.Text = Text;
            this.Count = Count;
        }

        /**
         * Return text
         *
         * @return String
         */
        public String getText() {
            return Text;
        }

        /**
         * Decrease Life count and return new count
         *
         * @return Life count
         */
        public int Tick() {
            Count--;
            return Count;
        }
    }
}
