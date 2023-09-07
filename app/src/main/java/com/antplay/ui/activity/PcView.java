package com.antplay.ui.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import com.antplay.AppView;
import com.antplay.Game;
import com.antplay.LimeLog;
import com.antplay.R;
import com.antplay.api.APIClient;
import com.antplay.api.RetrofitAPI;
import com.antplay.binding.PlatformBinding;
import com.antplay.binding.crypto.AndroidCryptoProvider;
import com.antplay.computers.ComputerManagerService;
import com.antplay.grid.PcGridAdapter;
import com.antplay.grid.assets.DiskAssetLoader;
import com.antplay.models.MessageResponse;
import com.antplay.models.VMTimerReq;
import com.antplay.nvstream.http.ComputerDetails;
import com.antplay.nvstream.http.NvApp;
import com.antplay.nvstream.http.NvHTTP;
import com.antplay.nvstream.http.PairingManager;
import com.antplay.nvstream.http.PairingManager.PairState;
import com.antplay.nvstream.jni.MoonBridge;
import com.antplay.nvstream.wol.WakeOnLanSender;
import com.antplay.preferences.AddComputerManually;
import com.antplay.preferences.GlPreferences;
import com.antplay.preferences.PreferenceConfiguration;
import com.antplay.preferences.StreamSettings;
import com.antplay.ui.fragments.AdapterFragment;
import com.antplay.ui.intrface.AdapterFragmentCallbacks;
import com.antplay.utils.AppUtils;
import com.antplay.utils.Const;
import com.antplay.utils.MyDialog;
import com.antplay.utils.HelpLauncher;
import com.antplay.utils.RestClient;
import com.antplay.utils.ServerHelper;
import com.antplay.utils.SharedPreferenceUtils;
import com.antplay.utils.ShortcutHelper;
import com.antplay.utils.SpinnerDialog;
import com.antplay.utils.UiHelper;


