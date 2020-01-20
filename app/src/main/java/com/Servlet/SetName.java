/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Servlet;

import Common.Config;
import Model.MyEchoDevices;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author hoang-trung-duc
 */
public class SetName  {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
//        response.setContentType("text/plain;charset=utf-8");
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
//        PrintWriter out = null;
//        try {
//            out = response.getWriter();
//            String nameRequest = getParam(request);
//            String[] params = nameRequest.split("\\,");
//            if (params.length != 2) {
//                out.print("Request Invalid");
//                return;
//            }
//            String type = params[0];
//            String name = params[1];
//            for (MyEchoDevices device : MyEchoDevices.values()) {
//                if (device.type.equalsIgnoreCase(type)) {
//                    device.name = name;
//                    Config.updateConfig();
//                    out.print("success");
//                    return;
//                }
//            }
//            out.print("No device found");
//        } catch (Exception ex) {
//            System.out.println(Time.class.getName() + ex.getMessage());
//            if (out != null) {
//                out.print(Time.class.getName() + ex.getMessage());
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
