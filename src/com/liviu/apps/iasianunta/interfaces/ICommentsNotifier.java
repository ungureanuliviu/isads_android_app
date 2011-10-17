package com.liviu.apps.iasianunta.interfaces;

import java.util.ArrayList;

import com.liviu.apps.iasianunta.data.Comment;

public interface ICommentsNotifier {
	public void onCommentLoaded(boolean isSuccess, int pAdId, ArrayList<Comment> pLoadedComments);
	public void onCommentAdded(boolean isSuccess, Comment pAddedComment);
}
