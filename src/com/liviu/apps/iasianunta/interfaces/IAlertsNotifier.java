package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Alert;

public interface IAlertsNotifier {
	public void onAlertsLoaded(boolean isSuccess, ArrayList<Alert> pLoadedAlerts);
}
