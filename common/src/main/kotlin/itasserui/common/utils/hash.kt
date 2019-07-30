package itasserui.common.utils

import itasserui.common.interfaces.hashRounds
import org.mindrot.jbcrypt.BCrypt

fun hash(rounds: Int = hashRounds, value: () -> String) =
    BCrypt.hashpw(
        value(),
        BCrypt.gensalt(rounds)
    )