import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PcView extends AppCompatActivity implements AdapterFragmentCallbacks  {
    private RelativeLayout noPcFoundLayout;
    Dialog startVMDialog,shutDownVMDialog;
    boolean startVMStatus,isVMConnected ,isVmDisConnected ,firstTimeVMTimer;
    Timer timerVmShutDown;
    boolean shutdownVMStatus ,startVmTimerStatus = false;

    String startVmValue = "" , connectbtnVisible;

    TextView  tvTimer;
    SpinnerDialog dialog;

    Timer startVmTimer;
    ComputerDetails myComputerDetails;

    TextView  text_PcName,timerText;
    Button btnStartVM,btnShutDownVM;

    boolean isFirstTime =  true;

    ProgressBar progressBar ,loadingBar;
    String time_remaining;
    private PcGridAdapter pcGridAdapter;
    private ShortcutHelper shortcutHelper;
    boolean doubleBackToExitPressedOnce = false;
    private Thread addThread;

    RetrofitAPI retrofitAPI;
    boolean btnStatus =  false;

    boolean btnShutDownStatus = false;

    String startVmCallCount;

    String accessToken , strVMId ,status,vmip;
    boolean isShutDown =  false;
    boolean isStartVm =  false;

    boolean btnStatus_=false;
    TextView searchPC;
    ImageView ivRefresh;
    ComputerDetails saveComputerDeatails;
    private ComputerManagerService.ComputerManagerBinder managerBinder;
    private final LinkedBlockingQueue<String> computersToAdd = new LinkedBlockingQueue<>();
    private boolean freezeUpdates, runningPolling, inForeground, completeOnCreateCalled;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            final ComputerManagerService.ComputerManagerBinder localBinder = ((ComputerManagerService.ComputerManagerBinder) binder);

            // Wait in a separate thread to avoid stalling the UI
            new Thread() {
                @Override
                public void run() {
                    // Wait for the binder to be ready
                    localBinder.waitForReady();
                    managerBinder = localBinder;
                    startComputerUpdates();
                    new AndroidCryptoProvider(PcView.this).getClientCertificate();
                }
            }.start();
        }

        public void onServiceDisconnected(ComponentName className) {
            managerBinder = null;
        }
    };


    private final ServiceConnection serviceConnection2 = new ServiceConnection() {
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

        if(startVmValue.equalsIgnoreCase("")) {
            dialog = SpinnerDialog.displayDialog(this, getResources().getString(R.string.title_add_pc),
                    getResources().getString(R.string.msg_add_pc), false);
        }

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
            if(startVmValue.equalsIgnoreCase("")) {
                dialog.dismiss();
            }
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

        if (invalidInput) {
            MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), getResources().getString(R.string.addpc_unknown_host), false);
        } else if (wrongSiteLocal) {
            MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), getResources().getString(R.string.addpc_wrong_sitelocal), false);
        } else if (!success) {
            String dialogText;
            if (portTestResult != MoonBridge.ML_TEST_RESULT_INCONCLUSIVE && portTestResult != 0) {
                dialogText = getResources().getString(R.string.nettest_text_blocked);
            } else {
               // dialogText = getResources().getString(R.string.addpc_fail);
            }
           // MyDialog.displayDialog(this, getResources().getString(R.string.conn_error_title), dialogText, false);
        } else {
            PcView.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 //   Toast.makeText(PcView.this, getResources().getString(R.string.addpc_success), Toast.LENGTH_LONG).show();

                    completeOnCreate();


                   // if (!isFinishing()) {

                        // Close the activity
                        //AddComputerManually.this.finish();
                  //  }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Only reinitialize views if completeOnCreate() was called
        // before this callback. If it was not, completeOnCreate() will
        // handle initializing views with the config change accounted for.
        // This is not prone to races because both callbacks are invoked
        // in the main thread.
        if (completeOnCreateCalled) {
            // Reinitialize views just in case orientation changed
            initializeViews();
        }
    }

    private final static int PAIR_ID = 2;
    private final static int UNPAIR_ID = 3;
    private final static int WOL_ID = 4;
    private final static int DELETE_ID = 5;
    private final static int RESUME_ID = 6;
    private final static int QUIT_ID = 7;
    private final static int VIEW_DETAILS_ID = 8;
    private final static int FULL_APP_LIST_ID = 9;
    private final static int TEST_NETWORK_ID = 10;
    private final static int GAMESTREAM_EOL_ID = 11;

    private void initializeViews() {
        setContentView(R.layout.activity_pc_view);
        UiHelper.notifyNewRootView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setShouldDockBigOverlays(false);
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        pcGridAdapter.updateLayoutWithPreferences(this, PreferenceConfiguration.readPreferences(this));
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(PcView.this, Const.ACCESS_TOKEN);
        btnStatus = SharedPreferenceUtils.getBoolean(PcView.this, Const.STARTBtnStatus);
        isVMConnected = SharedPreferenceUtils.getBoolean(PcView.this, Const.IS_VM_CONNECTED);
        isVmDisConnected =  SharedPreferenceUtils.getBoolean(PcView.this,Const.IS_VM_DISCONNECTED);
        connectbtnVisible =  SharedPreferenceUtils.getString(PcView.this,Const.connectbtnVisible);
        firstTimeVMTimer =  SharedPreferenceUtils.getBoolean(PcView.this,Const.FIRSTTIMEVMTIMER);



//        Intent serviceIntent = new Intent(this, ClearService.class);
//        ContextCompat.startForegroundService(this, serviceIntent);

        ivRefresh = findViewById(R.id.ivRefresh);
        searchPC = findViewById(R.id.searchPC);
        tvTimer = findViewById(R.id.tvTimer);
        text_PcName = findViewById(R.id.text_PcName);
        progressBar = findViewById(R.id.progressBar);
        loadingBar = findViewById(R.id.loadingBar);
        btnStartVM = findViewById(R.id.btnStartVM);
        btnShutDownVM = findViewById(R.id.btnShutDownVM);
        progressBar = findViewById(R.id.progressBar);
        ImageButton profileButton = findViewById(R.id.profileButton);
        ImageView settingsButton = findViewById(R.id.settingsButton);
        ImageButton addComputerButton = findViewById(R.id.manuallyAddPc);
        ImageButton helpButton = findViewById(R.id.helpButton);
        SwipeRefreshLayout swipeLayout = findViewById(R.id.refreshLayout);
        swipeLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                swipeLayout.setRefreshing(false);
                getVM("");
            }, 1000);
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.teal_700));
        startVMStatus  =  SharedPreferenceUtils.getBoolean(PcView.this,Const.IS_STARTVM);
        shutdownVMStatus  =  SharedPreferenceUtils.getBoolean(PcView.this,Const.IS_SHUT_DOWN);


        btnStartVM.setOnClickListener(view -> {
            String btnText =  btnStartVM.getText().toString();
            if(btnText.equalsIgnoreCase("start")){
                btnStatus = true;
                btnShutDownStatus =  false;
                SharedPreferenceUtils.saveBoolean(PcView.this,Const.STARTBtnStatus , true);
                startVm(strVMId);
            }
            else if(btnText.equalsIgnoreCase("connect")){

                doPair(myComputerDetails);
            }
        });

        btnShutDownVM.setOnClickListener(view -> {
            if (status.equalsIgnoreCase("running")){
                openShutDownVMDialog("shutdown",0L);
                btnShutDownStatus =  true;
                if(vmip!=null)
                    shutDownVM("",strVMId);
                else
                    stopVM("" ,strVMId);
            }
        });

        if(startVMStatus) {
            ivRefresh.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
           // showTimer(time_remaining);
        }
        else
            ivRefresh.setVisibility(View.INVISIBLE);

        ivRefresh.setOnClickListener(view -> getVM(""));


        profileButton.setOnClickListener(v -> startActivity(new Intent(PcView.this, ProfileActivity.class)));

        settingsButton.setOnClickListener(v -> startActivity(new Intent(PcView.this, StreamSettings.class)));
        addComputerButton.setOnClickListener(v -> {
            Intent i = new Intent(PcView.this, AddComputerManually.class);
            startActivity(i);
        });
        helpButton.setOnClickListener(v -> HelpLauncher.launchSetupGuide(PcView.this));

        // Amazon review didn't like the help button because the wiki was not entirely
        // navigable via the Fire TV remote (though the relevant parts were). Let's hide
        // it on Fire TV.
        if (getPackageManager().hasSystemFeature("amazon.hardware.fire_tv")) {
            helpButton.setVisibility(View.GONE);
        }

        getFragmentManager().beginTransaction().replace(R.id.pcFragmentContainer, new AdapterFragment()).commitAllowingStateLoss();

        noPcFoundLayout = findViewById(R.id.no_pc_found_layout);
        if (pcGridAdapter.getCount() == 0) {
            noPcFoundLayout.setVisibility(View.VISIBLE);
        } else {
            noPcFoundLayout.setVisibility(View.INVISIBLE);
        }
       // pcGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assume we're in the foreground when created to avoid a race
        // between binding to CMS and onResume()
        retrofitAPI = APIClient.getRetrofitInstance().create(RetrofitAPI.class);
        accessToken = SharedPreferenceUtils.getString(PcView.this, Const.ACCESS_TOKEN);
        inForeground = true;
        firstTimeVMTimer =  SharedPreferenceUtils.getBoolean(PcView.this,Const.FIRSTTIMEVMTIMER);
        if(!firstTimeVMTimer) {
            if (AppUtils.isOnline(PcView.this))
                getVM("");
            else
                AppUtils.showInternetDialog(PcView.this);
        }
        else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Long time  = timeDifference();
                if(time>1200){
                    SharedPreferenceUtils.saveBoolean(PcView.this,Const.FIRSTTIMEVMTIMER ,false);
                    getVM("");
                }
                else
                    openShutDownVMDialog("vmtimer" , 1200-time);
            }
        }


        tvTimer = findViewById(R.id.tvTimer);

        isVmDisConnected = SharedPreferenceUtils.getBoolean(PcView.this, Const.IS_VM_DISCONNECTED);
        if(isVmDisConnected) {
            timerVmShutDown = new Timer();
            timerVmShutDown.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.i("test_background" , "testttt");
                    if(!isFirstTime) {
                        if (AppUtils.isOnline(PcView.this))
                            getVMForShutDown();
                        else
                            AppUtils.showInternetDialog(PcView.this);
                    }
                    else{
                        isFirstTime =  false;
                    }
                }
            }, 0, 300* 1000);
        }


        // Create a GLSurfaceView to fetch GLRenderer unless we have
        // a cached result already.
        final GlPreferences glPrefs = GlPreferences.readPreferences(this);
        if (!glPrefs.savedFingerprint.equals(Build.FINGERPRINT) || glPrefs.glRenderer.isEmpty()) {
            GLSurfaceView surfaceView = new GLSurfaceView(this);
            surfaceView.setRenderer(new GLSurfaceView.Renderer() {
                @Override
                public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                    // Save the GLRenderer string so we don't need to do this next time
                    glPrefs.glRenderer = gl10.glGetString(GL10.GL_RENDERER);
                    glPrefs.savedFingerprint = Build.FINGERPRINT;
                    glPrefs.writePreferences();

                    LimeLog.info("Fetched GL Renderer: " + glPrefs.glRenderer);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            completeOnCreate();
                        }
                    });
                }

                @Override
                public void onSurfaceChanged(GL10 gl10, int i, int i1) {
                }

                @Override
                public void onDrawFrame(GL10 gl10) {
                }
            });
            setContentView(surfaceView);

        } else {
            LimeLog.info("Cached GL Renderer: " + glPrefs.glRenderer);
            completeOnCreate();
        }
    }

    private void completeOnCreate() {
        completeOnCreateCalled = true;
        shortcutHelper = new ShortcutHelper(this);
        UiHelper.setLocale(this);



        // Bind to the computer manager service
        bindService(new Intent(PcView.this, ComputerManagerService.class), serviceConnection, Service.BIND_AUTO_CREATE);
        pcGridAdapter = new PcGridAdapter(this, PreferenceConfiguration.readPreferences(this));
        initializeViews();

    }

    private void startComputerUpdates() {
        // Only allow polling to start if we're bound to CMS, polling is not already running,
        // and our activity is in the foreground.

        try{
            ComputerDetails computerDetails =  SharedPreferenceUtils.getObject(PcView.this,Const.SAVE_DETAILS);
            if(computerDetails!=null)
                removeComputer(computerDetails);
        }
        catch (Exception e){

        }

        if (managerBinder != null && !runningPolling && inForeground) {
            freezeUpdates = false;
            managerBinder.startPolling(details -> {
                if (!freezeUpdates) {
                    PcView.this.runOnUiThread(() -> {

                        if(details.manualAddress!=null){
                            Log.i("test_id" , details.manualAddress +" " + details.state);
                            saveComputerDeatails = details;
                            SharedPreferenceUtils.saveObject(PcView.this, Const.SAVE_DETAILS, saveComputerDeatails);
                            progressBar.setVisibility(View.GONE);
                            updateComputer(details ,time_remaining);
                        }
                    });
                }
            });
            runningPolling = true;
        }
    }

    private void stopComputerUpdates(boolean wait) {
        if (managerBinder != null) {
            if (!runningPolling) {
                return;
            }

            freezeUpdates = true;
            managerBinder.stopPolling();
            if (wait) {
                managerBinder.waitForPollingStopped();
            }

            runningPolling = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
try {
    if (managerBinder != null) {
        unbindService(serviceConnection);
        joinAddThread();
        unbindService(serviceConnection2);

    }
}
catch (Exception e){

}
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display a decoder crash notification if we've returned after a crash
        UiHelper.showDecoderCrashDialog(this);

        inForeground = true;
        startComputerUpdates();

    }

    @Override
    protected void onPause() {
        super.onPause();

        inForeground = false;
        stopComputerUpdates(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        MyDialog.closeDialogs();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        stopComputerUpdates(false);

        // Call superclass
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        ComputerObject computer = (ComputerObject) pcGridAdapter.getItem(info.position);

        // Add a header with PC status details
        menu.clearHeader();
        String headerTitle = computer.details.name + " - ";
        switch (computer.details.state) {
            case ONLINE:
                headerTitle += getResources().getString(R.string.pcview_menu_header_online);
                break;
            case OFFLINE:
                menu.setHeaderIcon(R.drawable.ic_pc_offline);
                headerTitle += getResources().getString(R.string.pcview_menu_header_offline);
                break;
            case UNKNOWN:
                headerTitle += getResources().getString(R.string.pcview_menu_header_unknown);
                break;
        }

        menu.setHeaderTitle(headerTitle);

        // Inflate the context menu
        if (computer.details.state == ComputerDetails.State.OFFLINE || computer.details.state == ComputerDetails.State.UNKNOWN) {
            menu.add(Menu.NONE, WOL_ID, 1, getResources().getString(R.string.pcview_menu_send_wol));
            menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 2, getResources().getString(R.string.pcview_menu_eol));
        } else if (computer.details.pairState != PairState.PAIRED) {
            menu.add(Menu.NONE, PAIR_ID, 1, getResources().getString(R.string.pcview_menu_pair_pc));
            if (computer.details.nvidiaServer) {
                menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 2, getResources().getString(R.string.pcview_menu_eol));
            }
        } else {
            if (computer.details.runningGameId != 0) {
                menu.add(Menu.NONE, RESUME_ID, 1, getResources().getString(R.string.applist_menu_resume));
                menu.add(Menu.NONE, QUIT_ID, 2, getResources().getString(R.string.applist_menu_quit));
            }

            if (computer.details.nvidiaServer) {
                menu.add(Menu.NONE, GAMESTREAM_EOL_ID, 3, getResources().getString(R.string.pcview_menu_eol));
            }

            menu.add(Menu.NONE, FULL_APP_LIST_ID, 4, getResources().getString(R.string.pcview_menu_app_list));
        }

        menu.add(Menu.NONE, TEST_NETWORK_ID, 5, getResources().getString(R.string.pcview_menu_test_network));
        menu.add(Menu.NONE, DELETE_ID, 6, getResources().getString(R.string.pcview_menu_delete_pc));
        menu.add(Menu.NONE, VIEW_DETAILS_ID, 7, getResources().getString(R.string.pcview_menu_details));
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        // For some reason, this gets called again _after_ onPause() is called on this activity.
        // startComputerUpdates() manages this and won't actual start polling until the activity
        // returns to the foreground.
        startComputerUpdates();
    }

    private void doPair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || computer.activeAddress == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.pair_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

    //    Toast.makeText(PcView.this, getResources().getString(R.string.pairing), Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                boolean success = false;
                try {
                    // Stop updates and wait while pairing
                    stopComputerUpdates(true);

                    Log.i("testt1" , "test");

                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer), computer.httpsPort, managerBinder.getUniqueId(), computer.serverCert, PlatformBinding.getCryptoProvider(PcView.this));
                    if (httpConn.getPairState() == PairState.PAIRED) {
                        // Don't display any toast, but open the app list
                        message = null;
                        success = true;
                    } else {
                        final String pinStr = PairingManager.generatePinString();
                        Log.i("testt2" , "test");

                        // Spin the dialog off in a thread because it blocks

//                        MyDialog.displayDialog(PcView.this, getResources().getString(R.string.pair_pairing_title), getResources().getString(R.string.pair_pairing_msg) + " " + pinStr + "\n\n" + getResources().getString(R.string.pair_pairing_help), false);


                        //sendAndVerifySecurityPin(pinStr);
                        sendAndVerifySecurityPinManually(pinStr);
                        PairingManager pm = httpConn.getPairingManager();
                        Log.i("testt3" , "test");

                        PairState pairState = pm.pair(httpConn.getServerInfo(true), pinStr);
                        if (pairState == PairState.PIN_WRONG) {
                            message = getResources().getString(R.string.pair_incorrect_pin);
                        } else if (pairState == PairState.FAILED) {
                            if (computer.runningGameId != 0) {
                                message = getResources().getString(R.string.pair_pc_ingame);
                            } else {
                                message = getResources().getString(R.string.pair_fail);
                            }
                        } else if (pairState == PairState.ALREADY_IN_PROGRESS) {
                            message = getResources().getString(R.string.pair_already_in_progress);
                        } else if (pairState == PairState.PAIRED) {
                            // Just navigate to the app view without displaying a toast
                            message = null;
                            success = true;
                            try {
                                managerBinder.getComputer(computer.uuid).serverCert = pm.getPairedCert();
                                managerBinder.invalidateStateForComputer(computer.uuid);
                            }
                            catch (Exception e){
                                doPair(computer);
                            }
                        } else {
                            // Should be no other values
                            message = null;
                        }
                    }
                } catch (UnknownHostException e) {
                    message = getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    message = e.getMessage();
                }

              //  MyDialog.closeDialogs();

                final String toastMessage = message;
                final boolean toastSuccess = success;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toastMessage != null) {
                            Toast.makeText(PcView.this, toastMessage, Toast.LENGTH_LONG).show();
                        }
                        if (toastSuccess) {
                            doAppList(computer, true, false);
                        } else {
                            startComputerUpdates();
                        }
                    }
                });
            }
        }).start();
    }

    String TAG = "ANT_PLAY";


