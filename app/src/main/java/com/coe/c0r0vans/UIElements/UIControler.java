package com.coe.c0r0vans.UIElements;

import android.view.ViewGroup;

/**
 * Класс для контроля за UI элеметнами и отрисовкой их.
 */
public class UIControler {
    private static ViewGroup effectLayout;
    private static ButtonLayout buttonLayout;
    private static ActionView actionLayout;
    private static ViewGroup windowLayout;
    private static ViewGroup alertLayout;

    public static ViewGroup getEffectLayout() {
        return effectLayout;
    }

    public static void setEffectLayout(ViewGroup effectLayout) {
        UIControler.effectLayout = effectLayout;
    }

    public static ButtonLayout getButtonLayout() {
        return buttonLayout;
    }

    public static void setButtonLayout(ButtonLayout buttonLayout) {
        UIControler.buttonLayout = buttonLayout;
    }

    public static ActionView getActionLayout() {
        return actionLayout;
    }

    public static void setActionLayout(ActionView actionLayout) {
        UIControler.actionLayout = actionLayout;
    }

    public static ViewGroup getWindowLayout() {
        return windowLayout;
    }

    public static void setWindowLayout(ViewGroup windowLayout) {
        UIControler.windowLayout = windowLayout;
    }

    public static ViewGroup getAlertLayout() {
        return alertLayout;
    }

    public static void setAlertLayout(ViewGroup staticLayout) {
        UIControler.alertLayout = staticLayout;
    }
}
