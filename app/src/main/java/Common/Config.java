/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import Model.MyEchoDevices;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author hoang-trung-duc
 */
public class Config {

    private static final boolean RUNNING_ON_LINUX = true;
//    private static final boolean RUNNING_ON_LINUX = false;
    public static long PERIOD = 5000;
    public static String filename = "Config.txt";

    public static String getLink() {
        if (RUNNING_ON_LINUX) {
            return "/opt/tomcat/webapps/";
        } else {
            return "";
        }
    }

    public static void updateConfig() throws Exception {
        try {
            // Save config
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getLink() + filename))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MyEchoDevices.EV.name).append(",");
                stringBuilder.append(MyEchoDevices.BATTERY.name).append(",");
                stringBuilder.append(MyEchoDevices.SOLAR.name).append(",");
                stringBuilder.append(MyEchoDevices.LIGHT.name);

                // Update to MyEchoDevices constants
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            throw new Exception(Config.class.getName() + ", Save: " + e.getMessage());
        }
    }

    public static void updateDeviceName() {
        // Load Config
        byte[] encoded;
        try {
            encoded = Files.readAllBytes(Paths.get(getLink() + filename));
        } catch (IOException ex) {
            System.err.println("Load name config error: " + ex.getMessage());
            return;
        }
        String config = new String(encoded, StandardCharsets.UTF_8);

        // Split 4 nick type
        String[] nameConfig = config.split("\\,");
        if (config.isEmpty() || nameConfig.length != 4) {
            System.err.println("Load name config error : File Config is empty.");
            return;
        }

        // Update to MyEchoDevices constants
        MyEchoDevices.loadConfig(nameConfig);
    }

}
