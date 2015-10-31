package unit.com.bitdubai.fermat_cry_plugin.layer.crypto_module.crypto_address_book.developer.bitdubai.DeveloperBitDubai;

import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.TimeFrequency;
import com.bitdubai.fermat_cry_plugin.layer.crypto_module.crypto_address_book.developer.bitdubai.DeveloperBitDubaiOld;
import com.bitdubai.fermat_cry_plugin.layer.crypto_module.crypto_address_book.developer.bitdubai.version_1.CryptoAddressBookCryptoModulePluginRoot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by natalia on 07/09/15.
 */
public class DeveloperBitDubaiTest {

    DeveloperBitDubaiOld developTest;

    @Before
    public void setUpVariable1(){
        developTest = new DeveloperBitDubaiOld();
    }

    @Test
    public void constructorTest (){
        DeveloperBitDubaiOld developerBitDubaiOld = new DeveloperBitDubaiOld();
        Assert.assertNotNull(developerBitDubaiOld.getPlugin());
    }

    @Test
    public void getterTest(){
        DeveloperBitDubaiOld developerBitDubaiOld = new DeveloperBitDubaiOld();
        Assert.assertEquals(developerBitDubaiOld.getAddress(), "13gpMizSNvQCbJzAPyGCUnfUGqFD8ryzcv");

        Assert.assertEquals(developerBitDubaiOld.getAmountToPay(), 100);

        Assert.assertEquals(developerBitDubaiOld.getCryptoCurrency(), CryptoCurrency.BITCOIN);

        Assert.assertEquals(developerBitDubaiOld.getTimePeriod(), TimeFrequency.MONTHLY);
    }

    @Test
    public void getPluging() {
        assertThat(developTest.getPlugin()).isInstanceOf(CryptoAddressBookCryptoModulePluginRoot.class);
    }
}

