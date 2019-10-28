package itasserui.lib.store

import itasserui.common.serialization.DBObject
import java.util.*

data class TestDBObject(override val id: UUID, val foo: String = "Bar") : DBObject
