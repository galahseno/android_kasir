package id.dev.oxy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import id.dev.oxy.data.model.auth.login.LoginResponse
import id.dev.oxy.util.Constant.AUTH_PREF
import id.dev.oxy.util.Constant.CUSTOMER_ID_KEY
import id.dev.oxy.util.Constant.EMAIL_KEY
import id.dev.oxy.util.Constant.IS_LOGIN_KEY
import id.dev.oxy.util.Constant.IS_PIN_KEY
import id.dev.oxy.util.Constant.LOCATION_ID_KEY
import id.dev.oxy.util.Constant.PIN_KEY
import id.dev.oxy.util.Constant.TOKEN_KEY
import id.dev.oxy.util.Constant.USERNAME_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AUTH_PREF)

class DataStoreRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private object PreferenceKeys {
        val usernameKey = stringPreferencesKey(name = USERNAME_KEY)
        val emailKey = stringPreferencesKey(name = EMAIL_KEY)
        val tokenKey = stringPreferencesKey(name = TOKEN_KEY)
        val pinKey = stringPreferencesKey(name = PIN_KEY)
        val isLoginKey = booleanPreferencesKey(name = IS_LOGIN_KEY)
        val isInputPinKey = booleanPreferencesKey(name = IS_PIN_KEY)

        val locationIdKey = stringPreferencesKey(name = LOCATION_ID_KEY)
        val customerIdKey = intPreferencesKey(name = CUSTOMER_ID_KEY)
    }

    private val dataStore = context.dataStore

    suspend fun saveLoginResponse(loginResponse: LoginResponse) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.usernameKey] = loginResponse.data.username
            preference[PreferenceKeys.emailKey] = loginResponse.data.email
            preference[PreferenceKeys.tokenKey] = loginResponse.token
        }
    }

    suspend fun savePin(pin: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.pinKey] = pin
        }
    }

    suspend fun saveInputPinSession(value: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.isInputPinKey] = value
        }
    }

    suspend fun saveLoginSession(value: Boolean) {
        if (value) {
            dataStore.edit { preference ->
                preference[PreferenceKeys.isLoginKey] = true
            }
        } else {
            dataStore.edit { it.clear() }
        }
    }

    suspend fun saveLocationId(locationId: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.locationIdKey] = locationId
        }
    }

    suspend fun saveCustomerId(customerId: Int) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.customerIdKey] = customerId
        }
    }

    val readHeaderData: Flow<List<String>> = dataStore.data
        .map { preferences ->
            listOf(
                preferences[PreferenceKeys.usernameKey] ?: "",
                preferences[PreferenceKeys.emailKey] ?: ""
            )
        }

    val readToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.tokenKey]
        }

    val readIsLogin: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.isLoginKey] ?: false
        }

    val readIsInputPin: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.isInputPinKey] ?: false
        }

    val readPin: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.pinKey] ?: ""
        }

    val readLocationId: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.locationIdKey] ?: ""
        }

    val readCustomerId: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.customerIdKey] ?: 1
        }
}