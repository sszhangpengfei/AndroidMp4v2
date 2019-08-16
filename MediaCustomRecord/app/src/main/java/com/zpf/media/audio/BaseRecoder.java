package com.zpf.media.audio;

/**
 * author:zpf
 * date:2019-08-16
 */
public abstract class BaseRecoder implements Runnable {

    public abstract void setRunning(boolean isRunning);

    public abstract boolean isRunning();

    public abstract void setPause(boolean isPause);

    public abstract boolean isPause();

    public abstract boolean checkType(String type);

}