//    private void sendAndVerifySecurityPinManually(String pinStr) {
//        UpdatePinRequestModal updatePinRequestModal  =  new UpdatePinRequestModal(pinStr);
//        Call<UpdatePinResponseModal> call = retrofitAPI.updatePin("Bearer " + accessToken , updatePinRequestModal);
//        call.enqueue(new Callback<>() {
//            @Override
//            public void onResponse(Call<UpdatePinResponseModal> call, Response<UpdatePinResponseModal> response) {
//                if(response.code()==Const.SUCCESS_CODE_200) {
//                    String status = response.body().getPinData().getStatus();
//                }
//                else if(response.code()==Const.ERROR_CODE_404 || response.code()==Const.ERROR_CODE_400 || response.code()==Const.ERROR_CODE_500) {
//                    try {
//                        JSONObject jObj = new JSONObject(response.errorBody().string());
//                        String value = jObj.getString("message");
//                        Toast.makeText(PcView.this, "" + value, Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            @Override
//            public void onFailure(Call<UpdatePinResponseModal> call, Throwable t) {
//                AppUtils.showToast(Const.something_went_wrong, PcView.this);
//            }
//        });
//
//    }


    private void sendAndVerifySecurityPinManually(String pinStr) {
        HashMap<String, String> pinMap = new HashMap<>();
        pinMap.put("pin", pinStr);

        Log.d(TAG, " Access Token : " + accessToken);
        new RestClient(PcView.this).postRequestWithHeader("update_pin", "vmauth", pinMap, accessToken, "", new RestClient.ResponseListener() {
            @Override
            public void onResponse(String tag, String response) {
                if (response != null) {
                    Log.d(TAG, "Response : " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getJSONObject("data").getString("status");

                        Log.d("ANT_PLAY", "Status : " + status);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new RestClient.ErrorListener() {
            @Override
            public void onError(String tag, String errorMsg, long statusCode) {
                Log.d("ANT_PLAY", "Message : " + errorMsg);
            }
        });
    }


    private void doWakeOnLan(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.ONLINE) {
            Toast.makeText(PcView.this, getResources().getString(R.string.wol_pc_online), Toast.LENGTH_SHORT).show();
            return;
        }

        if (computer.macAddress == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.wol_no_mac), Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                try {
                    WakeOnLanSender.sendWolPacket(computer);
                    message = getResources().getString(R.string.wol_waking_msg);
                } catch (IOException e) {
                    message = getResources().getString(R.string.wol_fail);
                }

                final String toastMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PcView.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void doUnpair(final ComputerDetails computer) {
        if (computer.state == ComputerDetails.State.OFFLINE || computer.activeAddress == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.error_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(PcView.this, getResources().getString(R.string.unpairing), Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvHTTP httpConn;
                String message;
                try {
                    httpConn = new NvHTTP(ServerHelper.getCurrentAddressFromComputer(computer), computer.httpsPort, managerBinder.getUniqueId(), computer.serverCert, PlatformBinding.getCryptoProvider(PcView.this));
                    if (httpConn.getPairState() == PairingManager.PairState.PAIRED) {
                        httpConn.unpair();
                        if (httpConn.getPairState() == PairingManager.PairState.NOT_PAIRED) {
                            message = getResources().getString(R.string.unpair_success);
                        } else {
                            message = getResources().getString(R.string.unpair_fail);
                        }
                    } else {
                        message = getResources().getString(R.string.unpair_error);
                    }
                } catch (UnknownHostException e) {
                    message = getResources().getString(R.string.error_unknown_host);
                } catch (FileNotFoundException e) {
                    message = getResources().getString(R.string.error_404);
                } catch (XmlPullParserException | IOException e) {
                    message = e.getMessage();
                    e.printStackTrace();
                }

                final String toastMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PcView.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    private void doAppList(ComputerDetails computer, boolean newlyPaired, boolean showHiddenGames) {
        if (computer.state == ComputerDetails.State.OFFLINE) {
            Toast.makeText(PcView.this, getResources().getString(R.string.error_pc_offline), Toast.LENGTH_SHORT).show();
            return;
        }
        if (managerBinder == null) {
            Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(this, AppView.class);
        i.putExtra(AppView.NAME_EXTRA, computer.name);
        i.putExtra(AppView.UUID_EXTRA, computer.uuid);
        i.putExtra(AppView.NEW_PAIR_EXTRA, newlyPaired);
        i.putExtra(AppView.SHOW_HIDDEN_APPS_EXTRA, showHiddenGames);
        startActivity(i);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final ComputerObject computer = (ComputerObject) pcGridAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case PAIR_ID:
                doPair(computer.details);
                return true;

            case UNPAIR_ID:
                doUnpair(computer.details);
                return true;

            case WOL_ID:
                doWakeOnLan(computer.details);
                return true;

            case DELETE_ID:
                if (ActivityManager.isUserAMonkey()) {
                    LimeLog.info("Ignoring delete PC request from monkey");
                    return true;
                }
                UiHelper.displayDeletePcConfirmationDialog(this, computer.details, new Runnable() {
                    @Override
                    public void run() {
                        if (managerBinder == null) {
                            Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                            return;
                        }
                        removeComputer(computer.details);
                    }
                }, null);
                return true;

            case FULL_APP_LIST_ID:
                doAppList(computer.details, false, true);
                return true;

            case RESUME_ID:
                if (managerBinder == null) {
                    Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                    return true;
                }

                ServerHelper.doStart(this, new NvApp("app", computer.details.runningGameId, false), computer.details, managerBinder);
                return true;

            case QUIT_ID:
                if (managerBinder == null) {
                    Toast.makeText(PcView.this, getResources().getString(R.string.error_manager_not_running), Toast.LENGTH_LONG).show();
                    return true;
                }

                // Display a confirmation dialog first
                UiHelper.displayQuitConfirmationDialog(this, new Runnable() {
                    @Override
                    public void run() {
                        ServerHelper.doQuit(PcView.this, computer.details, new NvApp("app", 0, false), managerBinder, null);
                    }
                }, null);
                return true;

            case VIEW_DETAILS_ID:
                MyDialog.displayDialog(PcView.this, getResources().getString(R.string.title_details), computer.details.toString(), false);
                return true;

            case TEST_NETWORK_ID:
                ServerHelper.doNetworkTest(PcView.this);
                return true;

            case GAMESTREAM_EOL_ID:
                HelpLauncher.launchGameStreamEolFaq(PcView.this);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void removeComputer(ComputerDetails details) {
        managerBinder.removeComputer(details);

        new DiskAssetLoader(this).deleteAssetsForComputer(details.uuid);

        // Delete hidden games preference value
        getSharedPreferences(AppView.HIDDEN_APPS_PREF_FILENAME, MODE_PRIVATE).edit().remove(details.uuid).apply();

        for (int i = 0; i < pcGridAdapter.getCount(); i++) {
            ComputerObject computer = (ComputerObject) pcGridAdapter.getItem(i);

            if (details.equals(computer.details)) {
                // Disable or delete shortcuts referencing this PC
                shortcutHelper.disableComputerShortcut(details, getResources().getString(R.string.scut_deleted_pc));

                pcGridAdapter.removeComputer(computer);
                pcGridAdapter.notifyDataSetChanged();

                if (pcGridAdapter.getCount() == 0) {
                    // Show the "Discovery in progress" view
                    noPcFoundLayout.setVisibility(View.VISIBLE);
                }

                break;
            }
        }
    }

    private void updateComputer(ComputerDetails details , String value) {
//        if (details.state == ComputerDetails.State.OFFLINE || details.activeAddress == null) {
//            Toast.makeText(PcView.this, getResources().getString(R.string.pair_pc_offline), Toast.LENGTH_SHORT).show();
//            return;
//        }


        showTimer(value);
        myComputerDetails  =   details;
        text_PcName.setText("" + details.name);
        if(status.equalsIgnoreCase("running")){
            SharedPreferenceUtils.saveBoolean(PcView.this,Const.IS_VM_DISCONNECTED , true);
            Log.i("testttt_btnstatus" , ""+ btnStatus);
            if(!btnShutDownStatus)
                btnStartVM.setVisibility(View.VISIBLE);
           else
                btnStartVM.setVisibility(View.INVISIBLE);

           btnShutDownVM.setVisibility(View.VISIBLE);
//            else
//                btnStartVM.setVisibility(View.VISIBLE);
//                btnShutDownVM.setVisibility(View.VISIBLE);



            text_PcName.setVisibility(View.VISIBLE);
            btnStartVM.setText("Connect");
            btnShutDownVM.setText("Shutdown");
            progressBar.setVisibility(View.GONE);
            ivRefresh.setVisibility(View.INVISIBLE);



        }
        else if(status.equalsIgnoreCase("stopped")){
            progressBar.setVisibility(View.GONE);
            btnStartVM.setText("Start");
            btnShutDownVM.setVisibility(View.INVISIBLE);

        }



        ComputerObject existingEntry = null;

        for (int i = 0; i < pcGridAdapter.getCount(); i++) {
            ComputerObject computer = (ComputerObject) pcGridAdapter.getItem(i);

            // Check if this is the same computer
            if (details.uuid.equals(computer.details.uuid)) {
                existingEntry = computer;
                break;
            }
        }

        // Add a launcher shortcut for this PC
        if (details.pairState == PairState.PAIRED) {
            shortcutHelper.createAppViewShortcutForOnlineHost(details);
        }

        if (existingEntry != null) {
            // Replace the information in the existing entry
           // existingEntry.details = details;
        } else {
            // Add a new entry
            pcGridAdapter.addComputer(new ComputerObject(details));

            // Remove the "Discovery in progress" view
            noPcFoundLayout.setVisibility(View.INVISIBLE);
            if(startVmValue.equalsIgnoreCase("")) {
                dialog.dismiss();
            }
        }


        // Notify the view that the data has changed
        pcGridAdapter.notifyDataSetChanged();
    }

    @Override
    public int getAdapterFragmentLayoutId() {
        return R.layout.pc_grid_view;
    }

    @Override
    public void receiveAbsListView(AbsListView listView) {
        listView.setAdapter(pcGridAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                ComputerObject computer = (ComputerObject) pcGridAdapter.getItem(pos);
                if (computer.details.state == ComputerDetails.State.UNKNOWN || computer.details.state == ComputerDetails.State.OFFLINE) {
                    // Open the context menu if a PC is offline or refreshing
                    openContextMenu(arg1);
                } else if (computer.details.pairState != PairState.PAIRED) {
                    // Pair an unpaired machine by default
                    doPair(computer.details);
                } else {
                    doAppList(computer.details, false, false);
                }
            }
        });
        UiHelper.applyStatusBarPadding(listView);
        registerForContextMenu(listView);
    }

    public static class ComputerObject {
        public ComputerDetails details;

        public ComputerObject(ComputerDetails details) {
            if (details == null) {
                throw new IllegalArgumentException("details must not be null");
            }
            this.details = details;
        }

        @Override
        public String toString() {
            return details.name;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
    private void openDialog(boolean purchaseVmFLag , String msg) {
        Dialog dialog = new Dialog(PcView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleText =  dialog.findViewById(R.id.titleText);
        TextView msgText =  dialog.findViewById(R.id.msgText);
        Button txtNo = dialog.findViewById(R.id.txtNo);
        Button txtYes = dialog.findViewById(R.id.txtYes);
        titleText.setText(getResources().getString(R.string.no_vm_title));
        txtYes.setText("purchase");

        msgText.setText(msg);
        if(!purchaseVmFLag) {
            txtYes.setVisibility(View.VISIBLE);
            txtNo.setText(getResources().getString(R.string.applist_menu_cancel));
        }
        else {
            txtYes.setVisibility(View.GONE);
            txtNo.setText("Ok");


        }

        txtYes.setOnClickListener(view -> {
                dialog.dismiss();
                AppUtils.navigateScreenWithoutFinish(PcView.this, SubscriptionPlanActivity.class);
            });
            txtNo.setOnClickListener(view -> {
                dialog.dismiss();
            });
            dialog.show();
        }

    private boolean handleDoneEvent(String vmIp ,String startVM) {
        if (vmIp.length() == 0) {
            Toast.makeText(PcView.this, getResources().getString(R.string.addpc_enter_ip), Toast.LENGTH_LONG).show();
            return true;
        }
        startVmValue =  startVM;
        computersToAdd.add(vmIp);
        return false;
    }


    private void getVMIP(String timeRemaing ,String startVm) {
        loadingBar.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = retrofitAPI.getVMIP("Bearer " + accessToken);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingBar.setVisibility(View.GONE);
                if (response.code()==200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String messageValue = jsonObject.getString("message");
                        if (messageValue.equalsIgnoreCase("success")) {
                            startVMStatus =  false;
                            shutdownVMStatus = false;
                            SharedPreferenceUtils.saveBoolean(PcView.this,Const.IS_STARTVM , false);
                            SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_SHUT_DOWN, false);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String vmIp = jsonArray.getJSONObject(0).getString("vmip");
                            startVmTimerStatus =  false;
//                            if(btnStatus)
//                                SharedPreferenceUtils.saveBoolean(PcView.this,Const.IS_VM_DISCONNECTED , true);

                            progressBar.setVisibility(View.GONE);
                            showTimer(time_remaining);
                            handleDoneEvent(vmIp ,startVm);
                            bindService(new Intent(PcView.this, ComputerManagerService.class), serviceConnection2, Service.BIND_AUTO_CREATE);
                            pcGridAdapter = new PcGridAdapter(PcView.this, PreferenceConfiguration.readPreferences(PcView.this));
                            initializeViews();
                        }
                    } catch (Exception e) {
                    }
                }
                else if(response.code()==400||response.code()==404||response.code()==500 ||response.code()==401) {
                    progressBar.setVisibility(View.GONE);
                    showTimer(timeRemaing);
                    if (status.equalsIgnoreCase("running")) {
                            if (Integer.parseInt(startVmCallCount) <= 1) {
                                firstTimeVMTimer =  true;
                                SharedPreferenceUtils.saveBoolean(PcView.this,Const.FIRSTTIMEVMTIMER ,true);
                                saveTime();
                                openShutDownVMDialog("vmtimer" , 1200L);


//                                openDialog(true, getResources().getString(R.string.searching_pc_second));
//                                searchPC.setText(getResources().getString(R.string.searching_pc_second));
                            } else {
                                if(!startVmTimerStatus) {
                                    openDialog(true, getResources().getString(R.string.startVMMsg));
                                    searchPC.setText(getResources().getString(R.string.startVMMsg));
                                }
                                else
                                    callTimer(timerText,60 ,"start");

                            }
                        } else
                            openDialog(true, getResources().getString(R.string.shutdown_vmmsg));
                    }
                }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, PcView.this);
            }
        });
    }

    private void showTimer(String timeRemaining) {
        long value = Long.parseLong(timeRemaining);
        long hours = value / 3600;
        long minutes =value % 3600 / 60;
        long   sec = value % 60;

        String  timeString = String.format("%02d:%02d:%02d", hours, minutes, sec);
        tvTimer.setText(timeString + " hrs.");
    }

    private void startVm(String strVMId) {
            loadingBar.setVisibility(View.VISIBLE);
        VMTimerReq  vmTimerReq  =  new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.startVm("Bearer " + accessToken , vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    loadingBar.setVisibility(View.GONE);
                if(response.code()==Const.SUCCESS_CODE_200) {
                    if (response.body().getSuccess().equalsIgnoreCase("true")) {
                        startVMStatus =  true;
                        shutdownVMStatus =  false;
                        SharedPreferenceUtils.saveBoolean(PcView.this,Const.IS_STARTVM , true);
                        SharedPreferenceUtils.saveBoolean(PcView.this,Const.IS_SHUT_DOWN , false);
                        if(startVmCallCount.equalsIgnoreCase("0")){
                            ivRefresh.setVisibility(View.VISIBLE);
                            openDialog(true, getResources().getString(R.string.searching_pc_second));
                            searchPC.setText(getResources().getString(R.string.searching_pc_second));
                        }
                        else if(btnStatus) {
                            openShutDownVMDialog("start",0L);
                        }
                    }
                }
                else if(response.code()==Const.ERROR_CODE_404 || response.code()==Const.ERROR_CODE_400 || response.code()==Const.ERROR_CODE_500) {
                    try {
                        JSONObject jObj = new JSONObject(response.errorBody().string());
                        String value = jObj.getString("message");
                        Toast.makeText(PcView.this, "" + value, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                loadingBar.setVisibility(View.GONE);
                AppUtils.showToast(Const.something_went_wrong, PcView.this);
            }
        });
    }

    private void getVM(String startVm) {
        try {
            Call<ResponseBody> call = retrofitAPI.getVMFromServer("Bearer " + accessToken);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == Const.SUCCESS_CODE_200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            strVMId = jsonArray.getJSONObject(0).getString("vmid");
                            startVmCallCount = jsonArray.getJSONObject(0).getString("start_vm_call_count");
                            status = jsonArray.getJSONObject(0).getString("status");
                            vmip = jsonArray.getJSONObject(0).getString("vmip");
                            time_remaining = jsonArray.getJSONObject(0).getString("time_remaining");
                            SharedPreferenceUtils.saveString(PcView.this, Const.VMID, strVMId);
                            if (startVmCallCount.equalsIgnoreCase("0"))
                                startVm(strVMId);
                            else if (Integer.parseInt(startVmCallCount) >= 1) {
                                if (status.equalsIgnoreCase("running")) {
                                        getVMIP(time_remaining,startVm);
                                }
                                else {
                                    ivRefresh.setVisibility(View.GONE);
                                    btnStartVM.setVisibility(View.VISIBLE);
                                    btnShutDownVM.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    btnStartVM.setText("Start");
                                    showTimer(time_remaining);
                                }

                            }
                        } catch (Exception e) {
                        }
                    } else if (response.code() == 404 || response.code() == 500 || response.code() == 400 || response.code() == 401) {
                        openDialog(false, getResources().getString(R.string.searching_pc_first));
                       try {
                           tvTimer.setText("00:00:00 hrs.");
                           searchPC = findViewById(R.id.searchPC);
                           searchPC.setText(getResources().getString(R.string.searching_pc_first));
                           progressBar.setVisibility(View.GONE);

                       }
                       catch (Exception e){

                       }

                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtils.showToast(Const.something_went_wrong, PcView.this);
                }
            });
        }
        catch (Exception e){
        }
    }


   

    private void shutDownVM(String status ,String strVMId) {
        VMTimerReq  vmTimerReq  =  new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.shutDownVm("Bearer " + accessToken , vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code()==200) {
                    btnStartVM.setVisibility(View.INVISIBLE);

                    if(status.equalsIgnoreCase("background")){
                        if (timerVmShutDown != null) {
                            timerVmShutDown.cancel();
                            timerVmShutDown.purge();
                            timerVmShutDown = null;
                        }
                        shutdownVMStatus = true;
                        isVmDisConnected =  false;
                        connectbtnVisible = "stopped";
                        btnStatus = false;
                        btnStartVM.setVisibility(View.VISIBLE);
                        btnShutDownVM.setVisibility(View.GONE);
                        btnStartVM.setText("Start");
                        SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_SHUT_DOWN, true);
                        SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_VM_DISCONNECTED, false);
                        SharedPreferenceUtils.saveString(PcView.this,Const.connectbtnVisible , "stopped");
                        SharedPreferenceUtils.saveBoolean(PcView.this,Const.STARTBtnStatus , false);

                       // getVM("");
                    }

                }
                else if(response.code()==400||response.code()==404){
                    try {
                        shutDownVMDialog.dismiss();
                    }
                    catch (Exception e){

                    }
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, PcView.this);
            }
        });
    }
    private void stopVM(String status ,String strVMId) {
        VMTimerReq vmTimerReq = new VMTimerReq(strVMId);
        Call<MessageResponse> call = retrofitAPI.stopVM("Bearer " + accessToken , vmTimerReq);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.code()==200) {
                    btnStartVM.setVisibility(View.INVISIBLE);
                    if(status.equalsIgnoreCase("background")){
                        if (timerVmShutDown != null) {
                            timerVmShutDown.cancel();
                            timerVmShutDown.purge();
                            timerVmShutDown = null;
                        }
                        shutdownVMStatus = true;
                        isVmDisConnected =  false;
                        connectbtnVisible = "stopped";
                        btnStatus = false;

                        SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_SHUT_DOWN, true);
                        SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_VM_DISCONNECTED, false);
                        SharedPreferenceUtils.saveString(PcView.this,Const.connectbtnVisible , "stopped");
                        SharedPreferenceUtils.saveBoolean(PcView.this,Const.STARTBtnStatus , false);

                        btnStartVM.setVisibility(View.VISIBLE);
                        btnShutDownVM.setVisibility(View.GONE);
                        btnStartVM.setText("Start");
                       // getVM("");
                    }

                }
                else if(response.code()==400||response.code()==404){
                    try {
                        shutDownVMDialog.dismiss();
                    }
                    catch (Exception e){
                    }
                }
            }
            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                AppUtils.showToast(Const.something_went_wrong, PcView.this);
            }
        });
    }

    public void getVMForShutDown() {
        try {
            Call<ResponseBody> call = retrofitAPI.getVMFromServer("Bearer " + accessToken);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            String status = jsonArray.getJSONObject(0).getString("status");
                            String vmip = jsonArray.getJSONObject(0).getString("vmip");
                            String strVMId = jsonArray.getJSONObject(0).getString("vmid");

                            isVmDisConnected = SharedPreferenceUtils.getBoolean(PcView.this, Const.IS_VM_DISCONNECTED);
                            if (isVmDisConnected) {
                                if (status.equalsIgnoreCase("running")) {
                                    if (vmip != null)
                                        shutDownVM("background" ,strVMId);
                                    else
                                        stopVM("background" ,strVMId);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppUtils.showToast(Const.something_went_wrong, PcView.this);
                }
            });
        }
        catch (Exception e){

        }
    }

    private void openShutDownVMDialog(String status ,Long time) {
        shutDownVMDialog = new Dialog(PcView.this);
        shutDownVMDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shutDownVMDialog.setContentView(R.layout.shutingdownvm_dialog_layout);
        shutDownVMDialog.setCancelable(false);
        shutDownVMDialog.setCanceledOnTouchOutside(false);
        shutDownVMDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        shutDownVMDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleText =  shutDownVMDialog.findViewById(R.id.titleText);
        timerText =  shutDownVMDialog.findViewById(R.id.timerText);
        TextView msgText =  shutDownVMDialog.findViewById(R.id.msgText);
        titleText.setText(getResources().getString(R.string.no_vm_title));
        if(status.equalsIgnoreCase("start")) {
            callTimer(timerText,60,status);
            msgText.setText(getResources().getString(R.string.startVMMsg));
        }
        else if(status.equalsIgnoreCase("vmtimer")){
            callTimer(timerText,time,status);
            msgText.setText(getResources().getString(R.string.searching_pc_second));
        }
        else{
            callTimer(timerText,25,status);
            msgText.setText(getResources().getString(R.string.shutdownVMMsg));
        }
        shutDownVMDialog.show();

    }

    private void callTimer(TextView timerText, long sec,String status) {
        new CountDownTimer(sec*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long value = millisUntilFinished / 1000;
                long minutes = value / 60;
                long  sec_ = value % 60;
                String  timeString = String.format("%02d:%02d",  minutes, sec_);
                timerText.setText(timeString);

             if(status.equalsIgnoreCase("start")) {
                 if (sec_== 6) {
                     startVmTimerStatus = true;
                     getVM("startVm");
                 }
             }
            }

            public void onFinish() {
                shutDownVMDialog.dismiss();
                if(status.equalsIgnoreCase("shutdown")) {
                    if (timerVmShutDown != null) {
                        timerVmShutDown.cancel();
                        timerVmShutDown.purge();
                        timerVmShutDown = null;
                    }
                    shutdownVMStatus = true;
                    isVmDisConnected =  false;
                    connectbtnVisible = "stopped";
                    btnStatus = false;
                    SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_SHUT_DOWN, true);
                    SharedPreferenceUtils.saveBoolean(PcView.this, Const.IS_VM_DISCONNECTED, false);
                    SharedPreferenceUtils.saveString(PcView.this,Const.connectbtnVisible , "stopped");
                    SharedPreferenceUtils.saveBoolean(PcView.this,Const.STARTBtnStatus , false);
                    getVM("");
                }
                else if(status.equalsIgnoreCase("vmtimer")){
                    SharedPreferenceUtils.saveBoolean(PcView.this,Const.FIRSTTIMEVMTIMER ,false);
                    getVM("");
                }
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long timeDifference() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm:ss 'h'");
        String startTimeString  =    SharedPreferenceUtils.getString(PcView.this,Const.startTime);
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss 'h'");
        String stopTimeString = sdf.format(new Date());
        LocalTime startTime = LocalTime.parse(startTimeString, timeFormatter);
        LocalTime stopTime = LocalTime.parse(stopTimeString, timeFormatter);

        if (stopTime.isBefore(startTime)) {
            System.out.println("Stop time must not be before start time");
        } else {
            Duration difference = Duration.between(startTime, stopTime);

            long hours = difference.toHours();
            difference = difference.minusHours(hours);
            long minutes = difference.toMinutes();
            difference = difference.minusMinutes(minutes);
            long seconds = difference.getSeconds();

            long newTime  =   hours*3600+minutes*60+seconds;


            return newTime;
        }
        return null;
    }

    public void saveTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss 'h'");
        String s = sdf.format(new Date());
        SharedPreferenceUtils.saveString(PcView.this,Const.startTime,s);


    }

}

