package com.androidfany.rommanager;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AboutActivity extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
	}
}
