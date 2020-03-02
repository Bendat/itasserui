package itasserui.app.viewer.blast

import itasserui.common.errors.RuntimeError

sealed class BlastServiceError : RuntimeError()

data class BadResponseCode(val expected: Int, val actual: Int) : BlastServiceError()
data class NoBlastInfoSection(val code: Int, val body: String) : BlastServiceError()
data class HTTPException(val result: Throwable) : BlastServiceError()
data class NoValidStatus(val status: String, val code: Int, val body: String) : BlastServiceError()
data class MissingBlastData(
    val id: String?, val estimatedTime: String?,
    val code: Int, val body: String
) : BlastServiceError()
