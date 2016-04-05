package utility.settings;

import java.util.EventListener;

/**
 * EventListener for Settings
 */
public abstract class SettingsListener implements EventListener {
    public abstract void onSettingsSave();
    public abstract void onSettingsLoad();
    public abstract void onSettingChange(String setting);
}
