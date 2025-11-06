package com.lyrisync.player

import android.media.MediaExtractor
import android.media.MediaFormat
import com.lyrisync.domain.model.MediaInfo
import com.lyrisync.util.Logger
import java.io.IOException

object MediaInfoExtractor {

    private val TAG = MediaInfoExtractor::class.java.simpleName

    fun extractAudioInfo(filePath: String): MediaInfo? {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(filePath)

            // Iterate through tracks to find the first audio track
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                // We only care about audio tracks
                if (mime?.startsWith("audio/") == true) {

                    // --- Bitrate Extraction (with fallback) ---
                    var bitRate = if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
                        format.getInteger(MediaFormat.KEY_BIT_RATE)
                    } else {
                        null
                    }

                    // For VBR files, MediaExtractor's KEY_BIT_RATE is often wrong (e.g., 32000).
                    // We can estimate a more accurate bitrate from file size and duration.
                    if (bitRate != null && (bitRate <= 32000) && format.containsKey(MediaFormat.KEY_DURATION)) {
                        try {
                            val durationUs = format.getLong(MediaFormat.KEY_DURATION)
                            val file = java.io.File(filePath)
                            if (durationUs > 0 && file.exists()) {
                                val fileSizeInBytes = file.length()
                                // Bitrate (bps) = (File Size in bits) / (Duration in seconds)
                                val estimatedBitrate = (fileSizeInBytes * 8) / (durationUs / 1_000_000.0)
                                bitRate = estimatedBitrate.toInt()
                                Logger.logInfo(TAG, "KEY_BIT_RATE was low, estimated new bitrate: $bitRate bps")
                            }
                        } catch (e: Exception) {
                            // This can happen with malformed files, log it and continue with the original value
                            e.printStackTrace()
                        }
                    }

                    // --- Sample Rate Extraction ---
                    val samplingRate = if (format.containsKey(MediaFormat.KEY_SAMPLE_RATE)) {
                        format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                    } else {
                        null
                    }

                    // --- Format Description ---
                    val audioFormat = getFormatDescription(mime)

                    Logger.logInfo(TAG, "Extracted Info: Format=$audioFormat, Bitrate=$bitRate, SampleRate=$samplingRate")

                    // We found the audio track, create the MediaInfo object and return.
                    if(bitRate != null && samplingRate != null) {
                        return MediaInfo(
                            samplingRate = samplingRate,
                            bitRateInKbps = bitRate/1000,
                            format = audioFormat
                        )
                    } else {
                        return null
                    }
                }
            }
            // If loop finishes without finding an audio track
            Logger.logInfo(TAG, "No audio track found in file: $filePath")
            return null

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            extractor.release()
        }
    }

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