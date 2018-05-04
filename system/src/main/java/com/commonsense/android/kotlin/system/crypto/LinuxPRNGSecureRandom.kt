package com.commonsense.android.kotlin.system.crypto

import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.system.logging.tryAndLog
import java.io.*
import java.security.SecureRandomSpi

/**
 * [SecureRandomSpi] which passes all requests to the Linux PRNG
 * (`/dev/urandom`).
 */
class LinuxPRNGSecureRandom : SecureRandomSpi() {

    private val sLock = Any()

    /**
     * Whether this engine instance has been seeded. This is needed because
     * each instance needs to seed itself if the client does not explicitly
     * seed it.
     */
    private var mSeeded: Boolean = false

    /**
     * Input stream for reading from Linux PRNG
     *
     * @GuardedBy("sLock")
     */
    private val sUrandomIn: DataInputStream by lazy {
        synchronized(sLock) {
            DataInputStream(FileInputStream(URANDOM_FILE))
        }
    }

    /**
     * Output stream for writing to Linux PRNG
     *
     * @GuardedBy("sLock")
     */
    private val sUrandomOut: OutputStream by lazy {
        synchronized(sLock) {
            FileOutputStream(URANDOM_FILE)
        }
    }

    override fun engineSetSeed(bytes: ByteArray) {
        // On a small fraction of devices /dev/urandom is not writable.
        // Log and ignore.
        tryAndLog(this::class.java.simpleName, "Failed to mix seed into $URANDOM_FILE", L::warning) {
            val out: OutputStream = synchronized(sLock) {
                sUrandomOut
            }
            out.write(bytes)
            out.flush()
        }
        mSeeded = true
    }

    override fun engineNextBytes(bytes: ByteArray) {
        if (!mSeeded) {
            // Mix in the device- and invocation-specific seed.
            engineSetSeed(generateSeed())
        }
        tryAndLog(this::class.java.simpleName, "Failed to read from $URANDOM_FILE", L::warning) {
            val dIn = synchronized(sLock) {
                sUrandomIn
            }
            synchronized(dIn) {
                dIn.readFully(bytes)
            }
        }
    }

    /**
     *
     */
    override fun engineGenerateSeed(size: Int): ByteArray {
        return ByteArray(size).apply {
            engineNextBytes(this)
        }
    }

    companion object {
        /*
         * IMPLEMENTATION NOTE: Requests to generate bytes and to mix in a seed
         * are passed through to the Linux PRNG (/dev/urandom). Instances of
         * this class seed themselves by mixing in the current time, PID, UID,
         * build fingerprint, and hardware serial number (where available) into
         * Linux PRNG.
         *
         * Concurrency: Read requests to the underlying Linux PRNG are
         * serialized (on sLock) to ensure that multiple threads do not get
         * duplicated PRNG output.
         */

        private val URANDOM_FILE = File("/dev/urandom")

    }
}