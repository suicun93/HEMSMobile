package AView;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Timer;
import java.util.TimerTask;

import Common.Constants;
import Main.EchoController;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

public class MainActivity extends AppCompatActivity {

      private ImageView notFoundFace = null;
      private RecyclerView rvDevices;
      private Button btnUpdate = null;
      private TextView txtItemCount = null;
      private SwipeRefreshLayout pullToRefresh;
      private boolean listIsEmpty;
      private DevicesAdapter adapter;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // <editor-fold defaultstate="collapsed" desc="// Skip this">
            //Full screen is set for the Window
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);

            // Require multicast
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                  WifiManager.MulticastLock lock = wifi.createMulticastLock("HEMSmulticast");
                  lock.acquire();
            }

            // Get View
            notFoundFace = findViewById(R.id.image_view);
            rvDevices = findViewById(R.id.list_view);
            btnUpdate = findViewById(R.id.btn_update);
            txtItemCount = findViewById(R.id.txt_item_count);
            pullToRefresh = findViewById(R.id.refresh);

            // Refresh when scroll down
            pullToRefresh.setColorSchemeResources(R.color.xanh_nhat, R.color.mau_chu_title, R.color.white);
            pullToRefresh.setProgressBackgroundColorSchemeResource(R.color.trang_nen);
            pullToRefresh.setOnRefreshListener(() -> onUpdatingFromView(pullToRefresh));
            // </editor-fold>

            // Bind data
            adapter = new DevicesAdapter();
            rvDevices.setAdapter(adapter);
            rvDevices.setLayoutManager(new WrapContentLinearLayoutManager(MainActivity.this));

            // Listen new devices
            EchoController.MY_ECHO_EVENT_LISTENER.setOnReceive((onAddingDevice, echoProperty) -> {
                  // Reload whole of list Devices.
                  runOnUiThread(() -> {
                        listIsEmpty = EchoController.listDevice.isEmpty();
                        rvDevices.setVisibility(listIsEmpty ? View.INVISIBLE : View.VISIBLE);
                        notFoundFace.setVisibility(listIsEmpty ? View.VISIBLE : View.INVISIBLE);
                        if (!listIsEmpty) {
                              String itemFound = EchoController.listDevice.size() + getString(R.string.number_devices_found);
                              txtItemCount.setText(itemFound);
                        } else
                              txtItemCount.setText(R.string.default_title_no_item_found);
                        // Update list Device
                        int position = echoProperty == null ? 0 : echoProperty.epc;
                        if (onAddingDevice) adapter.notifyItemInserted(position);
                        else adapter.notifyItemRemoved(position);
                  });
            });

            // Start Controller and Update (On some new OSs, we can not run network thread on main UI -> run on another thread.)
            new Thread(this::startController).start();

//            new Timer().schedule(new TimerTask() {
//                  @Override
//                  public void run() {
//                        runOnUiThread(() -> {
//                              listIsEmpty = EchoController.listDevice.isEmpty();
//                              rvDevices.setVisibility(listIsEmpty ? View.INVISIBLE : View.VISIBLE);
//                              notFoundFace.setVisibility(listIsEmpty ? View.VISIBLE : View.INVISIBLE);
//                              if (!listIsEmpty) {
//                                    String itemFound = EchoController.listDevice.size() + getString(R.string.number_devices_found);
//                                    txtItemCount.setText(itemFound);
//                              } else txtItemCount.setText(R.string.default_title_no_item_found);
//
//
//
//                        });
//                  }
//            }, 250);
      }

      private void startController() {
            try {
                  EchoController.startController();
            } catch (Exception e) {
                  runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.cannot_start_controller, Toast.LENGTH_LONG).show());
            }
      }

      public void onUpdatingFromView(View v) { // Just only update information, not update list devices
            v.performHapticFeedback(VIRTUAL_KEY);
            // Disable button update to wait for 2 second.
            int delay = 2000;
            btnUpdate.setText(R.string.updating);
            btnUpdate.setEnabled(false);
            if (v.getId() == R.id.btn_update)
                  pullToRefresh.setEnabled(false);
            new Timer().schedule(new TimerTask() {
                  @Override
                  public void run() {
                        runOnUiThread(() -> {
                              btnUpdate.setText(R.string.update);
                              btnUpdate.setEnabled(true);
                              pullToRefresh.setRefreshing(false);
                              pullToRefresh.setEnabled(true);
                        });
                  }
            }, delay);
            // Update information
            if (rvDevices.getAdapter() != null) rvDevices.getAdapter().notifyDataSetChanged();
      }
}

class WrapContentLinearLayoutManager extends LinearLayoutManager {
      public WrapContentLinearLayoutManager(Context context) {
            super(context);
      }

      @Override
      public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                  super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                  Log.e(Constants.ECHO_TAG, "meet a IOOBE in RecyclerView");
            }
      }
}