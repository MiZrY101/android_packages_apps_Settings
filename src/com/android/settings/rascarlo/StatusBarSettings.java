
package com.android.settings.rascarlo;

import android.content.Context; 
import android.content.ContentResolver;  
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.IWindowManager; 
import android.telephony.TelephonyManager; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBarSettings extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_BATTERY = "status_bar_battery";
    private static final String STATUS_BAR_CATEGORY_NOTIFICATIONS = "status_bar_notifications"; 
    private static final String KEY_MISSED_CALL_BREATH = "missed_call_breath";
    private static final String KEY_MMS_BREATH = "mms_breath";  

    private ListPreference mQuickPulldown;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private ListPreference mStatusBarBattery;
    private PreferenceCategory mPrefCategoryNotifications; 
    private CheckBoxPreference mMMSBreath;
    private boolean mVoiceCapable;

    CheckBoxPreference mMissedCallBreath;

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_settings);

        // Quick Settings pull down
        mQuickPulldown = (ListPreference) getPreferenceScreen().findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.QS_QUICK_PULLDOWN, 0);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);

        // Status bar brightness control
        mStatusBarBrightnessControl = (CheckBoxPreference) getPreferenceScreen().findPreference(
                STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity()
                .getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        // don't show status bar brightnees control on tablet
        if (Utils.isTablet(getActivity())) {
            getPreferenceScreen().removePreference(mStatusBarBrightnessControl);
        }

        // status bar battery
        mStatusBarBattery = (ListPreference) getPreferenceScreen().findPreference(
                STATUS_BAR_BATTERY);
        int statusBarBattery = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);
        mPrefCategoryNotifications = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_NOTIFICATIONS); 
        }

        // Determine if the device has voice capabilities
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mVoiceCapable = tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;

        mMissedCallBreath = (CheckBoxPreference) findPreference(KEY_MISSED_CALL_BREATH);
        mMMSBreath = (CheckBoxPreference) findPreference(KEY_MMS_BREATH); 

        if (!mVoiceCapable) {
            prefSet.removePreference(findPreference(KEY_MISSED_CALL_BREATH));
            prefSet.removePreference(findPreference(KEY_MMS_BREATH));
            prefSet.removePreference((PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_NOTIFICATIONS)); 
        }

        mMissedCallBreath.setOnPreferenceChangeListener(this);
        mMMSBreath.setOnPreferenceChangeListener(this);

     } 

    private void updatePulldownSummary(int value) {
        Resources res = getResources();
        if (value == 0) {
            /* quick pulldown deactivated */
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_summary_left : R.string.quick_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QS_QUICK_PULLDOWN, quickPulldownValue);
            updatePulldownSummary(quickPulldownValue);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) objValue);
            int index = mStatusBarBattery.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            return true;
        } else if (preference == mMissedCallBreath) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.MISSED_CALL_BREATH,
                    ((CheckBoxPreference)preference).isChecked() ? 0 : 1);
            return true;
        } else if (preference == mMMSBreath) {
            Settings.System.putInt(getActivity().getContentResolver(), Settings.System.MMS_BREATH,
                    ((CheckBoxPreference)preference).isChecked() ? 0 : 1);
            return true; 
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
