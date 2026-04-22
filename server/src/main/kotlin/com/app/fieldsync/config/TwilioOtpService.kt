package com.app.fieldsync.config

import com.twilio.rest.verify.v2.service.Verification
import com.twilio.rest.verify.v2.service.VerificationCheck

class TwilioOtpService {
    val serviceSID = "VA94bd8f056e3855921501dbae5fa2d013"

    fun generateOtp(phone: String): Boolean {
        return Verification.creator(
            serviceSID, phone, "sms"
        ).create().status == "pending"
    }

    fun verifyOtp(phone: String, code: String): Boolean {
        return VerificationCheck.creator(serviceSID).setTo(phone).setCode(code)
            .create().status == "approved"
    }
}