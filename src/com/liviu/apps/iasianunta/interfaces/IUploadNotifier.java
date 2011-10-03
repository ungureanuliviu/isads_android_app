package com.liviu.apps.iasianunta.interfaces;

import android.net.Uri;

public interface IUploadNotifier {
	public void onImageUploaded(boolean isSuccess, Uri uri, String jsonResponse);
}
