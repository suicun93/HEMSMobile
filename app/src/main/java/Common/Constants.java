/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

/**
 * @author hoang-trung-duc
 */
public enum Constants {
      Name("name"),
      EOJ("eoj"),
      MacAddress("macAdd"),
      OperationStatus("status"),
      OperationMode("mode"),
      ChargeDischargeElectricEnergy("d3"),
      InstantaneousAmountOfElectricityGenerated("e0"),
      AmountOfElectricityGenerated("e1"),
      RemainingElectric1("e2"),
      RemainingElectric3("e4"),
      IpAddressKey("ip"),
      PositionKey("position"),
      EOJKey("eoj");

      public static String DEVICE_DISCONNECTED = "デバイスが切断されました。";
      public static String SET_EPC_SUCCESS = "正常に設定た";
      public static String SET_EPC_FAIL = "設定に失敗た";
      public static String REENTER_START_TIME = "時間を再入力してください";
      public static String REENTER_END_TIME = "瞬時値を再入力してください";
      public static String DATA_OUT_OF_RANGE = "データが範囲外です。";
      public static String DATA_INVALID = "無効なデータです。";
      public static String UNKNOWN = "分からない";
      public static String SELECT = "選択する";
      public static String CANCEL = "キャンセル";
      public static String ERROR = "エラー";

      public static int MAX_ENERGY_EV_BATT = 999999999;
      public static int MAX_ENERGY_SOLAR = 65533;
      public static int TIME_OUT = 100;

      public static byte SCHEDULE_CONFIG_EPC = (byte) 0xFF;


      public final String value;

      Constants(String value) {
            this.value = value;
      }

      @androidx.annotation.NonNull
      @Override
      public String toString() {
            return value;
      }

}
