package com.example.navi

import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.biometric.BiometricManager.Authenticators
import android.widget.Toast

fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    onCancel()
                } else {
                    Toast.makeText(activity, activity.getString(R.string.error_message, errString), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(activity, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.biometric_prompt_title))
        .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
        .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or
                Authenticators.DEVICE_CREDENTIAL)
        .build()

    biometricPrompt.authenticate(promptInfo)
}