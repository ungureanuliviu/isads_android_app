package com.liviu.apps.iasianunta.interfaces;

import com.liviu.apps.iasianunta.data.User;

public interface ILoginNotifier {
	public void onLogin(boolean isSuccess, User pUser);
	public void onLogout(boolean isSuccess, int pUserId);
}
