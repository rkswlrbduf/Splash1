package samsung.membership.splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by KyuYeol on 2017-08-09.
 */

public class ShowWifiMonitor extends BroadcastReceiver {

    public final static int WIFI_DISABLED = 0x00;
    public final static int WIFI_ENABLED = WIFI_DISABLED + 1;

    public interface OnChangeNetworkStatusListener {
        public void OnChanged(int status);
    }

    public ShowWifiMonitor() {
        super();
    }

    private WifiManager wifiManager = null;
    private OnChangeNetworkStatusListener onChangeNetworkStatusListener = null;

    public ShowWifiMonitor(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void setOnChangeNetworkStatusListener(OnChangeNetworkStatusListener listener) {
        onChangeNetworkStatusListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onChangeNetworkStatusListener == null) {
            return;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if(ssid.contains("Rpi")) {
            onChangeNetworkStatusListener.OnChanged(WIFI_ENABLED);
        } else {
            onChangeNetworkStatusListener.OnChanged(WIFI_DISABLED);
        }
    }
}