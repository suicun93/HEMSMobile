package AView;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Timer;
import java.util.TimerTask;

import Common.Vibration;
import Main.EchoController;

public class MainActivity extends AppCompatActivity {

      private ImageView notFoundFace = null;
      private RecyclerView rvDevices;
      private Button btnUpdate = null;
      private TextView txtItemCount = null;
      // Save state
      private Parcelable recyclerViewState;

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
            notFoundFace = findViewById(R.id.imageView);
            rvDevices = findViewById(R.id.listView);
            btnUpdate = findViewById(R.id.button);
            txtItemCount = findViewById(R.id.txtItemCount);
            // </editor-fold>
            // Start Controller and Update
            try {
                  EchoController.startController();
            } catch (Exception e) {
                  Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                  return;
            }

            // Button Update's onClick
            btnUpdate.setOnClickListener(v -> {
                  Vibration.vibrate(this);
                  disableButtonUpdateInSeveralSeconds();
                  update();
            });

            // Reload when scroll down
            rvDevices.setOnFlingListener(new RecyclerView.OnFlingListener() {
                  private static final int SWIPE_VELOCITY_THRESHOLD = 4000;

                  @Override
                  public boolean onFling(int velocityX, int velocityY) {
                        if (btnUpdate.isEnabled()) {
                              if (velocityY < (-1) * SWIPE_VELOCITY_THRESHOLD) {
                                    Vibration.vibrate(MainActivity.this);
                                    disableButtonUpdateInSeveralSeconds();
                                    update();
                                    return true;
                              }
                        }
                        return false;
                  }
            });

            // Update View after 3 seconds
            new Timer().schedule(new TimerTask() {
                  @Override
                  public void run() {
                        runOnUiThread(() -> {
                              if (rvDevices.getLayoutManager() != null) {
                                    recyclerViewState = rvDevices.getLayoutManager().onSaveInstanceState();
                              }
                              update();
                              if (recyclerViewState != null) {
                                    rvDevices.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                              }
                        });
                  }
            }, 500, 3000);
      }

      private void disableButtonUpdateInSeveralSeconds() {
            // Disable button update to wait for 2 second.
            int delay = 2000;
            runOnUiThread(() -> {
                  btnUpdate.setText(R.string.updating);
                  btnUpdate.setEnabled(false);
            });
            new Timer().schedule(new TimerTask() {
                  @Override
                  public void run() {
                        runOnUiThread(() -> {
                              btnUpdate.setText(R.string.update);
                              btnUpdate.setEnabled(true);
                        });
                  }
            }, delay);
      }

      public void update() {
            // Reload list Devices.
            boolean listIsEmpty = EchoController.listDevice().isEmpty();
            rvDevices.setVisibility(listIsEmpty ? View.INVISIBLE : View.VISIBLE);
            notFoundFace.setVisibility(listIsEmpty ? View.VISIBLE : View.INVISIBLE);

            if (!listIsEmpty) {
                  txtItemCount.setText(EchoController.listDevice().size() + " 台のデバイスが見つかりった。");
                  // Update list Device
                  DevicesAdapter adapter = new DevicesAdapter();
                  rvDevices.setAdapter(adapter);
                  rvDevices.setLayoutManager(new LinearLayoutManager(this));
            } else {
                  txtItemCount.setText(R.string.default_title_no_item_found);
            }
      }
}
