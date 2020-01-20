/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Servlet;

import Common.Convert;
import Main.EchoController;
import com.sonycsl.echo.eoj.device.DeviceObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 *
 * @author hoang-trung-duc
 */
public class SetEPC {

    PrintWriter out = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/plain;charset=utf-8");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
//
//        try {
//
//            out = response.getWriter();
//
//            // Get Parameter
//            String paramRequest = getParam(request);
//            String[] params = paramRequest.split("\\,");
//
//            // Check parameter counts
//            if (params.length != 4) {
//                out.print("{\n"
//                        + "\"Failed\":\"" + "Wrong parameters" + "\"\n"
//                        + "}");
//                return;
//            }
//
//            // Split param
//            if (params[0].contains("0x")) {
//                params[0] = params[0].replace("0x", "");
//            }
//            byte[] data = Convert.hexStringToByteArray(params[0]);
//            ByteBuffer bb = ByteBuffer.allocate(2);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
//            bb.put(data[1]);
//            bb.put(data[0]);
//            short receivedEOJ = bb.getShort(0);     // 0x027E
//            String receivedAddress = params[1];                     // b8:27:eb:f4:18:10
//            byte EPC = Convert.hexStringToByteArray(params[2])[0];  // 0xFF
//            byte[] EDT = Convert.hexStringToByteArray(params[3]);   // (170)(175)(40)(xxxxxxxx)
//
//            // Check EOJ and Address and send set request
//            for (DeviceObject deviceObject : EchoController.listDevice()) {
//                if (deviceObject.getEchoClassCode() == receivedEOJ // Compare EOJ
//                        && deviceObject.getNode().getAddressStr().equalsIgnoreCase(receivedAddress)) {  // Compare Address
//                    // 結果を受け取る準備ための関数です。
//                    deviceObject.getReceiver().out = out;
//
//                    // 新しいEPCを設定する
//                    deviceObject.set().reqSetProperty(EPC, EDT).send();
//                    // 結果を受け取るを待ちる
//                    synchronized (out) {
//                        out.wait();
//                    }
//                    return;
//                }
//            }
//
//            // No devices found
//            out.print("{\n"
//                    + "\"Failed\":\"" + "No devices found." + "\"\n"
//                    + "}");
//        } catch (Exception ex) {
//            System.out.println(SetEPC.class.getName() + " " + ex.getMessage());
//            if (out != null) {
//                out.print("{\n"
//                        + "\"Failed\":\"" + ex.getMessage() + "\"\n"
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
