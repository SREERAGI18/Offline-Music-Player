package com.example.offlinemusicplayer.player

import android.media.MediaExtractor
import android.media.MediaFormat
import com.example.offlinemusicplayer.domain.model.MediaInfo
import com.example.offlinemusicplayer.util.Constants.MICROSECONDS_PER_SECOND
import com.example.offlinemusicplayer.util.Logger
import java.io.File
import java.io.IOException

object MediaInfoExtractor {

    private val TAG = MediaInfoExtractor::class.java.simpleName

    private const val BIT_RATE_THRESHOLD = 32000
    private const val BITS_PER_BYTE = 8
    private const val BPS_TO_KBPS = 1000

    fun extractAudioInfo(filePath: String?): MediaInfo? {
        if (filePath == null) return null

        val extractor = MediaExtractor()

        return try {
            extractor.setDataSource(filePath)

            val audioFormat = findFirstAudioTrack(extractor) ?: return null

            buildMediaInfo(audioFormat, filePath)
        } catch (e: IOException) {
            Logger.logError(TAG, "IOException while extracting audio info: ${e.message}")
            null
        } finally {
            extractor.release()
        }
    }

    private fun findFirstAudioTrack(extractor: MediaExtractor): MediaFormat? {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)

            if (mime?.startsWith("audio/") == true) {
                return format
            }
        }

        Logger.logInfo(TAG, "No audio track found")
        return null
    }

    private fun buildMediaInfo(format: MediaFormat, filePath: String): MediaInfo? {
        val mime = format.getString(MediaFormat.KEY_MIME) ?: return null

        val bitRate = resolveBitrate(format, filePath) ?: return null
        val sampleRate = format.getIntegerOrNull(MediaFormat.KEY_SAMPLE_RATE) ?: return null

        val audioFormat = getFormatDescription(mime)

        Logger.logInfo(
            TAG,
            "Extracted Info: Format=$audioFormat, Bitrate=$bitRate, SampleRate=$sampleRate"
        )

        return MediaInfo(
            samplingRate = sampleRate,
            bitRateInKbps = bitRate / BPS_TO_KBPS,
            format = audioFormat
        )
    }

    private fun resolveBitrate(format: MediaFormat, filePath: String): Int? {
        val declaredBitrate = format.getIntegerOrNull(MediaFormat.KEY_BIT_RATE) ?: return null

        if (declaredBitrate > BIT_RATE_THRESHOLD) return declaredBitrate

        val durationUs = format.getLongOrNull(MediaFormat.KEY_DURATION) ?: return declaredBitrate

        return try {
            val file = File(filePath)
            if (!file.exists() || durationUs <= 0) return declaredBitrate

            val estimatedBitrate =
                (file.length() * BITS_PER_BYTE) /
                    (durationUs.toDouble() / MICROSECONDS_PER_SECOND)

            Logger.logInfo(TAG, "Estimated new bitrate: $estimatedBitrate bps")
            estimatedBitrate.toInt()
        } catch (e: ArithmeticException) {
            Logger.logError(TAG, "Bitrate estimation failed: ${e.message}")
            declaredBitrate
        } catch (e: SecurityException) {
            Logger.logError(TAG, "Permission denied: ${e.message}")
            declaredBitrate
        }
    }

    private fun MediaFormat.getIntegerOrNull(key: String): Int? =
        if (containsKey(key)) getInteger(key) else null

    private fun MediaFormat.getLongOrNull(key: String): Long? =
        if (containsKey(key)) getLong(key) else null

    /**
     * Gets a user-friendly format description.
     */
    private fun getFormatDescription(mime: String): String? {
        // Fallback for older APIs or when KEY_DESCRIPTION is missing
        return when (mime) {
            "audio/mpeg" -> "MP3"
            "audio/mp4a-latm" -> "AAC"
            "audio/flac" -> "FLAC"
            "audio/3gpp" -> "AMR"
            "audio/amr-wb" -> "AMR-WB"
            "audio/vorbis" -> "Ogg Vorbis"
            "audio/opus" -> "Opus"
            "audio/x-ms-wma" -> "WMA"
            else -> mime.removePrefix("audio/").uppercase() // Generic fallback
        }
    }
}
