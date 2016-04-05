package coe.com.c0r0vans.UIElements;

import android.view.ViewGroup;

/**
 * Класс для контроля за UI элеметнами и отрисовкой их.
 */
public class UIControler {
    private static ViewGroup effectLayout;
    private static ViewGroup buttonLayout;
    private static ViewGroup actionLayout;
    private static ViewGroup windowLayout;
    private static ViewGroup staticLayout;

    public static ViewGroup getEffectLayout() {
        return effectLayout;
    }

    public static void setEffectLayout(ViewGroup effectLayout) {
        UIControler.effectLayout = effectLayout;
    }

    public static ViewGroup getButtonLayout() {
        return buttonLayout;
    }

    public static void setButtonLayout(ViewGroup buttonLayout) {
        UIControler.buttonLayout = buttonLayout;
    }

    public static ViewGroup getActionLayout() {
        return actionLayout;
    }

    public static void setActionLayout(ViewGroup actionLayout) {
        UIControler.actionLayout = actionLayout;
    }

    public static ViewGroup getWindowLayout() {
        return windowLayout;
    }

    public static void setWindowLayout(ViewGroup windowLayout) {
        UIControler.windowLayout = windowLayout;
    }

    public static ViewGroup getStaticLayout() {
        return staticLayout;
    }

    public static void setStaticLayout(ViewGroup staticLayout) {
        UIControler.staticLayout = staticLayout;
    }
}
