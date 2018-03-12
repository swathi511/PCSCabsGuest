package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.HashPojo;
import com.hjsoft.guestbooktaxi.model.PaymentPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 15/3/17.
 */
public class PaymentFragment extends Fragment {

    View v;
    EditText etReqAmount;
    Button btAddMoney;
    LinearLayout llStatus,llWalletAmount;
    TextView tvTxnStatus,tvAmount,tvWalletAmount;
    private String merchantKey, userCredentials,salt;
    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;
    private PayUChecksum checksum;
    // This sets the configuration
    private PayuConfig payuConfig;
    String amount;
    String guestProfileId;
    HashMap<String, String> user;
    SessionManager session;
    String companyId="CMP00001";
    API REST_CLIENT;
    DBAdapter dbAdapter;
    ProgressDialog progressDialog;
    TextView tv100,tv200,tv300,tv500;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_payments, container,false);
        etReqAmount=(EditText)v.findViewById(R.id.fp_et_amount_req);
        btAddMoney=(Button)v.findViewById(R.id.fp_bt_add_money);
        llStatus=(LinearLayout)v.findViewById(R.id.fp_ll_amount);
        llStatus.setVisibility(View.GONE);
        tvTxnStatus=(TextView)v.findViewById(R.id.fp_tv_txn_status);
        tvAmount=(TextView)v.findViewById(R.id.fp_tv_amount);
        tvWalletAmount=(TextView)v.findViewById(R.id.fp_tv_wallet_amount);
        llWalletAmount=(LinearLayout)v.findViewById(R.id.fp_ll_wallet);
        tv100=(TextView)v.findViewById(R.id.fp_tv_100);
        tv200=(TextView)v.findViewById(R.id.fp_tv_200);
        tv300=(TextView)v.findViewById(R.id.fp_tv_300);
        tv500=(TextView)v.findViewById(R.id.fp_tv_500);

        tv100.setText(getString(R.string.Rs)+" "+"100");
        tv200.setText(getString(R.string.Rs)+" "+"200");
        tv300.setText(getString(R.string.Rs)+" "+"300");
        tv500.setText(getString(R.string.Rs)+" "+"500");

        Payu.setInstance(getActivity());

        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        if(dbAdapter.isWalletPresent()>0)
        {
            tvWalletAmount.setText(getString(R.string.Rs)+" "+dbAdapter.getWalletAmount());
        }
        else {

            llWalletAmount.setVisibility(View.GONE);
        }

        btAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amount=etReqAmount.getText().toString().trim();

                if(amount.equals(""))
                {
                    Toast.makeText(getActivity(),"Enter Valid Amount !",Toast.LENGTH_LONG).show();
                }
                else {

                    etReqAmount.setText(" ");

                    navigateToBaseActivity(v);

                }
            }
        });

        tv100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etReqAmount.setText("100");
            }
        });

        tv200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etReqAmount.setText("200");
            }
        });

        tv300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etReqAmount.setText("300");
            }
        });

        tv500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etReqAmount.setText("500");
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        REST_CLIENT= RestClient.get();

    }

    public void navigateToBaseActivity(View view) {

        //testing credentials
       /* merchantKey = "gtKFFx";
        salt="eCwWELxi";*/
       //unknown credentials
//        merchantKey="TFuM0bQ1";
//        salt="UbcHi7gk6A";

        //******* production credentials
        merchantKey = "OzqBSo";
        salt="BaEjzGeU";

        String email = "support@hjsoftware.net";

       /* String value = environmentSpinner.getSelectedItem().toString();
        int environment;
        String TEST_ENVIRONMENT = getResources().getString(R.string.test);
        if (value.equals(TEST_ENVIRONMENT))
            environment = PayuConstants.STAGING_ENV;
        else
            environment = PayuConstants.PRODUCTION_ENV;
            */

        int environment= PayuConstants.PRODUCTION_ENV;
        //int environment= PayuConstants.STAGING_ENV;

        userCredentials = merchantKey + ":" + email;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = "gtKFFx"
         * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("product_info");
        mPaymentParams.setFirstName("firstname");
        mPaymentParams.setEmail(email);
        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId("" + System.currentTimeMillis());
        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("http://api.travelsmate.in/PayStatusCheck.aspx?companyid="+companyId);
        mPaymentParams.setFurl("http://api.travelsmate.in/PayStatusCheck.aspx?companyid="+companyId);
//        mPaymentParams.setSurl("http://192.168.1.5:1533/PayStatusCheck.aspx?companyid="+companyId);
//        mPaymentParams.setFurl("http://192.168.1.5:1533/PayStatusCheck.aspx?companyid="+companyId);

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        /*

        mPaymentParams.setCardNumber("4012001038443335");
        mPaymentParams.setCardName("Visa");
        mPaymentParams.setNameOnCard("abc");
        mPaymentParams.setExpiryMonth("05");// MM
        mPaymentParams.setExpiryYear("2017");// YYYY
         mPaymentParams.setCvv("123");
        mPaymentParams.setEnableOneClickPayment(1);
        */
        //#sthash.A4Qk61dt.dpuf;

        //mPaymentParams.setBankCode("ICIB");

        //- See more at: http://www.tothenew.com/blog/payu-payment-gateway-android-integration/#sthash.A4Qk61dt.dpuf


        // PayuHashes payuHashes=new PayuHashes();
        // mPaymentParams.setHash(payuHashes.getPaymentHash());

        // String hash=sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
        // mPaymentParams.setHash("123abc");
        // **************************************
      /*
        mPaymentParams.setHash(generateHashFromSDK(mPaymentParams,salt));
        try {
            ;
            PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
            if (postData.getCode() == PayuErrors.NO_ERROR) { // launch webview
                PayuConfig payuConfig = new PayuConfig();
                payuConfig.setEnvironment(PayuConstants.STAGING_ENV);
                payuConfig.setData(postData.getResult());
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
            } else {
                // something went wrong
                Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        //- See more at: http://www.tothenew.com/blog/payu-payment-gateway-android-integration/#sthash.A4Qk61dt.dpuf



        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        // generateHashFromServer(mPaymentParams);

        getHashFromServer(mPaymentParams);

        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        //String salt = "13p0PXZk";
        //generateHashFromSDK(mPaymentParams, salt);

    }


    public void getHashFromServer(PaymentParams mPaymentParams)
    {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        JsonObject v=new JsonObject();
        v.addProperty("txnid",mPaymentParams.getTxnId());
        v.addProperty("amount",amount);
        v.addProperty("product_info",mPaymentParams.getProductInfo());
        v.addProperty("firstname",mPaymentParams.getFirstName());
        v.addProperty("email",mPaymentParams.getEmail());
        v.addProperty("udf1",mPaymentParams.getUdf1());
        v.addProperty("udf2",mPaymentParams.getUdf2());
        v.addProperty("udf3",mPaymentParams.getUdf3());
        v.addProperty("udf4",mPaymentParams.getUdf4());
        v.addProperty("udf5",mPaymentParams.getUdf5());
        v.addProperty("user_credentials",mPaymentParams.getUserCredentials());
        v.addProperty("companyid",companyId);

        Call<List<HashPojo>> call=REST_CLIENT.getHashValue(v);
        call.enqueue(new Callback<List<HashPojo>>() {
            @Override
            public void onResponse(Call<List<HashPojo>> call, Response<List<HashPojo>> response) {

                HashPojo hashData;
                List<HashPojo> hashList;

                if(response.isSuccessful()) {
                    progressDialog.dismiss();
                    hashList = response.body();
                    hashData=hashList.get(0);
                    PayuHashes payuHashes = new PayuHashes();
                    payuHashes.setPaymentHash(hashData.getPaymentHash());
                    payuHashes.setVasForMobileSdkHash(hashData.getVASFORMobileSDK());
                    payuHashes.setPaymentRelatedDetailsForMobileSdkHash(hashData.getPaymentRelatedDetails());

                    if (hashData.getDeleteUserCard() != null) {
                        payuHashes.setDeleteCardHash(hashData.getDeleteUserCard());
                    }

                    if (hashData.getEditUserCard() != null) {
                        payuHashes.setEditCardHash(hashData.getEditUserCard());
                    }

                    if (hashData.getGetUserCard() != null) {
                        payuHashes.setStoredCardsHash(hashData.getGetUserCard());
                    }

                    if (hashData.getSaveUserCard() != null) {
                        payuHashes.setSaveCardHash(hashData.getSaveUserCard());
                    }

                    if(hashData.getIBIBOCodesHash()!=null) {
                        // payuHashes.setMerchantIbiboCodesHash(hashData.getIBIBOCodesHash());
                    }

                    launchSdkUI(payuHashes);
                }
                else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<HashPojo>> call, Throwable t) {

                progressDialog.dismiss();

                Toast.makeText(getActivity(),"Connectivity error!",Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });

    }

    public void generateHashFromServer(PaymentParams mPaymentParams) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        //System.out.println("Checking User Credentials..."+mPaymentParams.getUserCredentials());
        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    private class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        //TODO Below three hashes are mandatory for payment flow and needs to be generated at merchant server
                        /**
                         * Payment hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_hash -
                         *
                         * sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
                         *
                         */
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));

                            //System.out.println(response.getString(key));

                            String serverCalculatedHash=hashCal(merchantKey+"|"+mPaymentParams.getTxnId()+"|"+amount+"|"+mPaymentParams.getProductInfo()+"|"
                                    +mPaymentParams.getFirstName()+"|"+mPaymentParams.getEmail()+"|"+mPaymentParams.getUdf1()+"|"+mPaymentParams.getUdf2()+"|"+
                                    mPaymentParams.getUdf3()+"|"+mPaymentParams.getUdf4()+"|"+mPaymentParams.getUdf5()+"||||||"+salt);
                            //System.out.println("My hash is ... "+serverCalculatedHash);
                            break;
                        /**
                         * vas_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating vas_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be "default"
                         *
                         */
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            //System.out.println(response.getString(key));
                            break;
                        /**
                         * payment_related_details_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_related_details_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                           // System.out.println(response.getString(key));
                            break;

                        //TODO Below hashes only needs to be generated if you are using Store card feature
                        /**
                         * delete_user_card_hash is used while deleting a stored card.
                         * Below is formula for generating delete_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        /**
                         * get_user_cards_hash is used while fetching all the cards corresponding to a user.
                         * Below is formula for generating get_user_cards_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        /**
                         * edit_user_card_hash is used while editing details of existing stored card.
                         * Below is formula for generating edit_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        /**
                         * save_user_card_hash is used while saving card to the vault
                         * Below is formula for generating save_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;

                        //TODO This hash needs to be generated if you are using any offer key
                        /**
                         * check_offer_status_hash is used while using check_offer_status api
                         * Below is formula for generating check_offer_status_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be Offer Key.
                         *
                         */
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);

            progressDialog.dismiss();
            launchSdkUI(payuHashes);
        }
    }

    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(getActivity(), PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        //Lets fetch all the one click card tokens first
        //fetchMerchantHashes(intent);

        startActivityForResult(intent,PayuConstants.PAYU_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        //Toast.makeText(getActivity(),requestCode+":::::"+data,Toast.LENGTH_LONG).show();
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                try {

                    JSONObject j = new JSONObject(data.getStringExtra("payu_response"));
//                    System.out.println(j.getString("status"));
//                    System.out.println(j.getString("id"));
//                    System.out.println(j.getString("key"));
//                    System.out.println(data.getStringExtra("result"));

                    String stMode,stStatus,stTxnid,stAmnt,stAddChrgs,stAddedOn,stPaySrc,stPGType,stBankRefNo,stIbiboCode;
                    String stErrCode,stErrMsg,stNameCard,stCardNo,stIssuingBank,stCardType;


                    switch (j.getString("status")) {
                        case "success":
//                            String revHash = hashCal(salt + "|" + j.getString("status") + "||||||" + j.getString("udf5") + "|" + j.getString("udf4") + "|" + j.getString("udf3") + "|" + j.getString("udf2") + "|" + j.getString("udf1") + "|" + j.getString("email") + "|" + j.getString("firstname") + "|" + j.getString("productinfo") + "|" + j.getString("amount") + "|" + j.getString("txnid") + "|" + merchantKey);
//                            System.out.println("Reverse hash is " + revHash);
//                            System.out.println("Hash is " + j.getString("hash"));

                            // if (revHash.equals(j.getString("hash"))) {

                           /* System.out.println(guestProfileId+":"+j.getString("mode")+":"+j.getString("status"));
                            System.out.println(j.getString("txnid")+":"+j.getString("additional_charges"));
                            System.out.println(amount);
                            System.out.println(j.getString("addedon"));
                            System.out.println(j.getString("payment_source"));
                            System.out.println(j.getString("PG_TYPE"));
                            System.out.println(j.getString("bank_ref_no"));
                            System.out.println(j.getString("ibibo_code"));
                            System.out.println(j.getString("error_code"));
                            System.out.println(j.getString("Error_Message"));
                            System.out.println(j.getString("name_on_card"));
                            System.out.println(j.getString("card_no"));
                            System.out.println(j.getString("issuing_bank"));
                            System.out.println(j.getString("card_type"));
                            System.out.println(companyId);*/

                            //String stMode,stStatus,stTxnid,stAmnt,stAddChrgs,stAddedOn,stPaySrc,stPGType,stBankRefNo,stIbiboCode;
                           // String stErrCode,stErrMsg,stNameCard,stCardNo,stIssuingBank,stCardType;

                            if(j.has("mode")) {  stMode=j.getString("mode");   }
                            else {  stMode="No data"; }

                            if(j.has("status")){ stStatus=j.getString("status"); }
                            else { stStatus="No data"; }

                            if(j.has("txnid")){ stTxnid=j.getString("txnid"); }
                            else { stTxnid="No data"; }

                            if(j.has("amount")){ stAmnt=j.getString("amount"); }
                            else { stAmnt="0"; }

                            if(j.has("additional_charges")){ stAddChrgs=j.getString("additional_charges"); }
                            else { stAddChrgs="0"; }

                            if(j.has("addedon")){ stAddedOn=j.getString("addedon"); }
                            else { stAddedOn="No data"; }

                            if(j.has("payment_source")){ stPaySrc=j.getString("payment_source"); }
                            else { stPaySrc="PayU"; }

                            if(j.has("PG_TYPE")){ stPGType=j.getString("PG_TYPE"); }
                            else { stPGType="No data"; }

                            if(j.has("bank_ref_no")){ stBankRefNo=j.getString("bank_ref_no"); }
                            else { stBankRefNo="No data"; }

                            if(j.has("ibibo_code")){ stIbiboCode=j.getString("ibibo_code"); }
                            else { stIbiboCode="No data"; }

                            if(j.has("error_code")){ stErrCode=j.getString("error_code"); }
                            else { stErrCode="NA"; }

                            if(j.has("Error_Message")){ stErrMsg=j.getString("Error_Message"); }
                            else { stErrMsg="NA"; }

                            if(j.has("name_on_card")){ stNameCard=j.getString("name_on_card"); }
                            else { stNameCard="No Data"; }

                            if(j.has("card_no")){ stCardNo=j.getString("card_no"); }
                            else { stCardNo="No Data"; }

                            if(j.has("issuing_bank")){ stIssuingBank=j.getString("issuing_bank"); }
                            else { stIssuingBank="No Data"; }

                            if(j.has("card_type")){ stCardType=j.getString("card_type"); }
                            else { stCardType="No Data"; }

                            JsonObject v=new JsonObject();
                            v.addProperty("GuestProfileid",guestProfileId);
                            v.addProperty("mode",stMode);
                            v.addProperty("status",stStatus);
                            v.addProperty("txnid",stTxnid);
                            v.addProperty("amount",stAmnt);
                            v.addProperty("additional_charges",stAddChrgs);
                            v.addProperty("addedon",stAddedOn);
                            v.addProperty("payment_source",stPaySrc);
                            v.addProperty("PG_TYPE",stPGType);
                            v.addProperty("bank_ref_no",stBankRefNo);
                            v.addProperty("ibibo_code",stIbiboCode);
                            v.addProperty("error_code",stErrCode);
                            v.addProperty("Error_Message",stErrMsg);
                            v.addProperty("name_on_card",stNameCard);
                            v.addProperty("card_no",stCardNo);
                            v.addProperty("issuing_bank",stIssuingBank);
                            v.addProperty("card_type",stCardType);
                            v.addProperty("companyid",companyId);

                            Call<List<PaymentPojo>> call=REST_CLIENT.sendPaymentDetails(v);
                            call.enqueue(new Callback<List<PaymentPojo>>() {
                                @Override
                                public void onResponse(Call<List<PaymentPojo>> call, Response<List<PaymentPojo>> response) {

                                    List<PaymentPojo> resultList;
                                    PaymentPojo result;
                                    if(response.isSuccessful())
                                    {
                                        resultList=response.body();

                                        result=resultList.get(0);

                                        if(result.getStatus().equals("success"))
                                        {
                                            llStatus.setVisibility(View.VISIBLE);
                                            tvTxnStatus.setText("Transaction Successful !");
                                            tvAmount.setText(getString(R.string.Rs)+" "+result.getTotalwalletAmount());
                                            tvWalletAmount.setText(getString(R.string.Rs)+" "+result.getTotalwalletAmount());
                                            String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());
                                            if(dbAdapter.isWalletPresent()>0)
                                            {

                                                dbAdapter.updateWalletAmount(result.getTotalwalletAmount(),timeUpdated);
                                            }
                                            else {

                                                dbAdapter.insertWalletAmount(result.getTotalwalletAmount(),timeUpdated);
                                            }


                                        }
                                        else {
                                            llStatus.setVisibility(View.VISIBLE);
                                            tvTxnStatus.setText("Failed Transaction !");
                                            tvAmount.setText(getString(R.string.Rs)+" "+result.getTotalwalletAmount());
                                            tvWalletAmount.setText(getString(R.string.Rs)+" "+result.getTotalwalletAmount());

                                            String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());

                                            if(dbAdapter.isWalletPresent()>0)
                                            {

                                                dbAdapter.updateWalletAmount(result.getTotalwalletAmount(),timeUpdated);
                                            }
                                            else {
                                                dbAdapter.insertWalletAmount(result.getTotalwalletAmount(),timeUpdated);
                                            }
                                        }
                                    }
                                    else {

                                       // System.out.println("error is "+response.isSuccessful()+response.message()+response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<PaymentPojo>> call, Throwable t) {

                                   // t.printStackTrace();
                                    Toast.makeText(getActivity(),"Please Check Internet Connectivity !",Toast.LENGTH_LONG).show();
                                }
                            });

                                /*new AlertDialog.Builder(getActivity())
                                        .setCancelable(false)
                                        .setMessage("Your Transaction is successful!")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                            }
                                        }).show();*/


//                            } else {
//                                new AlertDialog.Builder(getActivity())
//                                        .setCancelable(false)
//                                        .setMessage("Oops! Failed Transaction" + "\n" + "Corrupted data found.")
//                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int whichButton) {
//                                                dialog.dismiss();
//                                            }
//                                        }).show();
//                            }

                            break;
                        case "failure":
                            //String stMode,stStatus,stTxnid,stAmnt,stAddChrgs,stAddedOn,stPaySrc,stPGType,stBankRefNo,stIbiboCode;
                            //String stErrCode,stErrMsg,stNameCard,stCardNo,stIssuingBank,stCardType;

                            if(j.has("mode")) {  stMode=j.getString("mode");   }
                            else {  stMode="No data"; }

                            if(j.has("status")){ stStatus=j.getString("status"); }
                            else { stStatus="No data"; }

                            if(j.has("txnid")){ stTxnid=j.getString("txnid"); }
                            else { stTxnid="No data"; }

                            if(j.has("amount")){ stAmnt=j.getString("amount"); }
                            else { stAmnt="0"; }

                            if(j.has("additional_charges")){ stAddChrgs=j.getString("additional_charges"); }
                            else { stAddChrgs="0"; }

                            if(j.has("addedon")){ stAddedOn=j.getString("addedon"); }
                            else { stAddedOn="No data"; }

                            if(j.has("payment_source")){ stPaySrc=j.getString("payment_source"); }
                            else { stPaySrc="PayU"; }

                            if(j.has("PG_TYPE")){ stPGType=j.getString("PG_TYPE"); }
                            else { stPGType="No data"; }

                            if(j.has("bank_ref_no")){ stBankRefNo=j.getString("bank_ref_no"); }
                            else { stBankRefNo="No data"; }

                            if(j.has("ibibo_code")){ stIbiboCode=j.getString("ibibo_code"); }
                            else { stIbiboCode="No data"; }

                            if(j.has("error_code")){ stErrCode=j.getString("error_code"); }
                            else { stErrCode="NA"; }

                            if(j.has("Error_Message")){ stErrMsg=j.getString("Error_Message"); }
                            else { stErrMsg="NA"; }

                            if(j.has("name_on_card")){ stNameCard=j.getString("name_on_card"); }
                            else { stNameCard="No Data"; }

                            if(j.has("card_no")){ stCardNo=j.getString("card_no"); }
                            else { stCardNo="No Data"; }

                            if(j.has("issuing_bank")){ stIssuingBank=j.getString("issuing_bank"); }
                            else { stIssuingBank="No Data"; }

                            if(j.has("card_type")){ stCardType=j.getString("card_type"); }
                            else { stCardType="No Data"; }

                            JsonObject b=new JsonObject();
                            b.addProperty("GuestProfileid",guestProfileId);
                            b.addProperty("mode",stMode);
                            b.addProperty("status",stStatus);
                            b.addProperty("txnid",stTxnid);
                            b.addProperty("amount",stAmnt);
                            b.addProperty("additional_charges",stAddChrgs);
                            b.addProperty("addedon",stAddedOn);
                            b.addProperty("payment_source",stPaySrc);
                            b.addProperty("PG_TYPE",stPGType);
                            b.addProperty("bank_ref_no",stBankRefNo);
                            b.addProperty("ibibo_code",stIbiboCode);
                            b.addProperty("error_code",stErrCode);
                            b.addProperty("Error_Message",stErrMsg);
                            b.addProperty("name_on_card",stNameCard);
                            b.addProperty("card_no",stCardNo);
                            b.addProperty("issuing_bank",stIssuingBank);
                            b.addProperty("card_type",stCardType);
                            b.addProperty("companyid",companyId);

                            Call<List<PaymentPojo>> call1=REST_CLIENT.sendPaymentDetails(b);
                            call1.enqueue(new Callback<List<PaymentPojo>>() {
                                @Override
                                public void onResponse(Call<List<PaymentPojo>> call, Response<List<PaymentPojo>> response) {


                                }

                                @Override
                                public void onFailure(Call<List<PaymentPojo>> call, Throwable t) {

                                    Toast.makeText(getActivity(),"Please Check Internet Connectivity !",Toast.LENGTH_LONG).show();
                                }
                            });


                            new AlertDialog.Builder(getActivity())
                                    .setCancelable(false)
                                    .setMessage("Oops! Failed Transaction" + "\n" + stErrMsg)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                            // String revHash1=hashCal(salt+"|"+j.getString("status")+"||||||"+j.getString("udf5")+"|"+j.getString("udf4")+"|"+j.getString("udf3")+"|"+j.getString("udf2")+"|"+j.getString("udf1")+"|"+j.getString("email")+"|"+j.getString("firstname")+"|"+j.getString("productinfo")+"|"+amount+"|"+j.getString("txnid")+"|"+merchantKey);
                           // String revHash1 = hashCal(salt + "|" + j.getString("status") + "||||||" + j.getString("udf5") + "|" + j.getString("udf4") + "|" + j.getString("udf3") + "|" + j.getString("udf2") + "|" + j.getString("udf1") + "|" + j.getString("email") + "|" + j.getString("firstname") + "|" + j.getString("productinfo") + "|" + j.getString("amount") + "|" + j.getString("txnid") + "|" + merchantKey);

                           // System.out.println("Reverse hash is " + revHash1);
                           // System.out.println("Hash is " + data.getStringExtra("hash"));
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }



               /* new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/

//                System.out.println(data.getStringExtra("payu_response"));
//                System.out.println(data.getStringExtra("result"));


            } else {
                Toast.makeText(getActivity(), "User cancelled transaction!", Toast.LENGTH_LONG).show();
            }

            //sha512(SALT|status||||||udf5|udf4|udf3|udf2|udf1|email|firstname|productinfo|amount|txnid|key)


        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }
}
