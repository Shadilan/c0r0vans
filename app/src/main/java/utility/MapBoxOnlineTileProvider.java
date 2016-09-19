package utility;

import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class MapBoxOnlineTileProvider extends UrlTileProvider {

    public final String TAG = this.getClass().getCanonicalName();
    private static final String FORMAT;

    static {
        FORMAT = "%s://api.tiles.mapbox.com/v4/%s/%d/%d/%d.png32?access_token=%s";
    }


    private boolean mHttpsEnabled;
    private String mMapIdentifier;
    private String mAccessToken;


    public MapBoxOnlineTileProvider(String mapIdentifier, String accessToken) {
        this(mapIdentifier, accessToken, false);
    }

    public MapBoxOnlineTileProvider(String mapIdentifier, String accessToken, boolean https) {
        super(256, 256);

        this.mHttpsEnabled = https;
        this.mMapIdentifier = mapIdentifier;
        this.mAccessToken = accessToken;
    }
    
    
    /**
     * The MapBox map identifier being used by this provider.
     *
     * @return the MapBox map identifier being used by this provider.
     */
    public String getMapIdentifier() {
        return this.mMapIdentifier;
    }

    /**
     * Sets the identifier of the MapBox hosted map you wish to use.
     *
     * @param aMapIdentifier the identifier of the map.
     */
    public void setMapIdentifier(String aMapIdentifier) {
        this.mMapIdentifier = aMapIdentifier;
    }

    /**
     * Whether this provider will use HTTPS when requesting tiles.
     *
     * @return {@link true} if HTTPS is enabled on this provider.
     */
    public boolean isHttpsEnabled() {
        return this.mHttpsEnabled;
    }

    /**
     * Sets whether this provider should use HTTPS when requesting tiles.
     *
     * @param enabled
     */
    public void setHttpsEnabled(boolean enabled) {
        this.mHttpsEnabled = enabled;
    }

    /**
     * The MapBox Acces Token found in Account Settings.
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            String protocol = this.mHttpsEnabled ? "https" : "http";
            final String url = String.format(FORMAT,
                    protocol, this.mMapIdentifier, z, x, y, this.mAccessToken);
            Log.d(TAG, url);
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

}