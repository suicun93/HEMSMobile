package AView;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Main.EchoController;

public class MainActivity extends AppCompatActivity {

      private ImageView notFoundFace = null;
      private RecyclerView rvDevices;
      private Button btnUpdate = null;
      private TextView txtItemCount = null;

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
            // </editor-fold>
            // Start Controller and Update
            try {
                  EchoController.startController();
            } catch (Exception e) {
                  Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // Get View
            notFoundFace = findViewById(R.id.imageView);
            rvDevices = findViewById(R.id.listView);
            btnUpdate = findViewById(R.id.button);
            txtItemCount = findViewById(R.id.txtItemCount);
            btnUpdate.setOnClickListener(v -> update());
      }

      private void disableButtonUpdate() {
            // Disable button update to wait for 1 second.
            new Thread(() -> {
                  runOnUiThread(() -> {
                        btnUpdate.setText(R.string.updating);
                        btnUpdate.setEnabled(false);
                  });
                  try {
                        Thread.sleep(3000);
                  } catch (InterruptedException e) {
                        System.out.println("InterruptedException: " + e.getMessage());
                  }
                  runOnUiThread(() -> {
                        btnUpdate.setText(R.string.update);
                        btnUpdate.setEnabled(true);
                  });
            }).start();
      }

      public void update() {
            disableButtonUpdate();
            // Reload list Devices.
            if (!EchoController.listDevice().isEmpty()) {
                  rvDevices.setVisibility(View.VISIBLE);
                  notFoundFace.setVisibility(View.INVISIBLE);
                  txtItemCount.setText(EchoController.listDevice().size() + " 台のデバイスが見つかりった。");

                  // Update list Device
                  DevicesAdapter adapter = new DevicesAdapter();
                  adapter.btnUpdate = btnUpdate;
                  rvDevices.setAdapter(adapter);
                  rvDevices.setOnFlingListener(new RecyclerView.OnFlingListener() {
                        private static final int SWIPE_VELOCITY_THRESHOLD = 4000;

                        @Override
                        public boolean onFling(int velocityX, int velocityY) {
                              if (btnUpdate.isEnabled()) {
                                    if (velocityY < (-1) * SWIPE_VELOCITY_THRESHOLD) {
                                          update();
                                          return true;
                                    }
                              }
                              return false;
                        }
                  });
                  rvDevices.setLayoutManager(new LinearLayoutManager(this));
            } else {
                  rvDevices.setVisibility(View.INVISIBLE);
                  notFoundFace.setVisibility(View.VISIBLE);
                  txtItemCount.setText(R.string.default_title_no_item_found);

                  Toast.makeText(this, R.string.default_title_no_item_found, Toast.LENGTH_SHORT).show();
            }
      }
}
