package com.commonsense.android.kotlin.base.compat

import com.commonsense.android.kotlin.test.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 20-05-2018.
 * Purpose:
 */
internal class TrustManagerFactoryTest {

    @Test
    fun getDefaultX509Trust() {
        TrustManagerFactory.getDefaultX509Trust()
                .assertNotNull("there should always be a default trust manager pr java spec")
    }
}