package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.EmailData
import com.example.playlistmaker.sharing.domain.api.SharingRepository

class SharingRepositoryImpl(
    private val context: Context,
    private val externalNavigator: ExternalNavigator
) : SharingRepository {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_course_url)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.terms_url)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            addresses = arrayOf(context.getString(R.string.support_email)),
            subject = context.getString(R.string.email_subject),
            text = context.getString(R.string.email_body)
        )
    }
}
