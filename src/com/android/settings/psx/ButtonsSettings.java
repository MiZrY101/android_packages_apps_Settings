package com.android.settings.psx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.preference.PreferenceScreen;
import android.content.ContentResolver;
import android.preference.ListPreference;

public class ButtonsSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_VOLUME_WAKE = "pref_volume_wake";
    private static final String KEY_VOLUME_ADJUST_SOUNDS = "volume_adjust_sounds";
    private static final String KEY_VOLBTN_MUSIC_CTRL = "volbtn_music_controls";

    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mVolumeAdustSound;
    private CheckBoxPreference mVolBtnMusicCtrl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.buttons_settings);
        
		ContentResolver resolver = getActivity().getContentResolver();	
		PreferenceScreen prefSet = getPreferenceScreen();
		
        mVolumeWake = (CheckBoxPreference) findPreference(KEY_VOLUME_WAKE);
        if (mVolumeWake != null) {
            mVolumeWake.setChecked(Settings.System.getInt(resolver,
                    Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
        }

        mVolumeAdustSound = (CheckBoxPreference) findPreference(KEY_VOLUME_ADJUST_SOUNDS);
        mVolumeAdustSound.setChecked(Settings.System.getInt(resolver,
                Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) == 1);

		mVolBtnMusicCtrl = (CheckBoxPreference) findPreference(KEY_VOLBTN_MUSIC_CTRL);
        mVolBtnMusicCtrl.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.VOLUME_MUSIC_CONTROLS, 1) != 0);				
	}


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {      
        if (preference == mVolumeAdustSound) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED,
                    mVolumeAdustSound.isChecked() ? 1 : 0);
			return true;
		} else if (preference == mVolumeWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN,
                    mVolumeWake.isChecked() ? 1 : 0);
			return true;
        } else if (preference == mVolBtnMusicCtrl) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_MUSIC_CONTROLS,
                    mVolBtnMusicCtrl.isChecked() ? 1 : 0);
			return true;
		}
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        // TODO Auto-generated method stub
        return false;
    }
	
}
