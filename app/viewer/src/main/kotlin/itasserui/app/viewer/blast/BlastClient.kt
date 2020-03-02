package itasserui.app.viewer.blast

import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
import arrow.data.Valid
import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.okhttp.*
import itasserui.app.viewer.events.*
import itasserui.common.utils.safeWait
import javafx.application.Platform
import tornadofx.Controller
import tornadofx.FXEvent
import tornadofx.isLong

// TODO add response code checks
class BlastClient : Controller() {
    data class BlastDetailsEvent(val requestID: String, val timeEstimate: Long) : FXEvent()

    private val url = "https://blast.ncbi.nlm.nih.gov/blast/Blast.cgi"
    private val service: BlastServiceController by inject()
    private val client = OkHttpClient()
    private var running = false
    private var cancelled = false
    fun postSequence(queryString: String): Validated<BlastServiceError, BlastDetailsEvent> {
        val query = getSearchRequest(queryString).urlEncoded
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("UTF-8"), query))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Content-Length", query.length.toString())
            .build()

        return when (val response = Try { client.newCall(request).execute() }) {
            is Failure -> HTTPException(response.exception).also { println(it) } .invalid()
            is Success -> parseResponse(response.value).also { println(it) }
        }
    }

    fun getStatus(rid: String): Validated<BlastServiceError, BlastStatus> {
        val query = getStatusRequest(rid)
        val request = Request.Builder()
            .url("$url?${query.urlEncoded}")
            .header("Content-Type", "text/plain")
            .get().build()
        return when (val response = Try { client.newCall(request).execute() }) {
            is Failure -> HTTPException(response.exception).invalid()
            is Success -> parseStatus(response.value)
        }
    }

    fun getRemoteAlignmentRequest(rid: String): Validated<BlastServiceError, String> {
        val query = remoteAlignmentRequest(rid)
        val request = Request.Builder()
            .url("$url?${query.urlEncoded}")
            .header("Content-Type", "text/plain")
            .get().build()

        return when (val response = Try { client.newCall(request).execute() }) {
            is Failure -> HTTPException(response.exception).invalid()
            is Success -> remoteAlignmentText(response.value).valid()
        }
    }

    fun waitForBlast(rid: String) {
        fire(BlastStartedEvent)
        if (running) {
            fire(BlastAlreadyRunningEvent)
            return
        }
        running = true
        do {
            val status = getStatus(rid)
            status.map {
                val event = BlastStatusEvent(it)
                Platform.runLater { fire(event) }
            }
            if (cancelled) {
                cancelled = false
                break
            }
            safeWait(5000)
            print(service.status == BlastStatus.Waiting)
        } while (service.status == BlastStatus.Waiting)
        running = false
        fire(BlastEndedEvent)
        when (val alignments = getRemoteAlignmentRequest(rid)){
            is Valid -> fire(BlastRemoteAlignmentsEvent(alignments.a))
        }


        // TODO Remote alignments
    }



    private fun remoteAlignmentText(response: Response): String {
        val body = response.body().string()
        val text = body.substringAfter("<PRE>")
        println(text)
        return text
    }

    private fun parseStatus(response: Response):
            Validated<BlastServiceError, BlastStatus> {
        val body = response.body().string()
        if (!body.contains("QBlastInfoBegin"))
            return NoBlastInfoSection(response.code(), body).invalid()
        val qbInfo = body.substringAfter("QBlastInfoBegin")
        val status = qbInfo.substringAfter("Status=").substringBefore("\n")
        if (status.contains("ThereAreHits=no"))
            return BlastStatus.NoHitsFound.valid()
        if (status.toLowerCase() !in BlastStatus.values().map { it.toString().toLowerCase() })
            return NoValidStatus(status, response.code(), body).invalid()
        return BlastStatus.get(status).valid()
    }

    private fun parseResponse(response: Response): Validated<BlastServiceError, BlastDetailsEvent> {
        val body = response.body().string()
        return getData(body, response)
            .map { BlastDetailsEvent(it.first, it.second) }
            .map { fire(it); it }
    }

    private fun getData(body: String, response: Response):
            Validated<BlastServiceError, Pair<String, Long>> {
        if (!body.contains("QBlastInfoBegin"))
            return NoBlastInfoSection(response.code(), body).invalid()
        val qbInfo = body.substringAfter("QBlastInfoBegin").substringBefore("QBlastInfoEnd")
        val rid = qbInfo.substringAfter("RID = ").substringBefore("\n")
        val rtoe = qbInfo.substringAfter("RTOE = ").substringBefore("\n")
        if (!qbInfo.contains("RID = ") or !qbInfo.contains("RTOE = ") or !rtoe.isLong())
            return MissingBlastData(rid, rtoe, response.code(), body).invalid()
        return Pair(rid, rtoe.toLong()).valid()
    }

    private fun getSearchRequest(
        queryString: String,
        op: BlastSearchRequest.() -> Unit = {}
    ) = blastRequest {
        program = service.program
        database = service.database
        query = queryString
    }.apply(op)

    private fun getStatusRequest(
        requestID: String,
        op: BlastStatusRequest.() -> Unit = {}
    ) = blastStatus {
        rid = requestID
    }.apply(op)
}

fun blastRequest(op: BlastSearchRequest.() -> Unit) =
    BlastSearchRequest().apply(op)

class BlastSearchRequest {
    val cmd: String = "Put"
    var program: BlastProgram? = null
    var database: String? = null
    var query: String? = null
    @Suppress("unused")
    @get:JsonIgnore
    val json
        get() = toString()

    val urlEncoded
        get() =
            "CMD=$cmd&DATABASE=$database&QUERY=$query&PROGRAM=$program"

    override fun toString(): String {
        return urlEncoded
    }

}

fun blastStatus(op: BlastStatusRequest.() -> Unit) =
    BlastStatusRequest().apply(op)

class BlastStatusRequest {
    val cmd: String = "Get"
    var rid: String? = null
    @JsonProperty("FORMAT_OBJECT")
    val formatObject: String = "SearchInfo"

    val urlEncoded
        get() =
            "CMD=$cmd&RID=$rid&FORMAT_OBJECT=$formatObject"
}

fun remoteAlignmentRequest(rid: String? = "", op: BlastRemoteAlignmentsRequest.() -> Unit = {}) =
    BlastRemoteAlignmentsRequest().apply(op).apply { this.rid = rid }

class BlastRemoteAlignmentsRequest {
    val cmd: String = "Get"
    var rid: String? = null
    @JsonProperty("FORMAT_TYPE")
    val formatType: String = "Text"

    val urlEncoded
        get() =
            "CMD=$cmd&RID=$rid&FORMAT_TYPE=$formatType"
}