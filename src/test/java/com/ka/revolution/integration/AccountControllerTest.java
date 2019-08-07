package com.ka.revolution.integration;

import com.ka.revolution.TestConstants;
import com.ka.revolution.controller.AccountController;
import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.com.request.TransferMoneyRequest;
import com.ka.revolution.model.com.response.GetAccountsResponse;
import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.service.AccountService;
import com.ka.revolution.util.FileUtil;
import express.Express;
import express.utils.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private Express server;
    private HttpClient httpClient;
    private Account account;
    private Account destinationAccount;

    @Before
    public void beforeTests() {
        MockitoAnnotations.initMocks(this);

        server = new Express();
        server.bind(new AccountController(accountService));
        server.listen(TestConstants.TEST_PORT);

        httpClient = HttpClientBuilder.create().build();

        account = Account.builder()
                .id(TestConstants.FIRST_ACCOUNT_ID)
                .amount(TestConstants.FIRST_ACCOUNT_AMOUNT)
                .fullName(TestConstants.FIRST_ACCOUNT_NAME)
                .build();

        destinationAccount = Account.builder()
                .id(TestConstants.SECOND_ACCOUNT_ID)
                .amount(TestConstants.SECOND_ACCOUNT_AMOUNT)
                .fullName(TestConstants.SECOND_ACCOUNT_NAME)
                .build();
    }

    @Test
    public void givenAccounts_whenGetAccountsCalled_thenReturnAccounts() throws IOException {
        Mockito.when(accountService.getAccounts()).thenReturn(Arrays.asList(account));

        final HttpUriRequest request = new HttpGet(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT);
        final HttpResponse response = httpClient.execute(request);
        final GetAccountsResponse responseModel = FileUtil.convertJsonStreamToObject(response.getEntity().getContent(), GetAccountsResponse.class);
        final List<Account> accounts = responseModel.getAccounts();

        assertEquals(MediaType._json.getMIME(), ContentType.getOrDefault(response.getEntity()).getMimeType());
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals(1, accounts.size());
        assertAccountContent(accounts.get(0));

        Mockito.verify(accountService, Mockito.times(1)).getAccounts();
    }

    @Test
    public void givenAccounts_whenGetAccountByIdCalled_thenReturnAccount() throws IOException {
        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);

        final HttpUriRequest request = new HttpGet(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT + "/" + TestConstants.FIRST_ACCOUNT_ID);
        final HttpResponse response = httpClient.execute(request);
        final Account account = FileUtil.convertJsonStreamToObject(response.getEntity().getContent(), Account.class);

        assertEquals(MediaType._json.getMIME(), ContentType.getOrDefault(response.getEntity()).getMimeType());
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertAccountContent(account);

        Mockito.verify(accountService, Mockito.times(1)).findAccountById(TestConstants.FIRST_ACCOUNT_ID);
    }

    @Test
    public void givenNoAccounts_whenGetAccountByIdCalled_thenReturnNotFound() throws IOException {
        final HttpUriRequest request = new HttpGet(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT + "/" + TestConstants.FIRST_ACCOUNT_ID);
        final HttpResponse response = httpClient.execute(request);

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(1)).findAccountById(TestConstants.FIRST_ACCOUNT_ID);
    }

    @Test
    public void givenNoAccounts_whenGetAccountsCalled_thenReturnEmptyList() throws IOException {
        final HttpUriRequest request = new HttpGet(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT);
        final HttpResponse response = httpClient.execute(request);
        final GetAccountsResponse responseModel = FileUtil.convertJsonStreamToObject(response.getEntity().getContent(), GetAccountsResponse.class);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertTrue(responseModel.getAccounts().isEmpty());

        Mockito.verify(accountService, Mockito.times(1)).getAccounts();
    }

    @Test
    public void givenCorrectValues_whenSaveAccount_thenReturnOk() throws IOException {
        final SaveAccountRequest saveAccountRequest = new SaveAccountRequest();
        saveAccountRequest.setAmount(TestConstants.THIRD_ACCOUNT_AMOUNT);
        saveAccountRequest.setFullName(TestConstants.THIRD_ACCOUNT_NAME);

        final HttpPost request = new HttpPost(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT);
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(saveAccountRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(1)).saveAccount(saveAccountRequest);
    }

    @Test
    public void givenEmptyFullName_whenSaveAccount_thenReturnBadRequest() throws IOException {
        final SaveAccountRequest saveAccountRequest = new SaveAccountRequest();
        saveAccountRequest.setAmount(TestConstants.THIRD_ACCOUNT_AMOUNT);

        final HttpPost request = new HttpPost(TestConstants.CONTEXT_ADDRESS + TestConstants.RESOURCE_ACCOUNT);
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(saveAccountRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(0)).saveAccount(saveAccountRequest);
    }

    @Test
    public void givenAccounts_whenTransferMoney_thenReturnOk() throws IOException {
        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);
        Mockito.when(accountService.findAccountById(TestConstants.SECOND_ACCOUNT_ID)).thenReturn(destinationAccount);

        final StringBuilder requestBuilder = buildTransferMoneyRequest();

        final TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest();
        transferMoneyRequest.setAmount(BigDecimal.valueOf(25000));
        transferMoneyRequest.setDestinationAccountId(destinationAccount.getId());

        final HttpPost request = new HttpPost(requestBuilder.toString());
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(transferMoneyRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(1)).transferMoney(account.getId(), transferMoneyRequest);
    }

    @Test
    public void givenDestinationAccountIdIsEmpty_whenTransferMoney_thenReturnBadRequest() throws IOException {
        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);
        Mockito.when(accountService.findAccountById(TestConstants.SECOND_ACCOUNT_ID)).thenReturn(destinationAccount);

        final StringBuilder requestBuilder = buildTransferMoneyRequest();

        final TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest();
        transferMoneyRequest.setAmount(BigDecimal.valueOf(25000));

        final HttpPost request = new HttpPost(requestBuilder.toString());
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(transferMoneyRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(0)).transferMoney(account.getId(), transferMoneyRequest);
    }

    @Test
    public void givenEmptyAmount_whenTransferMoney_thenReturnBadRequest() throws IOException {
        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);
        Mockito.when(accountService.findAccountById(TestConstants.SECOND_ACCOUNT_ID)).thenReturn(destinationAccount);

        final StringBuilder requestBuilder = buildTransferMoneyRequest();

        final TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest();
        transferMoneyRequest.setDestinationAccountId(TestConstants.SECOND_ACCOUNT_ID);

        final HttpPost request = new HttpPost(requestBuilder.toString());
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(transferMoneyRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(0)).transferMoney(account.getId(), transferMoneyRequest);
    }

    @Test
    public void givenNegativeTransferAmount_whenTransferMoney_thenReturnBadRequest() throws IOException {
        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);
        Mockito.when(accountService.findAccountById(TestConstants.SECOND_ACCOUNT_ID)).thenReturn(destinationAccount);

        final StringBuilder requestBuilder = buildTransferMoneyRequest();

        final TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest();
        transferMoneyRequest.setDestinationAccountId(TestConstants.SECOND_ACCOUNT_ID);
        transferMoneyRequest.setAmount(BigDecimal.valueOf(-1000));

        final HttpPost request = new HttpPost(requestBuilder.toString());
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(transferMoneyRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(0)).transferMoney(account.getId(), transferMoneyRequest);
    }

    @Test
    public void givenNotEnoughOriginationAccountBalance_whenTransferMoney_thenReturnNotAllowed() throws IOException {
        final TransferMoneyRequest transferMoneyRequest = new TransferMoneyRequest();
        transferMoneyRequest.setDestinationAccountId(TestConstants.SECOND_ACCOUNT_ID);
        transferMoneyRequest.setAmount(BigDecimal.valueOf(100000000));

        Mockito.when(accountService.findAccountById(TestConstants.FIRST_ACCOUNT_ID)).thenReturn(account);
        Mockito.when(accountService.findAccountById(TestConstants.SECOND_ACCOUNT_ID)).thenReturn(destinationAccount);
        Mockito.doThrow(new IllegalArgumentException()).when(accountService).transferMoney(account.getId(), transferMoneyRequest);

        final StringBuilder requestBuilder = buildTransferMoneyRequest();


        final HttpPost request = new HttpPost(requestBuilder.toString());
        request.setEntity(new StringEntity(FileUtil.convertObjectToJson(transferMoneyRequest)));

        final HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatusLine().getStatusCode());

        Mockito.verify(accountService, Mockito.times(1)).transferMoney(account.getId(), transferMoneyRequest);
    }

    private StringBuilder buildTransferMoneyRequest() {
        final StringBuilder requestBuilder = new StringBuilder(TestConstants.CONTEXT_ADDRESS);
        requestBuilder.append(TestConstants.RESOURCE_ACCOUNT);
        requestBuilder.append("/");
        requestBuilder.append(account.getId());
        requestBuilder.append("/");
        requestBuilder.append(TestConstants.PATH_TRANSFER);

        return requestBuilder;
    }

    private void assertAccountContent(Account account) {
        assertEquals(TestConstants.FIRST_ACCOUNT_ID, account.getId());
        assertEquals(TestConstants.FIRST_ACCOUNT_NAME, account.getFullName());
        assertEquals(TestConstants.FIRST_ACCOUNT_AMOUNT, account.getAmount());
    }

    @After
    public void afterTests() throws InterruptedException {
        server.stop();
        server = null;
        Thread.sleep(100);
    }

}
