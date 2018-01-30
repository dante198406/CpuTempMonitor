package com.erobbing.cputempmonitor;

/**
 * Created by zhangzhaolei on 2018/1/30.
 */

public class FanCtrlNative {
    static {
        System.loadLibrary("fanctrl");
    }

    /**
     * disable fan
     *
     * @return (true, success)
     */
    static native boolean disableFan();

    /**
     * enable fan
     *
     * @return (true, success)
     */
    static native boolean enableFan();
}
