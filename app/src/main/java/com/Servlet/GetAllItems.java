/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Servlet;

import Common.Config;
import Model.MyEchoDevices;
import Main.EchoController;
import static Model.MyEchoDevices.BATTERY;
import static Model.MyEchoDevices.EV;
import static Model.MyEchoDevices.SOLAR;
import com.sonycsl.echo.eoj.device.DeviceObject;
import com.sonycsl.echo.eoj.device.housingfacilities.Battery;
import com.sonycsl.echo.eoj.device.housingfacilities.ElectricVehicle;
import com.sonycsl.echo.eoj.device.housingfacilities.HouseholdSolarPowerGeneration;


/**
 *
 * @author hoang-trung-duc
 */
public class GetAllItems {

//    /**
//     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
//     * methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
//
//        PrintWriter out = null;
//        try {
//            out = response.getWriter();
//            EchoController.startController();
//
//            // Load config
//            Config.updateDeviceName();
//
//            StringBuilder responseString = new StringBuilder("{\n"
//                    + "   \"success\":\"OK\",\n");
//            responseString.append("   \"devices\":{\n");
//            if (!EchoController.listDevice().isEmpty()) {
//                EchoController.listDevice().forEach((DeviceObject deviceObject) -> {
//                    MyEchoDevices device = MyEchoDevices.from(deviceObject);
//                    if (device != MyEchoDevices.UNKNOWN) {
//                        try {
//                            switch (device) {
//                                case BATTERY:
//                                    synchronized (BATTERY) {
//                                        ((Battery) deviceObject).get().reqGetRemainingStoredElectricity1().send(); // E2
//                                        BATTERY.wait();
//                                        ((Battery) deviceObject).get().reqGetRemainingStoredElectricity3().send(); // E4
//                                        BATTERY.wait();
//                                    }
//                                    break;
//                                case EV:
//                                    synchronized (EV) {
//                                        ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity1().send(); // E2
//                                        EV.wait();
//                                        ((ElectricVehicle) deviceObject).get().reqGetRemainingBatteryCapacity3().send(); // E4
//                                        EV.wait();
//                                    }
//                                    break;
//                                case SOLAR:
//                                    ((HouseholdSolarPowerGeneration) deviceObject).get().reqGetMeasuredCumulativeAmountOfElectricityGenerated().send(); // E1
//                                    synchronized (SOLAR) {
//                                        SOLAR.wait();
//                                    }
//                                    break;
//                                default:
//                                    break;
//                            }
//                        } catch (IOException e) {
//                            System.out.println("Get information failed: " + device.name() + ", " + e.getMessage());
//                        } catch (InterruptedException ex) {
//                            System.out.println("");
//                        }
//                        // Write to response
//                        responseString.append(device);
//                    } else {
//                        responseString.append("      \"" + MyEchoDevices.UNKNOWN.type + "\":{ \n"
//                                + "         \"name\":\"" + MyEchoDevices.UNKNOWN.name + "\",\n"
//                                + "         \"eoj\":\"" + String.format("0x%04x", deviceObject.getEchoClassCode()) + "\",\n"
//                                + "         \"macAdd\":\"" + deviceObject.getNode().getAddressStr() + "\"\n"
//                                + "      }\n");
//                    }
//                    responseString.append("      ,\n");
//                });
//                responseString.deleteCharAt(responseString.length() - 1);
//                responseString.deleteCharAt(responseString.length() - 1);
//            }
//            responseString.append("   }\n"
//                    + "}");
//
//            out.print(responseString.toString());
//
//        } catch (IOException ex) {
//            System.out.println(GetAllItems.class.getName() + " " + ex.getMessage());
//            if (out != null) {
//                out.print("{\n"
//                        + "\"success\":\"" + ex.getMessage() + "\"\n"
//                        + "}");
//            }
//        }
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Handles the HTTP <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>

}
