package com.ka.revolution.unit;

import com.ka.revolution.TestConstants;
import com.ka.revolution.model.com.request.TransferMoneyRequest;
import com.ka.revolution.util.FileUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileUtilTest {

    private InputStream inputStream;

    @Before
    public void beforeTests() {
        inputStream = this.getClass().getClassLoader().getResourceAsStream(TestConstants.TEST_FILE);
    }

    @Test
    public void whenConvertProperInputStreamToObject_thenSuccess() throws IOException {
        final TransferMoneyRequest request = FileUtil.convertJsonStreamToObject(inputStream, TransferMoneyRequest.class);

        assertNotNull(request);
        assertEquals(new Long(100), request.getDestinationAccountId());
        assertEquals(BigDecimal.valueOf(100), request.getAmount());
    }

    @Test
    public void whenConvertNonExistingInputStreamToString_thenReturnNull() throws IOException {
        final String result = FileUtil.convertInputStreamToString(null);

        Assert.assertNull(result);
    }

}
