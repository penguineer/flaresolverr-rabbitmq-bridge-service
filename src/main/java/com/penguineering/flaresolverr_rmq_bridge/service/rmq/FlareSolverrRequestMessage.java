package com.penguineering.flaresolverr_rmq_bridge.service.rmq;

import com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr.FlareSolverrRequest;

/**
 * Encapsulates a FlareSolverr request.
 *
 * <p>Further parameters will be added in the future.</p>
 *
 * @param request the FlareSolverr request
 */
public record FlareSolverrRequestMessage(
        FlareSolverrRequest request
) {
}
