package AView;import android.app.TimePickerDialog;import android.content.DialogInterface;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.view.WindowManager;import android.widget.ArrayAdapter;import android.widget.Button;import android.widget.EditText;import android.widget.ImageView;import android.widget.Spinner;import android.widget.TableLayout;import android.widget.TextView;import android.widget.TimePicker;import android.widget.Toast;import androidx.appcompat.app.AppCompatActivity;import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;import com.sonycsl.echo.eoj.device.DeviceObject;import com.sonycsl.echo.eoj.device.housingfacilities.Battery;import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;import java.io.IOException;import java.util.Calendar;import java.util.Timer;import java.util.TimerTask;import Common.Convert;import Main.EchoController;import Model.MyEchoDevices;import Model.OperationMode;import Model.OperationStatus;import static Model.MyEchoDevices.BATTERY;import static Model.MyEchoDevices.EV;import static Model.MyEchoDevices.SOLAR;import static Model.MyEchoDevices.UNKNOWN;public class DeviceActivity extends AppCompatActivity {      private Intent intent;      private DeviceObject deviceObject = null;      private TimePickerDialog picker;      private TableLayout configLayout;      private Button configButton, onButton, offButton;      private boolean configButtonSetupped = false;      private TextView deviceNameBar,                deviceName,                ipAddressTxt,                operationStatusTxt,                operationModeTxt,                instantaneousTxt,                currentElectricTxt,                currentPercentTxt;      private EditText startTime, endTime, instantaneousEditText;      private Spinner spinner;      private ImageView deviceImage;      private OperationMode[] operationModeOptions = new OperationMode[]{OperationMode.Charging, OperationMode.RapidCharging, OperationMode.Discharging};      private Timer updateTask;      @Override      protected void onCreate(Bundle savedInstanceState) {            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);            super.onCreate(savedInstanceState);            setContentView(R.layout.activity_device);            setupUI();            // Get device's information from parent and validate device            intent = getIntent();            String positionString = intent.getStringExtra("position");            String ipString = intent.getStringExtra("ip");            short eojShort = intent.getShortExtra("eoj", (short) 0x0000);            try {                  int position = Integer.parseInt(positionString);                  deviceObject = EchoController.listDevice().get(position);                  if (deviceObject == null                            || deviceObject.getEchoClassCode() != eojShort                            || !deviceObject.getNode().getAddressStr().equalsIgnoreCase(ipString)) {                        throw new Exception("Device disconnected.");                  }            } catch (Exception e) {                  System.out.println("Error: Position = " + positionString + " " + e.getMessage());                  Toast.makeText(this, "デバイスが切断されました。", Toast.LENGTH_LONG).show();                  finish();            }            // If device is ready:            // Refresh when scroll down            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);            pullToRefresh.setOnRefreshListener(() -> {                  updateData();                  pullToRefresh.setRefreshing(false);            });            // Set up Configuration            // Button ON            onButton.setOnClickListener(v -> {                  try {                        deviceObject.set().reqSetOperationStatus(new byte[]{OperationStatus.ON.value}).send();                        Toast.makeText(DeviceActivity.this, "Turn ON success", Toast.LENGTH_SHORT).show();                  } catch (IOException e) {                        Toast.makeText(DeviceActivity.this, "Turn ON failed: " + e.getMessage(), Toast.LENGTH_LONG).show();                  }            });            // Button OFF            offButton.setOnClickListener(v -> {                  try {                        deviceObject.set().reqSetOperationStatus(new byte[]{OperationStatus.OFF.value}).send();                        Toast.makeText(DeviceActivity.this, "Turn OFF success", Toast.LENGTH_SHORT).show();                  } catch (IOException e) {                        Toast.makeText(DeviceActivity.this, "Turn OFF failed: " + e.getMessage(), Toast.LENGTH_LONG).show();                  }            });            // Refresh UI            loadData();            // Refresh data every 2 seconds            updateTask = new Timer();            updateTask.schedule(new TimerTask() {                  @Override                  public void run() {                        updateData();                  }            }, 0, 2000);      }      private void loadData() {            try {                  MyEchoDevices device = MyEchoDevices.from(deviceObject);                  // Name                  deviceNameBar.setText(device.name());                  deviceName.setText(device.name());                  // IP Address                  ipAddressTxt.setText(getTextResource(R.string.ip_address_string) + deviceObject.getNode().getAddressStr());                  // Image                  deviceImage.setImageResource(device.image());                  // Operation status                  operationStatusTxt.setText(getTextResource(R.string.operation_status_string) + device.operationStatus.name());                  // operation mode, current, current percentage, instantaneous                  // Config layout                  switch (device) {                        case EV:                        case BATTERY:                              operationModeTxt.setVisibility(View.VISIBLE);                              instantaneousTxt.setVisibility(View.VISIBLE);                              currentElectricTxt.setVisibility(View.VISIBLE);                              currentPercentTxt.setVisibility(View.VISIBLE);                              configLayout.setVisibility(View.VISIBLE);                              operationModeTxt.setText(getTextResource(R.string.operation_mode_string) + device.operationMode.japaneseName);                              instantaneousTxt.setText(getTextResource(R.string.instantaneous_string) + device.d3 + " W");                              currentElectricTxt.setText(getTextResource(R.string.current_electric_string) + device.e2 + " Wh");                              currentPercentTxt.setText(getTextResource(R.string.current_percentage_string) + device.e4 + " %");                              break;                        case SOLAR:                              operationModeTxt.setVisibility(View.GONE);                              findViewById(R.id.mode_schedule_text).setVisibility(View.GONE);                              spinner.setVisibility(View.GONE);                              instantaneousTxt.setVisibility(View.VISIBLE);                              currentElectricTxt.setVisibility(View.VISIBLE);                              configLayout.setVisibility(View.VISIBLE);                              instantaneousTxt.setText(getTextResource(R.string.instantaneous_string) + device.e0 + " W");                              currentElectricTxt.setText(getTextResource(R.string.current_electric_string) + device.e1 + " Wh");                              break;                        default:                              break;                  }                  // TODO: Config button: Disconnect Device -> Config -> Test                  if (!configButtonSetupped) {                        configButton.setOnClickListener(v -> {                              // Get Data: start time, end time, state, instantaneous value                              String startTimeString = startTime.getText().toString();                              String endTimeString = endTime.getText().toString();                              String instantaneousString = instantaneousEditText.getText().toString();                              String[] startTimeArray = startTimeString.split("\\:");                              String[] endTimeArray = endTimeString.split("\\:");                              if (startTimeArray.length != 2 || endTimeArray.length != 2) {                                    Toast.makeText(DeviceActivity.this, "時間を再入力してください", Toast.LENGTH_LONG).show();                                    return;                              }                              if (instantaneousString.length() <= 0) {                                    Toast.makeText(DeviceActivity.this, "瞬時値を再入力してください", Toast.LENGTH_LONG).show();                                    return;                              }                              try {                                    // Convert to int.                                    int startHour = Integer.parseInt(startTimeArray[0].trim());                                    int startMinute = Integer.parseInt(startTimeArray[1].trim());                                    int endHour = Integer.parseInt(endTimeArray[0].trim());                                    int endMinute = Integer.parseInt(endTimeArray[1].trim());                                    int instantaneous = Integer.parseInt(instantaneousString);                                    OperationMode selectedMode = (OperationMode) spinner.getSelectedItem();                                    if (startHour < 0 || startHour > 23 ||                                              endHour < 0 || endHour > 23 ||                                              startMinute < 0 || startMinute > 60 ||                                              endMinute < 0 || endMinute > 60 ||                                              instantaneous < 0 || instantaneous > 999999999                                    ) {                                          throw new Exception("データが範囲外です。");                                    }                                    // Convert to byte                                    byte startHourByte = Convert.intToByte(startHour);                                    byte startMinuteByte = Convert.intToByte(startMinute);                                    byte endHourByte = Convert.intToByte(endHour);                                    byte endMinuteByte = Convert.intToByte(endMinute);                                    byte selectedModeByte = selectedMode.value;                                    byte[] instantaneousByte = Convert.intToByteArray(instantaneous);                                    // Merge byte to byte array                                    byte[] EDT = null;                                    switch (device) {                                          case EV:                                          case BATTERY:                                                EDT = new byte[9];                                                EDT[8] = instantaneousByte[3];                                                EDT[7] = instantaneousByte[2];                                                EDT[6] = instantaneousByte[1];                                                EDT[5] = instantaneousByte[0];                                                EDT[4] = selectedModeByte;                                                EDT[3] = endMinuteByte;                                                EDT[2] = endHourByte;                                                EDT[1] = startMinuteByte;                                                EDT[0] = startHourByte;                                                break;                                          case SOLAR:                                                EDT = new byte[6];                                                EDT[5] = instantaneousByte[3];                                                EDT[4] = instantaneousByte[2];                                                EDT[3] = endMinuteByte;                                                EDT[2] = endHourByte;                                                EDT[1] = startMinuteByte;                                                EDT[0] = startHourByte;                                                break;                                          default:                                                break;                                    }                                    // Send EPC                                    deviceObject.set().reqSetProperty((byte) 0xff, EDT).send();                              } catch (NumberFormatException e) {                                    Toast.makeText(DeviceActivity.this, "無効なデータです。", Toast.LENGTH_LONG).show();                                    return;                              } catch (Exception e) {                                    Toast.makeText(DeviceActivity.this, "デバイスが切断されました。", Toast.LENGTH_LONG).show();                              }                        });                  }                  // Update data                  updateData();            } catch (Exception e) {                  System.err.println("Error: " + "reLoad device failed" + " " + e.getMessage());                  Toast.makeText(this, "デバイスが切断されました。", Toast.LENGTH_LONG).show();                  finish();            }      }      private void updateData() {            // Update data            MyEchoDevices device = MyEchoDevices.from(deviceObject);            runOnUiThread(() -> {                  try {                        // Operation status                        operationStatusTxt.setText(getTextResource(R.string.operation_status_string) + device.operationStatus.name());                        // IP Address                        ipAddressTxt.setText(getTextResource(R.string.ip_address_string) + deviceObject.getNode().getAddressStr());                  } catch (Exception e) {                        System.err.println("update Data error: " + e.getMessage());                        Toast.makeText(this, "デバイスが切断されました。", Toast.LENGTH_SHORT).show();                        finish();                        updateTask.cancel();                  }            });            if (device != UNKNOWN) {                  switch (device) {                        case BATTERY:                              runOnUiThread(() -> {                                    operationModeTxt.setText(getTextResource(R.string.operation_mode_string) + device.operationMode.japaneseName);                                    instantaneousTxt.setText(getTextResource(R.string.instantaneous_string) + device.d3 + " W");                              });                              new Thread(() -> {                                    synchronized (BATTERY) {                                          try {                                                ((Battery) deviceObject).get().reqGetRemainingStoredElectricity1().send(); // E2                                                BATTERY.wait();                                                runOnUiThread(() -> currentElectricTxt.setText(getTextResource(R.string.current_electric_string) + device.e2 + " Wh"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentElectricTxt.setText(getResources().getString(R.string.current_electric_string)));                                          }                                    }                              }).start(); // E2                              new Thread(() -> {                                    synchronized (BATTERY) {                                          try {                                                ((Battery) deviceObject).get().reqGetRemainingStoredElectricity3().send(); // E4                                                BATTERY.wait();                                                runOnUiThread(() -> currentPercentTxt.setText(getTextResource(R.string.current_percentage_string) + device.e4 + " %"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentPercentTxt.setText(getResources().getString(R.string.current_percentage_string)));                                          }                                    }                              }).start();  // E4                              break;                        case EV:                              runOnUiThread(() -> {                                    operationModeTxt.setText(getTextResource(R.string.operation_mode_string) + device.operationMode.japaneseName);                                    instantaneousTxt.setText(getTextResource(R.string.instantaneous_string) + device.d3 + " W");                              });                              new Thread(() -> {                                    synchronized (EV) {                                          try {                                                ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity1().send(); // E2                                                EV.wait();                                                runOnUiThread(() -> currentElectricTxt.setText(getTextResource(R.string.current_electric_string) + device.e2 + " Wh"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentElectricTxt.setText(getResources().getString(R.string.current_electric_string)));                                          }                                    }                              }).start(); // E2                              new Thread(() -> {                                    synchronized (EV) {                                          try {                                                ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity3().send(); // E4                                                EV.wait();                                                runOnUiThread(() -> currentPercentTxt.setText(getTextResource(R.string.current_percentage_string) + device.e4 + " %"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentPercentTxt.setText(getResources().getString(R.string.current_percentage_string)));                                          }                                    }                              }).start();  // E4                              break;                        case SOLAR:                              runOnUiThread(() -> {                                    instantaneousTxt.setText(getTextResource(R.string.instantaneous_string) + device.e0 + " W");                              });                              // E1                              synchronized (SOLAR) {                                    try {                                          ((HouseholdSolarPowerGeneration) deviceObject).get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1                                          SOLAR.wait();                                          runOnUiThread(() -> currentElectricTxt.setText(getTextResource(R.string.current_electric_string) + device.e1 + " Wh"));                                    } catch (Exception e) {                                          runOnUiThread(() -> currentElectricTxt.setText(getResources().getString(R.string.current_electric_string)));                                    }                              }                              break;                        default:                              break;                  }            }      }      private void setupUI() {            // 1 Spinner            spinner = findViewById(R.id.spinner);            ArrayAdapter<OperationMode> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, operationModeOptions);            spinner.setAdapter(adapter);            // 1 Image            deviceImage = findViewById(R.id.device_img2);            // 1 Layout            configLayout = findViewById(R.id.config_layout);            // 3 Buttons            onButton = findViewById(R.id.on_btn);            offButton = findViewById(R.id.off_btn);            configButton = findViewById(R.id.config_btn);            // 3 Edit texts            startTime = findViewById(R.id.start_time_txt);            endTime = findViewById(R.id.end_time_txt);            instantaneousEditText = findViewById(R.id.instantaneous_edit_text);            // Otherwise, text view            deviceNameBar = findViewById(R.id.name_bar_txt);            deviceName = findViewById(R.id.name2_txt);            ipAddressTxt = findViewById(R.id.ip_address2_txt);            operationStatusTxt = findViewById(R.id.operation_status2_txt);            operationModeTxt = findViewById(R.id.operation_mode2_txt);            instantaneousTxt = findViewById(R.id.instantaneous2_txt);            currentElectricTxt = findViewById(R.id.current2_txt);            currentPercentTxt = findViewById(R.id.current_percent2_txt);      }      private String getTextResource(int id) {            return getResources().getString(id).replace("分からない", "");      }      public void backPreviousActivity(View v) {            super.finish();      }      public void selectTime(View v) {            if (!(v instanceof EditText)) {                  return;            }            final Calendar calendar = Calendar.getInstance();            int hour = calendar.get(Calendar.HOUR_OF_DAY);            int minutes = calendar.get(Calendar.MINUTE);            // time picker dialog            picker = new TimePickerDialog(DeviceActivity.this,                      new TimePickerDialog.OnTimeSetListener() {                            @Override                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {                                  ((EditText) v).setText(sHour + ":" + sMinute);                            }                      }, hour, minutes, true);            picker.setCanceledOnTouchOutside(true);            picker.setIcon(R.drawable.back_button);            picker.setTitle("時間を選択する");            picker.setButton(DialogInterface.BUTTON_POSITIVE, "選択する", picker);            picker.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", picker);            picker.show();      }}