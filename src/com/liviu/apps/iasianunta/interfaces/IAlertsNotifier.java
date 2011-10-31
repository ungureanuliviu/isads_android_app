package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Alert;

public interface IAlertsNotifier {
	public void onAlertsLoaded(boolean isSuccess, ArrayList<Alert> pLoadedAlerts);
	public void onAlertAdded(boolean isSuccess, Alert pAddedAlert);
	public void onAlertLoaded(boolean isSuccess, Alert pLoadedAlert);
	public void onAlertRemoved(boolean isSuccess, Alert pAlertRemoved);
}
