package com.antplay.preferences;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import com.antplay.computers.ComputerManagerService;
import com.antplay.R;
import com.antplay.nvstream.http.ComputerDetails;
import com.antplay.nvstream.http.NvHTTP;
import com.antplay.nvstream.jni.MoonBridge;
import com.antplay.utils.Const;
import com.antplay.utils.MyDialog;
import com.antplay.utils.RestClient;
import com.antplay.utils.ServerHelper;
import com.antplay.utils.SharedPreferenceUtils;
import com.antplay.utils.SpinnerDialog;
import com.antplay.utils.UiHelper;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddComputerManually extends Activity {
    private TextView hostText;
    private ComputerManagerService.ComputerManagerBinder managerBinder;
    private final LinkedBlockingQueue<String> computersToAdd = new LinkedBlockingQueue<>();
    private Thread addThread;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, final IBinder binder) {
            managerBinder = ((ComputerManagerService.ComputerManagerBinder) binder);
            startAddThread();
        }

        public void onServiceDisconnected(ComponentName className) {
            joinAddThread();
            managerBinder = null;
        }
    };

    private boolean isWrongSubnetSiteLocalAddress(String address) {
        try {
            InetAddress targetAddress = InetAddress.getByName(address);
            if (!(targetAddress instanceof Inet4Address) || !targetAddress.isSiteLocalAddress()) {
                return false;
            }

            // We have a site-local address. Look for a matching local interface.
            for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                    if (!(addr.getAddress() instanceof Inet4Address) || !addr.getAddress().isSiteLocalAddress()) {
                        // Skip non-site-local or non-IPv4 addresses
                        continue;
                    }

                    byte[] targetAddrBytes = targetAddress.getAddress();
                    byte[] ifaceAddrBytes = addr.getAddress().getAddress();

                    // Compare prefix to ensure it's the same
                    boolean addressMatches = true;
                    for (int i = 0; i < addr.getNetworkPrefixLength(); i++) {
                        if ((ifaceAddrBytes[i / 8] & (1 << (i % 8))) != (targetAddrBytes[i / 8] & (1 << (i % 8)))) {
                            addressMatches = false;
                            break;
                        }
                    }

                    if (addressMatches) {
                        return false;
                    }
                }
            }

            // Couldn't find a matching interface
            return true;
        } catch (Exception e) {
            // Catch all exceptions because some broken Android devices
            // will throw an NPE from inside getNetworkInterfaces().
            e.printStackTrace();
            return false;
        }
    }

    private URI parseRawUserInputToUri(String rawUserInput) {
        try {
            // Try adding a scheme and parsing the remaining input.
            // This handles input like 127.0.0.1:47989, [::1], [::1]:47989, and 127.0.0.1.
            URI uri = new URI("moonlight://" + rawUserInput);
            if (uri.getHost() != null && !uri.getHost().isEmpty()) {
                return uri;
            }
        } catch (URISyntaxException ignored) {
        }

        try {
            // Attempt to escape the input as an IPv6 literal.
            // This handles input like ::1.
            URI uri = new URI("moonlight://[" + rawUserInput + "]");
            if (uri.getHost() != null && !uri.getHost().isEmpty()) {
                return uri;
            }
        } catch (URISyntaxException ignored) {
        }

        return null;
    }

    private void doAddPc(String rawUserInput) throws InterruptedException {
        boolean wrongSiteLocal = false;
        boolean invalidInput = false;
        boolean success;
        int portTestResult;

        SpinnerDialog dialog = SpinnerDialog.displayDialog(this, getResources().getString(R.string.title_add_pc),
                getResources().getString(R.string.msg_add_pc), false);

        try {
            ComputerDetails details = new ComputerDetails();

            // Check if we parsed a host address successfully
            URI uri = parseRawUserInputToUri(rawUserInput);
            if (uri != null && uri.getHost() != null && !uri.getHost().isEmpty()) {
                String host = uri.getHost();
                int port = uri.getPort();

                // If a port was not specified, use the default
                if (port == -1) {
                    port = NvHTTP.DEFAULT_HTTP_PORT;
                }

                details.manualAddress = new ComputerDetails.AddressTuple(host, port);
                success = managerBinder.addComputerBlocking(details);
                if (!success) {
                    wrongSiteLocal = isWrongSubnetSiteLocalAddress(host);
                }
            } else {
                // Invalid user input
                success = false;
                invalidInput = true;
            }
        } catch (InterruptedException e) {
            // Propagate the InterruptedException to the caller for proper handling
            dialog.dismiss();
            throw e;
        } catch (IllegalArgumentException e) {
            // This can be thrown from OkHttp if the host fails to canonicalize to a valid name.
            // https://github.com/square/okhttp/blob/okhttp_27/okhttp/src/main/java/com/squareup/okhttp/HttpUrl.java#L705
            e.printStackTrace();
            success = false;
            invalidInput = true;
        }

        // Keep the SpinnerDialog open while testing connectivity
        if (!success && !wrongSiteLocal && !invalidInput) {
            // Run the test before dismissing the spinner because it can take a few seconds.
            portTestResult = MoonBridge.testClientConnectivity(ServerHelper.CONNECTION_TEST_SERVER, 443,
                    MoonBridge.ML_PORT_FLAG_TCP_47984 | MoonBridge.ML_PORT_FLAG_TCP_47989);
        } else {
            // Don't bother with the test if we succeeded or the IP address was bogus
            portTestResult = MoonBridge.ML_TEST_RESULT_INCONCLUSIVE;
        }

        dialog.dismiss();

        if (invalidInput) {
            MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), getResources().getString(R.string.addpc_unknown_host), false);
        } else if (wrongSiteLocal) {
            MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), getResources().getString(R.string.addpc_wrong_sitelocal), false);
        } else if (!success) {
            String dialogText;
            if (portTestResult != MoonBridge.ML_TEST_RESULT_INCONCLUSIVE && portTestResult != 0) {
                dialogText = getResources().getString(R.string.nettest_text_blocked);
            } else {
                dialogText = getResources().getString(R.string.addpc_fail);
            }
            MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), dialogText, false);
        } else {
            AddComputerManually.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddComputerManually.this, getResources().getString(R.string.addpc_success), Toast.LENGTH_LONG).show();

                    if (!isFinishing()) {
                        // Close the activity
                        AddComputerManually.this.finish();
                    }
                }
            });
        }

    }

    private void startAddThread() {
        addThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        String computer = computersToAdd.take();
                        doAddPc(computer);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        addThread.setName("UI - AddComputerManually");
        addThread.start();
    }

    private void joinAddThread() {
        if (addThread != null) {
            addThread.interrupt();

            try {
                addThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();

                // InterruptedException clears the thread's interrupt status. Since we can't
                // handle that here, we will re-interrupt the thread to set the interrupt
                // status back to true.
                Thread.currentThread().interrupt();
            }

            addThread = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        MyDialog.closeDialogs();
        SpinnerDialog.closeDialogs(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (managerBinder != null) {
            joinAddThread();
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiHelper.setLocale(this);

        setContentView(R.layout.activity_add_computer_manually);

        UiHelper.notifyNewRootView(this);

        this.hostText = findViewById(R.id.hostTextView);
        hostText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        hostText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    return handleDoneEvent();
                } else if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                    // This is how the Fire TV dismisses the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hostText.getWindowToken(), 0);
                    return false;
                }

                return false;
            }
        });

        findViewById(R.id.addPcButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDoneEvent();
            }
        });

        // Bind to the ComputerManager service
        bindService(new Intent(AddComputerManually.this,
                ComputerManagerService.class), serviceConnection, Service.BIND_AUTO_CREATE);

        //getVMFromServer();
        getVMFromServerManually();
    }

    // Returns true if the event should be eaten
    private boolean handleDoneEvent() {
        String hostAddress = hostText.getText().toString().trim();

        if (hostAddress.length() == 0) {
            Toast.makeText(AddComputerManually.this, getResources().getString(R.string.addpc_enter_ip), Toast.LENGTH_LONG).show();
            return true;
        }

        computersToAdd.add(hostAddress);
        return false;
    }

    String TAG = "ANT_PLAY";

    private void getVMFromServer() {

//        RetrofitAPI retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
//        String accessToken = SharedPreferenceUtils.getString(AddComputerManually.this, Const.ACCESS_TOKEN);
//        Log.d(TAG, " Access Token : " + accessToken);
//        Call<GetVMResponse> call = retrofitAPI.getVMFromServer("Bearer " + accessToken);
//        call.enqueue(new Callback<GetVMResponse>() {
//            @Override
//            public void onResponse(Call<GetVMResponse> call, Response<GetVMResponse> response) {
//                // progressBar_checkIn.setVisibility(View.GONE);
//                // assert response.body() != null;
//
//                Log.d(TAG, " Response Code : " + response.code());
//                if (response.isSuccessful()) {
//                    String vmIp = response.body().getData().get(0).getVmIp();
//                    Log.d(TAG, " VM IP : " + vmIp);
//                    //Toast.makeText(AddComputerManually.this, "VM IP : "+vmIp, Toast.LENGTH_SHORT).show();
//                    hostText.setText(vmIp);
//                } else {
//                    Toast.makeText(AddComputerManually.this, "API Hit Successful, Status : false", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetVMResponse> call, Throwable t) {
//                Toast.makeText(AddComputerManually.this, "Something went wrong" + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private void getVMFromServerManually() {
        String accessToken = SharedPreferenceUtils.getString(AddComputerManually.this, Const.ACCESS_TOKEN);
        Log.d(TAG, " Access Token : " + accessToken);

        new RestClient(AddComputerManually.this).getRequestWithHeader("add_vm_manually", "getvmip", "", accessToken, new RestClient.ResponseListener() {
            @Override
            public void onResponse(String tag, String response) {

                if (response != null) {
                    Log.d(TAG,"Response : "+response );
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        String vmIp = jsonArray.getJSONObject(0).getString("vmip");
                        Log.d("ANT_PLAY", "VM IP : " + vmIp);
                        hostText.setText(vmIp);
                       /*
                        Intent i = new Intent(LoginActivity.this, PcView.class);
                        startActivity(i);
                        finish();*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new RestClient.ErrorListener() {
            @Override
            public void onError(String tag, String errorMsg, long statusCode) {
                Log.e(TAG, "Reason Of Failure : " + errorMsg);
            }
        });
    }

}
