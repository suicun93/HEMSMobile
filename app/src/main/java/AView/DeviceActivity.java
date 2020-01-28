package AView;import android.app.TimePickerDialog;import android.content.DialogInterface;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.view.WindowManager;import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.Button;import android.widget.EditText;import android.widget.ImageView;import android.widget.Spinner;import android.widget.TableLayout;import android.widget.TextView;import android.widget.Toast;import androidx.appcompat.app.AppCompatActivity;import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;import com.sonycsl.echo.eoj.device.DeviceObject;import com.sonycsl.echo.eoj.device.housingfacilities.Battery;import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;import java.util.Calendar;import java.util.Timer;import java.util.TimerTask;import Common.Constants;import Common.Convert;import Main.EchoController;import Model.MyEchoDevices;import Model.OperationMode;import Receiver.ResultHandlable;import static Model.MyEchoDevices.BATTERY;import static Model.MyEchoDevices.EV;import static Model.MyEchoDevices.SOLAR;import static Model.MyEchoDevices.UNKNOWN;import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;import static com.sonycsl.echo.eoj.device.DeviceObject.EDT_OPERATION_STATUS_OFF;import static com.sonycsl.echo.eoj.device.DeviceObject.EDT_OPERATION_STATUS_ON;public class DeviceActivity extends AppCompatActivity {      private Intent intent;      private DeviceObject deviceObject = null;      private TimePickerDialog picker;      private TableLayout configLayout;      private Button configButton;      private boolean configButtonSetupped = false;      private boolean UIReady = false;      private byte[] EDT = null;      private TextView deviceNameBar,                deviceName,                ipAddressTxt,                operationStatusTxt,                operationModeTxt,                instantaneousTxt,                currentElectricTxt,                currentPercentTxt;      private EditText startTime, endTime, instantaneousEditText;      private Spinner spinner;      private ImageView deviceImage;      private OperationMode[] operationModeOptions = new OperationMode[]{OperationMode.Charging, OperationMode.RapidCharging, OperationMode.Discharging};      private Timer updateTask;      @Override      protected void onCreate(Bundle savedInstanceState) {            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);            super.onCreate(savedInstanceState);            setContentView(R.layout.activity_device);            setupUI();            // Get device's information from parent and validate device            intent = getIntent();            String positionString = intent.getStringExtra(Constants.PositionKey.value);            String ipString = intent.getStringExtra(Constants.IpAddressKey.value);            short eojShort = intent.getShortExtra(Constants.EOJKey.value, (short) 0x0000);            // Check Device is ready or not            try {                  int position = Integer.parseInt(positionString);                  deviceObject = EchoController.listDevice().get(position);                  if ((deviceObject == null)                            || (deviceObject.getEchoClassCode() != eojShort)                            || !deviceObject.getNode().getAddressStr().equalsIgnoreCase(ipString)) {                        throw new Exception("Device disconnected.");                  }            } catch (Exception e) {                  System.out.println("Error: Position = " + positionString + " " + e.getMessage());                  alertDeviceDisconnected();                  finish();            }            // If device is ready:            // Refresh when scroll down            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);            pullToRefresh.setOnRefreshListener(() -> {                  pullToRefresh.performHapticFeedback(VIRTUAL_KEY);                  updateData();                  pullToRefresh.setRefreshing(false);            });            // Handle result when set EPC            if (deviceObject.getReceiver() instanceof ResultHandlable) {                  ((ResultHandlable) deviceObject.getReceiver()).setResultHandle(this::handleResult);            }            // Refresh UI            loadData();            // Refresh data every 2 seconds            updateTask = new Timer();            updateTask.schedule(new TimerTask() {                  @Override                  public void run() {                        updateData();                  }            }, 0, 2000);      }      private void loadData() {            try {                  MyEchoDevices device = MyEchoDevices.from(deviceObject);                  // Name                  deviceNameBar.setText(device.name());                  deviceName.setText(device.name());                  // IP Address                  ipAddressTxt.setText(getTextResourceWithoutUnknown(R.string.ip_address_string) + deviceObject.getNode().getAddressStr());                  // Image                  deviceImage.setImageResource(device.image());                  // Operation status                  operationStatusTxt.setText(getTextResourceWithoutUnknown(R.string.operation_status_string) + device.operationStatus.name());                  // operation mode, current, current percentage, instantaneous                  // Config layout                  switch (device) {                        case EV:                        case BATTERY:                              operationModeTxt.setVisibility(View.VISIBLE);                              instantaneousTxt.setVisibility(View.VISIBLE);                              currentElectricTxt.setVisibility(View.VISIBLE);                              currentPercentTxt.setVisibility(View.VISIBLE);                              configLayout.setVisibility(View.VISIBLE);                              operationModeTxt.setText(getTextResourceWithoutUnknown(R.string.operation_mode_string) + device.operationMode.japaneseName);                              instantaneousTxt.setText(getTextResourceWithoutUnknown(R.string.instantaneous_string) + device.d3 + " W");                              currentElectricTxt.setText(getTextResourceWithoutUnknown(R.string.current_electric_string) + device.e2 + " Wh");                              currentPercentTxt.setText(getTextResourceWithoutUnknown(R.string.current_percentage_string) + device.e4 + " %");                              break;                        case SOLAR:                              operationModeTxt.setVisibility(View.GONE);                              findViewById(R.id.mode_schedule_text).setVisibility(View.GONE);                              spinner.setVisibility(View.GONE);                              instantaneousTxt.setVisibility(View.VISIBLE);                              currentElectricTxt.setVisibility(View.VISIBLE);                              configLayout.setVisibility(View.VISIBLE);                              instantaneousTxt.setText(getTextResourceWithoutUnknown(R.string.instantaneous_string) + device.e0 + " W");                              currentElectricTxt.setText(getTextResourceWithoutUnknown(R.string.current_electric_string) + device.e1 + " Wh");                              break;                        default:                              break;                  }                  // Config button                  if (!configButtonSetupped) {                        configButton.setOnClickListener(v -> {                              v.performHapticFeedback(VIRTUAL_KEY);                              // Get Data: start time, end time, state, instantaneous value                              String startTimeString = startTime.getText().toString();                              String endTimeString = endTime.getText().toString();                              String instantaneousString = instantaneousEditText.getText().toString();                              String[] startTimeArray = startTimeString.split(":");                              String[] endTimeArray = endTimeString.split(":");                              if (startTimeArray.length != 2 || endTimeArray.length != 2) {                                    Toast.makeText(DeviceActivity.this, getString(R.string.reenter_time), Toast.LENGTH_SHORT).show();                                    return;                              }                              if (instantaneousString.length() <= 0) {                                    Toast.makeText(DeviceActivity.this, getString(R.string.reenter_instantaneous_value), Toast.LENGTH_SHORT).show();                                    return;                              }                              try {                                    // Convert to int.                                    int startHour = Integer.parseInt(startTimeArray[0].trim());                                    int startMinute = Integer.parseInt(startTimeArray[1].trim());                                    int endHour = Integer.parseInt(endTimeArray[0].trim());                                    int endMinute = Integer.parseInt(endTimeArray[1].trim());                                    int instantaneous = Integer.parseInt(instantaneousString);                                    OperationMode selectedMode = (OperationMode) spinner.getSelectedItem();                                    // Check range of value                                    if (startHour < 0 || startHour > 23 ||                                              endHour < 0 || endHour > 23 ||                                              startMinute < 0 || startMinute > 60 ||                                              endMinute < 0 || endMinute > 60 ||                                              instantaneous < 0 || instantaneous > Constants.MAX_ENERGY_EV_BATT                                    )                                          throw new NumberFormatException(getString(R.string.data_out_of_range));                                    // https://echonet.jp/wp/wp-content/uploads/pdf/General/Standard/Release/Release_L_en/Appendix_Release_L_E.pdf#page=286                                    // Solar's Instantaneous < 65533                                    if (device == SOLAR)                                          if (instantaneous > Constants.MAX_ENERGY_SOLAR)                                                throw new NumberFormatException(getString(R.string.data_out_of_range));                                    // Convert to byte                                    byte startHourByte = Convert.intToByte(startHour);                                    byte startMinuteByte = Convert.intToByte(startMinute);                                    byte endHourByte = Convert.intToByte(endHour);                                    byte endMinuteByte = Convert.intToByte(endMinute);                                    byte selectedModeByte = selectedMode.value;                                    byte[] instantaneousByte = Convert.intToByteArray(instantaneous);                                    // Merge byte to byte array                                    switch (device) {                                          case EV:                                          case BATTERY:                                                EDT = new byte[9];                                                EDT[8] = instantaneousByte[3];                                                EDT[7] = instantaneousByte[2];                                                EDT[6] = instantaneousByte[1];                                                EDT[5] = instantaneousByte[0];                                                EDT[4] = selectedModeByte;                                                EDT[3] = endMinuteByte;                                                EDT[2] = endHourByte;                                                EDT[1] = startMinuteByte;                                                EDT[0] = startHourByte;                                                break;                                          case SOLAR:                                                EDT = new byte[6];                                                EDT[5] = instantaneousByte[3];                                                EDT[4] = instantaneousByte[2];                                                EDT[3] = endMinuteByte;                                                EDT[2] = endHourByte;                                                EDT[1] = startMinuteByte;                                                EDT[0] = startHourByte;                                                break;                                          default:                                                break;                                    }                                    // Send EPC                                    new Thread(() -> {                                          try {                                                deviceObject.set().reqSetProperty(Constants.SCHEDULE_CONFIG_EPC, EDT).send();                                          } catch (Exception e) {                                                runOnUiThread(this::alertDeviceDisconnected);                                          }                                    }).start();                                    // Send EPC                              } catch (NumberFormatException e) {                                    Toast.makeText(DeviceActivity.this, getString(R.string.data_invalid) + e.getMessage(), Toast.LENGTH_SHORT).show();                              } catch (Exception e) {                                    alertDeviceDisconnected();                              }                        });                  }                  // Update data                  updateData();            } catch (Exception e) {                  System.out.println("Error: " + "reLoad device failed" + " " + e.getMessage());                  alertDeviceDisconnected();                  finish();            }      }      private void updateData() {            // Update data            MyEchoDevices device = MyEchoDevices.from(deviceObject);            runOnUiThread(() -> {                  try {                        // Operation status                        operationStatusTxt.setText(getTextResourceWithoutUnknown(R.string.operation_status_string) + device.operationStatus.name());                        // IP Address                        ipAddressTxt.setText(getTextResourceWithoutUnknown(R.string.ip_address_string) + deviceObject.getNode().getAddressStr());                  } catch (Exception e) {                        System.out.println("Error update Data: " + e.getMessage());                        alertDeviceDisconnected();                        finish();                  }            });            if (device != UNKNOWN) {                  switch (device) {                        case BATTERY:                              runOnUiThread(() -> {                                    operationModeTxt.setText(getTextResourceWithoutUnknown(R.string.operation_mode_string) + device.operationMode.japaneseName);                                    instantaneousTxt.setText(getTextResourceWithoutUnknown(R.string.instantaneous_string) + device.d3 + " W");                              });                              new Thread(() -> {                                    synchronized (BATTERY) {                                          try {                                                ((Battery) deviceObject).get().reqGetRemainingStoredElectricity1().send(); // E2                                                BATTERY.wait(Constants.TIME_OUT);                                                String result = getTextResourceWithoutUnknown(R.string.current_electric_string) + device.e2 + " Wh";                                                runOnUiThread(() -> currentElectricTxt.setText(result));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentElectricTxt.setText(getString(R.string.current_electric_string)));                                          }                                    }                              }).start(); // E2                              new Thread(() -> {                                    synchronized (BATTERY) {                                          try {                                                ((Battery) deviceObject).get().reqGetRemainingStoredElectricity3().send(); // E4                                                BATTERY.wait(Constants.TIME_OUT);                                                runOnUiThread(() -> currentPercentTxt.setText(getTextResourceWithoutUnknown(R.string.current_percentage_string) + device.e4 + " %"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentPercentTxt.setText(getString(R.string.current_percentage_string)));                                          }                                    }                              }).start();  // E4                              break;                        case EV:                              runOnUiThread(() -> {                                    operationModeTxt.setText(getTextResourceWithoutUnknown(R.string.operation_mode_string) + device.operationMode.japaneseName);                                    instantaneousTxt.setText(getTextResourceWithoutUnknown(R.string.instantaneous_string) + device.d3 + " W");                              });                              new Thread(() -> {                                    synchronized (EV) {                                          try {                                                ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity1().send(); // E2                                                EV.wait(Constants.TIME_OUT);                                                runOnUiThread(() -> currentElectricTxt.setText(getTextResourceWithoutUnknown(R.string.current_electric_string) + device.e2 + " Wh"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentElectricTxt.setText(getString(R.string.current_electric_string)));                                          }                                    }                              }).start(); // E2                              new Thread(() -> {                                    synchronized (EV) {                                          try {                                                ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity3().send(); // E4                                                EV.wait(Constants.TIME_OUT);                                                runOnUiThread(() -> currentPercentTxt.setText(getTextResourceWithoutUnknown(R.string.current_percentage_string) + device.e4 + " %"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentPercentTxt.setText(getString(R.string.current_percentage_string)));                                          }                                    }                              }).start();  // E4                              break;                        case SOLAR:                              runOnUiThread(() -> instantaneousTxt.setText(getTextResourceWithoutUnknown(R.string.instantaneous_string) + device.e0 + " W"));                              new Thread(() -> {                                    synchronized (SOLAR) {                                          try {                                                ((HouseholdSolarPowerGeneration) deviceObject).get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1                                                SOLAR.wait(Constants.TIME_OUT);                                                runOnUiThread(() -> currentElectricTxt.setText(getTextResourceWithoutUnknown(R.string.current_electric_string) + device.e1 + " Wh"));                                          } catch (Exception e) {                                                runOnUiThread(() -> currentElectricTxt.setText(getString(R.string.current_electric_string)));                                          }                                    }                              }).start(); // E1                              break;                        case LIGHT:                              break;                        default:                              break;                  }            }      }      private void setupUI() {            // 1 Spinner            spinner = findViewById(R.id.spinner);            ArrayAdapter<OperationMode> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, operationModeOptions);            spinner.setAdapter(adapter);            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {                  @Override                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {                        if (!UIReady) {                              UIReady = true;                              return;                        }                        view.performHapticFeedback(VIRTUAL_KEY);                  }                  @Override                  public void onNothingSelected(AdapterView<?> parent) {                  }            });            // 1 Image            deviceImage = findViewById(R.id.device_img2);            // 1 Layout            configLayout = findViewById(R.id.config_layout);            // 1 Buttons            configButton = findViewById(R.id.config_btn);            // 3 Edit texts            startTime = findViewById(R.id.start_time_txt);            endTime = findViewById(R.id.end_time_txt);            instantaneousEditText = findViewById(R.id.instantaneous_edit_text);            // Otherwise, text view            deviceNameBar = findViewById(R.id.name_bar_txt);            deviceName = findViewById(R.id.name2_txt);            ipAddressTxt = findViewById(R.id.ip_address2_txt);            operationStatusTxt = findViewById(R.id.operation_status2_txt);            operationModeTxt = findViewById(R.id.operation_mode2_txt);            instantaneousTxt = findViewById(R.id.instantaneous2_txt);            currentElectricTxt = findViewById(R.id.current2_txt);            currentPercentTxt = findViewById(R.id.current_percent2_txt);      }      private String getTextResourceWithoutUnknown(int id) {            return getString(id).replace(getString(R.string.unknown), "");      }      public void backPreviousActivity(View v) {            v.performHapticFeedback(VIRTUAL_KEY);            super.finish();      }      @Override      public void finish() {            if (updateTask != null) updateTask.cancel();            super.finish();      }      public void selectTime(View v) {            v.performHapticFeedback(VIRTUAL_KEY);            if (!(v instanceof EditText)) return;            final Calendar calendar = Calendar.getInstance();            int hour = calendar.get(Calendar.HOUR_OF_DAY);            int minutes = calendar.get(Calendar.MINUTE);            // Time picker dialog            picker = new TimePickerDialog(DeviceActivity.this,                      (tp, sHour, sMinute) -> {                            ((EditText) v).setText(sHour + ":" + sMinute);                            v.performHapticFeedback(VIRTUAL_KEY);                      }, hour, minutes, true);            picker.setCanceledOnTouchOutside(true);            picker.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.select_btn), picker);            picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_btn), picker);            picker.show();      }      private void alertDeviceDisconnected() {            Toast.makeText(this, getString(R.string.device_disconnected), Toast.LENGTH_SHORT).show();      }      public void onClickButtonOnOff(View v) {            v.performHapticFeedback(VIRTUAL_KEY);            if (v.getId() == R.id.on_btn) EDT = EDT_OPERATION_STATUS_ON;            else if (v.getId() == R.id.off_btn) EDT = EDT_OPERATION_STATUS_OFF;            else return;            new Thread(() -> {                  try {                        deviceObject.set().reqSetOperationStatus(EDT).send();                  } catch (Exception e) {                        runOnUiThread(this::alertDeviceDisconnected);                  }            }).start();      }      private void handleResult(boolean success) {            String notification = success ? getString(R.string.set_epc_success) : getString(R.string.set_epc_fail);            runOnUiThread(() -> Toast.makeText(DeviceActivity.this, notification, Toast.LENGTH_SHORT).show());            if (success) updateData();      }}