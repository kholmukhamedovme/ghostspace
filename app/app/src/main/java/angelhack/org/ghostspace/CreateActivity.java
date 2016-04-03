package angelhack.org.ghostspace;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends ActionBarActivity implements BeaconConsumer, RangeNotifier, View.OnClickListener {

    private String TAG = "MY_TAG";
    private Handler handler;
    private BackgroundPowerSaver backgroundPowerSaver;


    private LinearLayout ll;
    private TextView tvA;
    private TextView tvB;
    private TextView tvC;
    private EditText et;

    private TextView staticTv;
    private Button createBtn;

    int N;
    int T;
    private int[] counters;
    private double[] summs;
    private int allDone;
    private double[] avs;

    private double[] finalNum;
    private String[] finalString;

    private double f;

    private boolean create;
    //private ScanCallback myCallbackMethod;

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_layout);
/*
        myCallbackMethod = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }
        };

        BluetoothLeScanner scanner = ((BluetoothManager)getSystemService(BLUETOOTH_SERVICE)).getAdapter().getBluetoothLeScanner();
        ScanSettings settings = (new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        scanner.startScan(filters, settings, myCallbackMethod);
*/
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));


        staticTv = (TextView) findViewById(R.id.textView);
        tvA = (TextView) findViewById(R.id.tvA);
        tvB = (TextView) findViewById(R.id.tvB);
        tvC = (TextView) findViewById(R.id.tvC);

        et = (EditText) findViewById(R.id.etMessage);

        createBtn = (Button) findViewById(R.id.createButton);
        createBtn.setOnClickListener(this);

        handler = new Handler(Looper.getMainLooper());
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        N = 3;
        f = Math.pow(10, -6);

        counters = new int[N];
        summs = new double[N];
        avs = new double[N];
        finalNum = new double[N];
        finalString = new String[N];

        for (int i = 0; i < N; i++) {
            counters[i] = 0;
            summs[i] = 0;
            avs[i] = 0;
            finalNum[i] = 0;
            finalString[i] = "";
        }

        allDone = 0;

        create = false;

        maxStorageCapacity = 3;
        storage = new ArrayList[N];
        for (int i = 0; i < N; i++) {
            storage[i] = new ArrayList<>();
            for (int j = 0; j < maxStorageCapacity; j++) {
                storage[i].add((double) 0);
            }
        }

        storageCounter = new int[N];
        for (int i = 0; i < N; i++) {
            storageCounter[i] = 0;
        }
    }

    private BeaconManager mBeaconManager;

    @Override
    public void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        mBeaconManager.bind(this);
    }

    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    //FB:79:DA:46:C2:B2
    //CF:59:C9:2C:AA:DB
    //DA:D7:7D:6C:B3:B2


    private int maxStorageCapacity;
    private List<Double>[] storage;

    private int[] storageCounter;

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        Map<String, Integer> ourBeacons = new HashMap<>();
        ourBeacons.put("FB:79:DA:46:C2:B2", 0);
        ourBeacons.put("CF:59:C9:2C:AA:DB", 1);
        ourBeacons.put("DA:D7:7D:6C:B3:B2", 2);

        for (final Beacon beacon : beacons) {
            String crtAddress = beacon.getBluetoothAddress();
            Integer localId = ourBeacons.get(crtAddress);

            if (localId != null) {

                counters[localId]++;

                T = beacon.getTxPower();

                summs[localId] += Math.abs(beacon.getRssi());

                //-------------------------------------
                int divider = Approximation.DEVIDER;

                if (counters[localId] >= divider) {
                    if (counters[localId] == divider) {
                        allDone++;
                    }
                    avs[localId] = summs[localId] / counters[localId];
                }
                //-------------------------------------
                //storaging

                int crtIndex = storageCounter[localId] % maxStorageCapacity;
                storageCounter[localId]++;

                storage[localId].set(crtIndex, avs[localId]);


                //-------------------------------------
                if (allDone == N) {

                    double[] localSum = new double[N];

                    for (int i = 0; i < N; i++) {
                        localSum[i] = 0;
                        int capacityOffset = 0;

                        for (int j = 0; j < maxStorageCapacity; j++) {
                            if (storage[i].get(j) != 0) {
                                localSum[i] += storage[i].get(j);
                            } else {
                                capacityOffset++;
                            }
                        }
                        localSum[i] = (localSum[i] / (maxStorageCapacity - capacityOffset));

                        finalNum[i] = Approximation.approximate(localSum[i] * f);
                    }

                    for (int i = 0; i < N; i++) {
                        finalString[i] = String.valueOf(localSum[i]);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            tvA.setText(finalString[0]);
                            tvB.setText(finalString[1]);
                            tvC.setText(finalString[2]);
                        }
                    });

                    //send request
                    if (create) {
                        //create

                        create = false;
                        String message = et.getText().toString();
                        if (!message.equals("") && message != null) {

                            String action = "create";
                            String server = "http://ghostspace.pe.hu/" + action + ".php?";


                            final String request = server + "a=" + finalString[0] + "&b=" + finalString[1]
                                    + "&c=" + finalString[2] + "&message=" + message;// + "&cutoff=10";

                            RequestTask rt = new RequestTask(new MyAction() {
                                @Override
                                public void action(String str) {
                                    staticTv.setText(str);
                                }
                            });
                            rt.execute(request);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    et.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Ghost's message is empty!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        RequestTask task = new RequestTask(new MyAction() {
            @Override
            public void action(String str) {

            }
        });

        task.execute("http://ghostspace.pe.hu/clean.php?stats");
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onStop() {
        RequestTask task = new RequestTask(new MyAction() {
            @Override
            public void action(String str) {

            }
        });

        task.execute("http://ghostspace.pe.hu/clean.php?stats");

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        create = true;
    }
}
