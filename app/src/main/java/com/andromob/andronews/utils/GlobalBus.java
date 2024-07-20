package com.andromob.andronews.utils;

import org.greenrobot.eventbus.EventBus;

public class GlobalBus {
    private static EventBus sBus;
    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }

}
