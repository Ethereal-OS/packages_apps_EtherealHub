/*
 * Copyright (C) 2023 Ethereal Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ethereal.hub.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import com.ethereal.hub.fragments.SmartPixels;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;
import com.ethereal.hub.preferences.CustomSeekBarPreference;
import android.provider.Settings;
import android.os.SystemProperties;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import android.content.pm.PackageManager.NameNotFoundException;
import com.android.settings.SettingsPreferenceFragment;
import com.ethereal.hub.preferences.SystemSettingMasterSwitchPreference;
import com.ethereal.hub.preferences.SystemSettingListPreference;
import com.ethereal.hub.preferences.SecureSettingSwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import android.provider.SearchIndexableResource;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class MiscSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String KEY_GAMES_SPOOF = "use_games_spoof";
    private static final String KEY_NETFLIX_SPOOF = "use_netflix_spoof";    

    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";
    private static final String SYS_GAMES_SPOOF = "persist.sys.pixelprops.games";
    private static final String SYS_NETFLIX_SPOOF = "persist.sys.pixelprops.netflix";

    private static final String SMART_PIXELS = "smart_pixels";

    private SwitchPreference mGamesSpoof;
    private SwitchPreference mPhotosSpoof;
    private SwitchPreference mNetFlixSpoof;
     
    private Preference mSmartPixels;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.etherealhub_misc);

        final PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mGamesSpoof = (SwitchPreference) findPreference(KEY_GAMES_SPOOF);
        if (mGamesSpoof != null) {
            mGamesSpoof.setChecked(SystemProperties.getBoolean(SYS_GAMES_SPOOF, false));
            mGamesSpoof.setOnPreferenceChangeListener(this);
        }

        mPhotosSpoof = (SwitchPreference) findPreference(KEY_PHOTOS_SPOOF);
        if (mPhotosSpoof != null) {
            mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
            mPhotosSpoof.setOnPreferenceChangeListener(this);
        }

        mNetFlixSpoof = (SwitchPreference) findPreference(KEY_NETFLIX_SPOOF);
        if (mNetFlixSpoof != null) {
            mNetFlixSpoof.setChecked(SystemProperties.getBoolean(SYS_NETFLIX_SPOOF, false));
            mNetFlixSpoof.setOnPreferenceChangeListener(this);
        }

        mSmartPixels = (Preference) findPreference(SMART_PIXELS);
        boolean mSmartPixelsSupported = getResources().getBoolean(
            com.android.internal.R.bool.config_supportSmartPixels);
        if (!mSmartPixelsSupported && mSmartPixels != null) {
            prefSet.removePreference(mSmartPixels);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mGamesSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_GAMES_SPOOF, value ? "true" : "false");
            return true;
        } else if (preference == mPhotosSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
            return true;
        } else if (preference == mNetFlixSpoof) {
            boolean value = (Boolean) newValue;
            SystemProperties.set(SYS_NETFLIX_SPOOF, value ? "true" : "false");
            return true;
        }
        return false;
    }   

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ETHEREAL;
    }

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider(R.xml.etherealhub_misc) {
            @Override
            public List<String> getNonIndexableKeys(Context context) {
                List<String> keys = super.getNonIndexableKeys(context);

                boolean mSmartPixelsSupported = context.getResources().getBoolean(
                    com.android.internal.R.bool.config_supportSmartPixels);
                if (!mSmartPixelsSupported) {
                    keys.add(SMART_PIXELS);
                }

                return keys;
            }
    };
}

