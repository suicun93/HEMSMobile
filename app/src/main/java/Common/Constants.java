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

      public static final String ECHO_TAG = "Echo";
      public static String ERROR = "エラー";

      public static int MAX_ENERGY_EV_BATT = 999999999;
      public static int MAX_ENERGY_SOLAR = 65533;
      public static int PERIOD = 2000;

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
