package Direction;

import java.util.List;

/*
https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}