package utility.sign;

import java.util.EventListener;

/**
 * Created by Shadilan on 24.07.2016.
 */
public abstract class SignInListener implements EventListener {
    public abstract void onComplete(String token);
    public abstract void onCanceled();
    public abstract void onSignOff();
}
