package itasserui.app.user.units

import arrow.data.Invalid
import arrow.data.Valid
import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.errors.MissingCharset
import itasserui.common.errors.MissingLower
import itasserui.common.errors.MissingUpper
import itasserui.common.errors.ShortPassword
import itasserui.common.extensions.validPassword

class PasswordTest : DescribeSpec({
    describe("RawPassword Validation Tests") {
        context("Invalid Passwords") {
            it("Should be too short") {
                val password = "hi12".validPassword()
                password.isValid should be(false)
                password as Invalid
                val err = password.e
                err as ShortPassword
            }

            it("Should be invalid due to lack of uppercase characters") {
                val password = "qwertylo;".validPassword()
                password.isValid should be(false)
                password as Invalid
                val err = password.e
                err as MissingUpper
            }

            it("Missing Special Characters") {
                val password = "Qwertylol".validPassword()
                password.isValid should be(false)
                password as Invalid
                val err = password.e
                err as MissingCharset
            }

            it("Missing Lowercase Characters") {
                val password = "QWERTYLO;".validPassword()
                password.isValid should be(false)
                password as Invalid
                val err = password.e
                err as MissingLower
            }
        }

        it("Valid RawPassword") {
            val pass = "ValidPassword1;"
            val password = pass.validPassword()
            password.isValid should be(true)
            password as Valid
            password.a should be(pass)
        }
    }
})