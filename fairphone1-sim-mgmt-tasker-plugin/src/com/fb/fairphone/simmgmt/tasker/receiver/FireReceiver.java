/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.fb.fairphone.simmgmt.tasker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import com.fb.fairphone.simmgmt.tasker.Constants;
import com.fb.fairphone.simmgmt.tasker.bundle.BundleScrubber;
import com.fb.fairphone.simmgmt.tasker.bundle.PluginBundleManager;
import com.fb.fairphone.simmgmt.tasker.ui.EditActivity;
import com.fb.fairphone.simmgmt.tasker.dualsim.SimManagement;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 * 
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver {
	private static SimManagement mSimManagement;

	/**
	 * @param context
	 *            {@inheritDoc}.
	 * @param intent
	 *            the incoming
	 *            {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING}
	 *            Intent. This should contain the
	 *            {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was
	 *            saved by {@link EditActivity} and later broadcast by Locale.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent) {
		/*
		 * Always be strict on input parameters! A malicious third-party app
		 * could send a malformed Intent.
		 */

		if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent
				.getAction())) {
			if (Constants.IS_LOGGABLE) {
				Log.e(Constants.LOG_TAG,
						String.format(
								Locale.US,
								"Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
			}
			return;
		}

		BundleScrubber.scrub(intent);

		final Bundle bundle = intent
				.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		BundleScrubber.scrub(bundle);

		if (PluginBundleManager.isBundleValid(bundle)) {
			final Boolean bSIM1 = bundle
					.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_SIM1);

			final Boolean bSIM2 = bundle
					.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_SIM2);

			if (mSimManagement == null)
				mSimManagement = SimManagement.getInstance(context);

			if (mSimManagement.isSupported()) {
				// Only update the SIM-state if current state doesn't match the
				// target state.
				if (mSimManagement.getState(1) != bSIM1)
					mSimManagement.changeState(1, bSIM1);

				if (mSimManagement.getState(2) != bSIM2)
					mSimManagement.changeState(2, bSIM2);
			} else {
				Toast.makeText(context, "Phone is not supported.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}