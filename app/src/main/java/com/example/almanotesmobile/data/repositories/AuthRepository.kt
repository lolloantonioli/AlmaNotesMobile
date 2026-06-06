package com.example.almanotesmobile.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val BIOMETRIC_CONSENT_ASKED = booleanPreferencesKey("biometric_consent_asked")
        val REGISTERED_ACCOUNTS = stringSetPreferencesKey("registered_accounts")
        val AWARDED_BADGES = stringSetPreferencesKey("awarded_badges")
    }

    val isRegistered: Flow<Boolean> = dataStore.data.map { prefs ->
        (prefs[Keys.IS_REGISTERED] ?: false) || prefs[Keys.REGISTERED_ACCOUNTS].orEmpty().isNotEmpty()
    }
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }
    val biometricEnabled: Flow<Boolean> = dataStore.data.map { it[Keys.BIOMETRIC_ENABLED] ?: false }
    val biometricConsentAsked: Flow<Boolean> = dataStore.data.map { it[Keys.BIOMETRIC_CONSENT_ASKED] ?: false }
    val username: Flow<String> = dataStore.data.map { it[Keys.USERNAME] ?: "" }
    val email: Flow<String> = dataStore.data.map { it[Keys.EMAIL] ?: "" }
    val password: Flow<String> = dataStore.data.map { it[Keys.PASSWORD] ?: "" }
    val profileImageUri: Flow<String?> = dataStore.data.map { prefs ->
        val accountKey = prefs.currentProfileImageKey()
        accountKey?.let { prefs[it] }
    }
    val reviewCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[prefs.currentReviewCountKey()] ?: 0
    }
    val downloadedNoteIds: Flow<List<Long>> = dataStore.data.map { prefs ->
        prefs[prefs.currentDownloadedIdsKey()].orEmpty().mapNotNull { it.toLongOrNull() }
    }

    suspend fun saveRegistration(username: String, email: String, password: String) {
        dataStore.edit { prefs ->
            val accountId = sanitizeAccountId(email.ifBlank { username })
            prefs[Keys.REGISTERED_ACCOUNTS] = prefs[Keys.REGISTERED_ACCOUNTS]
                .orEmpty()
                .toMutableSet()
                .apply { add(accountId) }
            prefs[accountUsernameKey(accountId)] = username
            prefs[accountEmailKey(accountId)] = email
            prefs[accountPasswordKey(accountId)] = password
            prefs[Keys.USERNAME] = username
            prefs[Keys.EMAIL] = email
            prefs[Keys.PASSWORD] = password
            prefs[Keys.IS_REGISTERED] = true
        }
    }

    suspend fun updateProfileImage(uri: String) {
        dataStore.edit { prefs ->
            val accountKey = prefs.currentProfileImageKey()
            if (accountKey != null) {
                prefs[accountKey] = uri
            }
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val accountId = sanitizeAccountId(email)
            val storedEmail = prefs[accountEmailKey(accountId)]
            val storedPassword = prefs[accountPasswordKey(accountId)]
            val matchesAccount = storedEmail == email && storedPassword == password
            val matchesLegacyAccount = prefs[Keys.EMAIL] == email && prefs[Keys.PASSWORD] == password

            if (matchesAccount || matchesLegacyAccount) {
                val username = prefs[accountUsernameKey(accountId)] ?: prefs[Keys.USERNAME].orEmpty()
                prefs[Keys.USERNAME] = username
                prefs[Keys.EMAIL] = email
                prefs[Keys.PASSWORD] = password
                prefs[Keys.REGISTERED_ACCOUNTS] = prefs[Keys.REGISTERED_ACCOUNTS]
                    .orEmpty()
                    .toMutableSet()
                    .apply { add(accountId) }
                prefs[Keys.IS_REGISTERED] = true
                prefs[Keys.IS_LOGGED_IN] = true
                success = true
            }
        }
        return success
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.BIOMETRIC_ENABLED] = enabled
            prefs[Keys.BIOMETRIC_CONSENT_ASKED] = true
        }
    }

    suspend fun loginWithBiometric(): Boolean {
        return try {
            var isReg = false
            dataStore.edit { prefs ->
                // Controlla sia legacy che multi-account (uguale alla Flow isRegistered)
                isReg = (prefs[Keys.IS_REGISTERED] ?: false)
                        || prefs[Keys.REGISTERED_ACCOUNTS].orEmpty().isNotEmpty()
                if (isReg) prefs[Keys.IS_LOGGED_IN] = true
            }
            isReg
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        dataStore.edit { it[Keys.IS_LOGGED_IN] = false }
    }

    suspend fun markNoteReviewed(noteId: Long): Int {
        var updatedCount = 0
        dataStore.edit { prefs ->
            val reviewedKey = prefs.currentReviewedIdsKey()
            val reviewedSet = prefs[reviewedKey].orEmpty().toMutableSet()
            val reviewCountKey = prefs.currentReviewCountKey()

            if (reviewedSet.add(noteId.toString())) {
                prefs[reviewedKey] = reviewedSet
                prefs[reviewCountKey] = reviewedSet.size
            }
            updatedCount = prefs[reviewCountKey] ?: reviewedSet.size
        }
        return updatedCount
    }

    suspend fun markNoteAsDownloaded(noteId: Long): Int {
        var updatedCount = 0
        dataStore.edit { prefs ->
            val key = prefs.currentDownloadedIdsKey()
            val set = prefs[key].orEmpty().toMutableSet()
            set.add(noteId.toString())
            prefs[key] = set
            updatedCount = set.size
        }
        return updatedCount
    }

    suspend fun markBadgeAwardedIfNew(badgeId: String): Boolean {
        var isNew = false
        dataStore.edit { prefs ->
            val accountScopedBadgeId = "${prefs.currentAccountId()}:$badgeId"
            val badges = prefs[Keys.AWARDED_BADGES].orEmpty().toMutableSet()
            isNew = badges.add(accountScopedBadgeId)
            if (isNew) {
                prefs[Keys.AWARDED_BADGES] = badges
            }
        }
        return isNew
    }

    private fun Preferences.currentProfileImageKey(): Preferences.Key<String>? {
        val accountId = currentAccountId()
        if (accountId.isBlank()) return null
        return stringPreferencesKey("profile_image_uri_$accountId")
    }

    private fun accountUsernameKey(accountId: String): Preferences.Key<String> =
        stringPreferencesKey("account_username_$accountId")

    private fun accountEmailKey(accountId: String): Preferences.Key<String> =
        stringPreferencesKey("account_email_$accountId")

    private fun accountPasswordKey(accountId: String): Preferences.Key<String> =
        stringPreferencesKey("account_password_$accountId")

    private fun Preferences.currentDownloadedIdsKey(): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("downloaded_ids_${currentAccountId()}")

    private fun Preferences.currentReviewedIdsKey(): Preferences.Key<Set<String>> =
        stringSetPreferencesKey("reviewed_ids_${currentAccountId()}")

    private fun Preferences.currentReviewCountKey(): Preferences.Key<Int> =
        intPreferencesKey("review_count_${currentAccountId()}")

    private fun Preferences.currentAccountId(): String =
        sanitizeAccountId(this[Keys.EMAIL].orEmpty().ifBlank { this[Keys.USERNAME].orEmpty() })

    private fun sanitizeAccountId(raw: String): String = raw
        .lowercase()
        .replace(Regex("[^a-z0-9._-]"), "_")
}
