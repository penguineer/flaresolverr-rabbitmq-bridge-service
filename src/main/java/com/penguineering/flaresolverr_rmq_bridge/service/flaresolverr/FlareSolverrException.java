package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import java.io.IOException;

public class FlareSolverrException extends IOException {
    public FlareSolverrException(String message) {
        super(message);
    }

    public FlareSolverrException(Throwable cause) {
        super(cause);
    }

    public FlareSolverrException(String message, Throwable cause) {
        super(message, cause);
    }
}
