package com.stripe.android.paymentsheet.paymentdatacollection.ach

import com.stripe.android.paymentsheet.R
import org.junit.Assert
import org.junit.Test

class TransformToBankIconTest {
    @Test
    fun `given valid bank name, transform returns the correct bank icon`() {
        Assert.assertEquals(TransformToBankIcon("Bank of America"), R.drawable.stripe_ic_bank_boa)
        Assert.assertEquals(TransformToBankIcon("bank of america"), R.drawable.stripe_ic_bank_boa)
        Assert.assertEquals(TransformToBankIcon("Capital One"), R.drawable.stripe_ic_bank_capitalone)
        Assert.assertEquals(TransformToBankIcon("capital one"), R.drawable.stripe_ic_bank_capitalone)
        Assert.assertEquals(TransformToBankIcon("BBVA"), R.drawable.stripe_ic_bank_compass)
        Assert.assertEquals(TransformToBankIcon("bbva"), R.drawable.stripe_ic_bank_compass)
        Assert.assertEquals(TransformToBankIcon("Compass"), R.drawable.stripe_ic_bank_compass)
        Assert.assertEquals(TransformToBankIcon("compass"), R.drawable.stripe_ic_bank_compass)
        Assert.assertEquals(TransformToBankIcon("Citi"), R.drawable.stripe_ic_bank_citi)
        Assert.assertEquals(TransformToBankIcon("citi"), R.drawable.stripe_ic_bank_citi)
        Assert.assertEquals(TransformToBankIcon("MORGAN CHASE"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("morgan chase"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("JP MORGAN"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("jp morgan"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("Chase"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("chase"), R.drawable.stripe_ic_bank_morganchase)
        Assert.assertEquals(TransformToBankIcon("NAVY FEDERAL CREDIT UNION"), R.drawable.stripe_ic_bank_nfcu)
        Assert.assertEquals(TransformToBankIcon("navy federal credit union"), R.drawable.stripe_ic_bank_nfcu)
        Assert.assertEquals(TransformToBankIcon("PNC BANK"), R.drawable.stripe_ic_bank_pnc)
        Assert.assertEquals(TransformToBankIcon("pnc bank"), R.drawable.stripe_ic_bank_pnc)
        Assert.assertEquals(TransformToBankIcon("PNC Bank"), R.drawable.stripe_ic_bank_pnc)
        Assert.assertEquals(TransformToBankIcon("SUNTRUST"), R.drawable.stripe_ic_bank_suntrust)
        Assert.assertEquals(TransformToBankIcon("suntrust"), R.drawable.stripe_ic_bank_suntrust)
        Assert.assertEquals(TransformToBankIcon("SunTrust Bank"), R.drawable.stripe_ic_bank_suntrust)
        Assert.assertEquals(TransformToBankIcon("suntrust bank"), R.drawable.stripe_ic_bank_suntrust)
        Assert.assertEquals(TransformToBankIcon("Silicon Valley Bank"), R.drawable.stripe_ic_bank_svb)
        Assert.assertEquals(TransformToBankIcon("silicon valley bank"), R.drawable.stripe_ic_bank_svb)
        Assert.assertEquals(TransformToBankIcon("stripe"), R.drawable.stripe_ic_bank)
        Assert.assertEquals(TransformToBankIcon("Stripe"), R.drawable.stripe_ic_bank)
        Assert.assertEquals(TransformToBankIcon("stripe"), R.drawable.stripe_ic_bank)
        Assert.assertEquals(TransformToBankIcon("TestInstitution"), R.drawable.stripe_ic_bank)
        Assert.assertEquals(TransformToBankIcon("testinstitution"), R.drawable.stripe_ic_bank)
        Assert.assertEquals(TransformToBankIcon("TD Bank"), R.drawable.stripe_ic_bank_td)
        Assert.assertEquals(TransformToBankIcon("td bank"), R.drawable.stripe_ic_bank_td)
        Assert.assertEquals(TransformToBankIcon("USAA FEDERAL SAVINGS BANK"), R.drawable.stripe_ic_bank_usaa)
        Assert.assertEquals(TransformToBankIcon("usaa federal savings bank"), R.drawable.stripe_ic_bank_usaa)
        Assert.assertEquals(TransformToBankIcon("USAA Bank"), R.drawable.stripe_ic_bank_usaa)
        Assert.assertEquals(TransformToBankIcon("usaa bank"), R.drawable.stripe_ic_bank_usaa)
        Assert.assertEquals(TransformToBankIcon("U.S. BANK"), R.drawable.stripe_ic_bank_usbank)
        Assert.assertEquals(TransformToBankIcon("u.s. bank"), R.drawable.stripe_ic_bank_usbank)
        Assert.assertEquals(TransformToBankIcon("US Bank"), R.drawable.stripe_ic_bank_usbank)
        Assert.assertEquals(TransformToBankIcon("us bank"), R.drawable.stripe_ic_bank_usbank)
        Assert.assertEquals(TransformToBankIcon("Wells Fargo"), R.drawable.stripe_ic_bank_wellsfargo)
        Assert.assertEquals(TransformToBankIcon("wells fargo"), R.drawable.stripe_ic_bank_wellsfargo)
    }
}
