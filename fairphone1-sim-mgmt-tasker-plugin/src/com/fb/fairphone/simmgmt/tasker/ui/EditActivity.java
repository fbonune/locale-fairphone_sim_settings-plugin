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

package com.fb.fairphone.simmgmt.tasker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import com.fb.fairphone.simmgmt.tasker.R;
import com.fb.fairphone.simmgmt.tasker.bundle.BundleScrubber;
import com.fb.fairphone.simmgmt.tasker.bundle.PluginBundleManager;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 * <p>
 * This Activity can be started in one of two states:
 * <ul>
 * <li>New plug-in instance: The Activity's Intent will not contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}.</li>
 * <li>Old plug-in instance: The Activity's Intent will contain
 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} from a previously saved
 * plug-in instance that the user is editing.</li>
 * </ul>
 * 
 * @see com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class EditActivity extends AbstractPluginActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BundleScrubber.scrub(getIntent());

		final Bundle localeBundle = getIntent().getBundleExtra(
				com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		BundleScrubber.scrub(localeBundle);

		setContentView(R.layout.main);

		if (null == savedInstanceState) {
			if (PluginBundleManager.isBundleValid(localeBundle)) {
				/*
				 * Used two radio buttons instead of a slider to match the style
				 * of other locale plugins
				 */
				final Boolean bSIM1 = localeBundle
						.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_SIM1);
				if (bSIM1) {
					((RadioGroup) findViewById(R.id.radioGroup1))
							.check(R.id.radio11);
				} else {
					((RadioGroup) findViewById(R.id.radioGroup1))
							.check(R.id.radio12);
				}

				final Boolean bSIM2 = localeBundle
						.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_SIM2);
				if (bSIM2) {
					((RadioGroup) findViewById(R.id.radioGroup2))
							.check(R.id.radio21);
				} else {
					((RadioGroup) findViewById(R.id.radioGroup2))
							.check(R.id.radio22);
				}
			}
		}
	}

	@Override
	public void finish() {
		if (!isCanceled()) {
			final StringBuilder sim1Status = new StringBuilder();
			final StringBuilder sim2Status = new StringBuilder();

			// find the radioGroup SIM1 by id
			// ------------------------------
			RadioGroup radioSIM1 = (RadioGroup) findViewById(R.id.radioGroup1);
			int selectedId1 = radioSIM1.getCheckedRadioButtonId();
			// find the radioButton by returned id
			RadioButton buttonSIM1 = (RadioButton) findViewById(selectedId1);

			sim1Status.append(" SIM1: ");
			sim1Status.append(buttonSIM1.getText());

			// find the radioGroup SIM2 by id
			// ------------------------------
			RadioGroup radioSIM2 = (RadioGroup) findViewById(R.id.radioGroup2);
			int selectedId2 = radioSIM2.getCheckedRadioButtonId();
			// find the radioButton by returned id
			RadioButton buttonSIM2 = (RadioButton) findViewById(selectedId2);

			sim2Status.append(" SIM2: ");
			sim2Status.append(buttonSIM2.getText());

			final Intent resultIntent = new Intent();

			/*
			 * This extra is the data to ourselves: either for the Activity or
			 * the BroadcastReceiver. Note that anything placed in this Bundle
			 * must be available to Locale's class loader. So storing String,
			 * int, and other standard objects will work just fine. Parcelable
			 * objects are not acceptable, unless they also implement
			 * Serializable. Serializable objects must be standard Android
			 * platform objects (A Serializable class private to this plug-in's
			 * APK cannot be stored in the Bundle, as Locale's classloader will
			 * not recognize it).
			 */
			final Bundle resultBundle = PluginBundleManager.generateBundle(
					getApplicationContext(), (selectedId1 == R.id.radio11),
					(selectedId2 == R.id.radio21));
			resultIntent
					.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE,
							resultBundle);

			/*
			 * The blurb is concise status text to be displayed in the host's
			 * UI.
			 */
			final String blurb = generateBlurb(getApplicationContext(),
					sim1Status.toString(), sim2Status.toString());
			resultIntent.putExtra(
					com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);

			setResult(RESULT_OK, resultIntent);
		}

		super.finish();
	}

	/**
	 * @param context
	 *            Application context.
	 * @param sim1Status
	 *            The message displaying SIM1's status.
	 * @param sim2Status
	 *            The message displaying SIM2's status.
	 * @return A blurb for the plug-in.
	 */
	/* package */static String generateBlurb(final Context context,
			final String sim1Status, final String sim2Status) {
		StringBuilder messageBlurb = new StringBuilder();
		messageBlurb.append(sim1Status);
		messageBlurb.append(" ");
		messageBlurb.append(sim2Status);

		final String message = messageBlurb.toString();
		int maxBlurbLength = context.getResources().getInteger(
				R.integer.twofortyfouram_locale_maximum_blurb_length);

		if (message.length() > maxBlurbLength) {
			return message.toString().substring(0, maxBlurbLength);
		}

		return message;
	}
}