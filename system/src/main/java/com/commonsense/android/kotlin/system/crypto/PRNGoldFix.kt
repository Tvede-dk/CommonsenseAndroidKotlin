package com.commonsense.android.kotlin.system.crypto

import com.commonsense.android.kotlin.system.extensions.apiLevel
import com.commonsense.android.kotlin.system.extensions.isApiGreaterThan
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.Provider
import java.security.SecureRandom
import java.security.Security


/**
 * Created by Kasper Tvede on 10-04-2018.
 * taken from
 * https://android-developers.googleblog.com/2013/08/some-securerandom-thoughts.html
 * and simplified.
 * Purpose: fix old bad issue with PRNG
 *
 * "We have now determined that applications which use the Java Cryptography Architecture (JCA) for key generation,
 *   signing, or random number generation may not receive cryptographically strong values on Android devices
 *   due to improper initialization of the underlying PRNG"
 */

class PRNGoldFix {
    //the first version where the problems started
    private val marshmellow = 16
    //up to this version
    private val marshmellow_mr2 = 18


    /**
     * Applies the fix for OpenSSL PRNG having low entropy. Does nothing if the
     * fix is not needed.
     *
     * @throws SecurityException if the fix is needed but could not be applied.
     */
    @Throws(SecurityException::class)
    private fun applyOpenSSLFix() {
        if (apiLevel in marshmellow..marshmellow_mr2) {
            // No need to apply the fix
            return
        }
        try {
            // Mix in the device- and invocation-specific seed.
            Class.forName("org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                    .getMethod("RAND_seed", ByteArray::class.java)
                    .invoke(null, generateSeed())

            // Mix output of Linux PRNG into OpenSSL's PRNG
            val bytesRead = Class.forName(
                    "org.apache.harmony.xnet.provider.jsse.NativeCrypto")
                    .getMethod("RAND_load_file", String::class.java, Long::class.javaPrimitiveType)
                    .invoke(null, "/dev/urandom", 1024) as Int
            if (bytesRead != 1024) {
                throw IOException(
                        "Unexpected number of bytes read from Linux PRNG: $bytesRead")
            }
        } catch (e: Exception) {
            throw SecurityException("Failed to seed OpenSSL PRNG", e)
        }

    }

    /**
     * Installs a Linux PRNG-backed `SecureRandom` implementation as the
     * default. Does nothing if the implementation is already the default or if
     * there is not need to install the implementation.
     *
     * @throws SecurityException if the fix is needed but could not be applied.
     */
    @Throws(SecurityException::class)
    private fun installLinuxPRNGSecureRandom() {
        if (isApiGreaterThan(marshmellow_mr2)) {
            // No need to apply the fix
            return
        }

        // Install a Linux PRNG-based SecureRandom implementation as the
        // default, if not yet installed.
        val secureRandomProviders = Security.getProviders("SecureRandom.SHA1PRNG")
        if (secureRandomProviders == null
                || secureRandomProviders.isEmpty()
                || LinuxPRNGSecureRandomProvider::class.java != secureRandomProviders[0].javaClass) {
            Security.insertProviderAt(LinuxPRNGSecureRandomProvider(), 1)
        }

        // Assert that new SecureRandom() and
        // SecureRandom.getInstance("SHA1PRNG") return a SecureRandom backed
        // by the Linux PRNG-based SecureRandom implementation.
        val rng1 = SecureRandom()
        if (LinuxPRNGSecureRandomProvider::class.java != rng1.provider.javaClass) {
            throw SecurityException(
                    "new SecureRandom() backed by wrong Provider: " + rng1.provider.javaClass)
        }

        val rng2: SecureRandom
        try {
            rng2 = SecureRandom.getInstance("SHA1PRNG")
        } catch (e: NoSuchAlgorithmException) {
            throw SecurityException("SHA1PRNG not available", e)
        }

        if (LinuxPRNGSecureRandomProvider::class.java != rng2.provider.javaClass) {
            throw SecurityException(
                    "SecureRandom.getInstance(\"SHA1PRNG\") backed by wrong"
                            + " Provider: " + rng2.provider.javaClass)
        }
    }

    /**
     * `Provider` of `SecureRandom` engines which pass through
     * all requests to the Linux PRNG.
     */
    private class LinuxPRNGSecureRandomProvider : Provider("LinuxPRNG", 1.0, ("A Linux-specific random number provider that uses" + " /dev/urandom")) {
        init {
            // Although /dev/urandom is not a SHA-1 PRNG, some apps
            // explicitly request a SHA1PRNG SecureRandom and we thus need to
            // prevent them from getting the default implementation whose output
            // may have low entropy.
            put("SecureRandom.SHA1PRNG", LinuxPRNGSecureRandom::class.java.name)
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software")
        }
    }
}
