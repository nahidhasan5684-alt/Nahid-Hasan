package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.auth.AuthManager
import com.example.auth.UserRole
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SCORNER", appName)
    }

    @Test
    fun `verify auth manager role switching`() {
        AuthManager.switchRole(UserRole.TEACHER)
        assertEquals(UserRole.TEACHER, AuthManager.currentUser.role)
        AuthManager.switchRole(UserRole.STUDENT)
        assertEquals(UserRole.STUDENT, AuthManager.currentUser.role)
    }

    @Test
    fun `verify language localization manager`() {
        LanguageManager.currentLanguage = AppLanguage.BENGALI
        assertEquals("স্কর্নার", LanguageManager.getString("app_name"))
        LanguageManager.currentLanguage = AppLanguage.ENGLISH
        assertEquals("SCORNER", LanguageManager.getString("app_name"))
    }
}
