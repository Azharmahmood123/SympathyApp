package com.astadevs.sympathyapp

import android.content.SharedPreferences
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_CITY
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_COUNTRY
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_LATITUDE
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_LONGITUDE
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_ORGANIZATION_ID
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_SUB_ADDRESS
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_USER_EMAIL
import com.astadevs.sympathyapp.AppSettings.AppPrefs.PREF_USER_UID

object AppSettings {
    private val prefs: SharedPreferences = App.self.getSharedPreferences(App.packageId, 0)

    var userUID: String
        get() = getString(PREF_USER_UID, "")
        set(value) = setString(PREF_USER_UID, value)

    var userEmail: String
        get() = getString(PREF_USER_EMAIL, "")
        set(value) = setString(PREF_USER_EMAIL, value)

    var selectedCity: String
        get() = getString(PREF_CITY, "")
        set(value) = setString(PREF_CITY, value)

    var selectedLat: String
        get() = getString(PREF_LATITUDE, "0.0")
        set(value) = setString(PREF_LATITUDE, value)

    var selectedLong: String
        get() = getString(PREF_LONGITUDE, "0.0")
        set(value) = setString(PREF_LONGITUDE, value)

    var selectedCountry: String
        get() = getString(PREF_COUNTRY, "")
        set(value) = setString(PREF_COUNTRY, value)

    var selectedSubAddress: String
        get() = getString(PREF_SUB_ADDRESS, "")
        set(value) = setString(PREF_SUB_ADDRESS, value)

    var organizationID: String
        get() = getString(PREF_ORGANIZATION_ID, "")
        set(value) = setString(PREF_ORGANIZATION_ID, value)

    fun isOrganization(): Boolean = organizationID.isNotEmpty()

    private fun getString(key: String, defaultVal: String): String {
        return prefs.getString(key, defaultVal) ?: ""
    }

    private fun setString(key: String, value: String?) {
        if (value == null) {
            remove(key)
        } else {
            prefs.edit()
                .putString(key, value)
                .apply()
        }
    }

    private fun remove(key: String) {
        prefs.edit()
            .remove(key)
            .apply()
    }

    object AppPrefs {
        const val PREF_USER_UID = "pref_user_uid"
        const val PREF_USER_EMAIL = "pref_user_email"

        const val PREF_CITY = "pref_city"
        const val PREF_LATITUDE = "pref_latitude"
        const val PREF_LONGITUDE = "pref_longitude"
        const val PREF_COUNTRY = "pref_country"
        const val PREF_SUB_ADDRESS = "pref_sub_address"

        const val PREF_ORGANIZATION_ID = "pref_organization_id"
    }
}