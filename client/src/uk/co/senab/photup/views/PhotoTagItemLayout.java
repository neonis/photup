package uk.co.senab.photup.views;

import java.util.List;

import uk.co.senab.bitmapcache.R;
import uk.co.senab.photup.Constants;
import uk.co.senab.photup.listeners.OnPhotoTagsChangedListener;
import uk.co.senab.photup.model.PhotoTag;
import uk.co.senab.photup.model.PhotoUpload;
import android.content.Context;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class PhotoTagItemLayout extends FrameLayout implements MultiTouchImageView.OnMatrixChangedListener,
		OnPhotoTagsChangedListener {

	static final String LOG_TAG = "PhotoTagItemLayout";

	private final MultiTouchImageView mImageView;
	private final AbsoluteLayout mTagLayout;

	private final PhotoUpload mUpload;

	public PhotoTagItemLayout(Context context, PhotoUpload upload) {
		super(context);

		mImageView = new MultiTouchImageView(context, true);
		mImageView.setMatrixChangeListener(this);
		addView(mImageView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

		mTagLayout = new AbsoluteLayout(context);
		addView(mTagLayout, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

		mUpload = upload;
		mUpload.setTagChangedListener(this);

		addPhotoTags();
	}

	private void addPhotoTags() {
		mTagLayout.removeAllViews();

		final List<PhotoTag> tags = mUpload.getPhotoTags();
		if (null != tags && !tags.isEmpty()) {
			LayoutInflater layoutInflater = LayoutInflater.from(getContext());

			TextView tagLayout;
			for (PhotoTag tag : tags) {
				tagLayout = (TextView) layoutInflater.inflate(R.layout.layout_photo_tag, mTagLayout, false);
				tagLayout.setVisibility(View.GONE);
				tagLayout.setTag(tag);

				mTagLayout.addView(tagLayout);
			}
		}
	}

	public MultiTouchImageView getImageView() {
		return mImageView;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	public void onMatrixChanged(RectF rect) {
		Log.d(LOG_TAG, rect.toString());

		AbsoluteLayout.LayoutParams lp;
		for (int i = 0, z = mTagLayout.getChildCount(); i < z; i++) {
			View tagLayout = mTagLayout.getChildAt(i);
			PhotoTag tag = (PhotoTag) tagLayout.getTag();

			lp = (AbsoluteLayout.LayoutParams) tagLayout.getLayoutParams();
			lp.x = Math.round((rect.width() * tag.getX() / 100f) + rect.left);
			lp.y = Math.round((rect.height() * tag.getY() / 100f) + rect.top);
			tagLayout.setLayoutParams(lp);

			if (Constants.DEBUG) {
				Log.d(LOG_TAG, "Tag Location: x: " + lp.x + " y: " + lp.y);
			}

			tagLayout.setVisibility(View.VISIBLE);
		}
	}

	public void onPhotoTagsChanged() {
		post(new Runnable() {
			public void run() {
				addPhotoTags();
			}
		});
	}
}