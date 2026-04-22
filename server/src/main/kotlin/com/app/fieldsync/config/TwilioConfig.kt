package com.app.fieldsync.config

import com.twilio.Twilio

object TwilioConfig {

    val accountSid: String? = System.getenv("TWILIO_ACCOUNT_SID")
    val authToken: String? = System.getenv("TWILIO_AUTH_TOKEN")

    fun init() {
        Twilio.init(accountSid, authToken)
    }
}