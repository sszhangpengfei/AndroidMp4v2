package com.zpf.media.camera;

/**
 * author:zpf
 * date:2019-08-16
 */

public class PrepareCameraException extends Exception {

    private static final String	LOG_PREFIX			= "Unable to unlock camera - ";
    private static final String	MESSAGE				= "Unable to use camera for recording";

    private static final long	serialVersionUID	= 6305923762266448674L;

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
