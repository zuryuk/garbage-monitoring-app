package Direction;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/*
https://github.com/hiepxuan2008/GoogleMapDirectionSimple/
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
