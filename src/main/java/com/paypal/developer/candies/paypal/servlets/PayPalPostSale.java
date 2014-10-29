/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paypal.developer.candies.paypal.servlets;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.core.LoggingManager;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.developer.candies.paypal.util.GenerateAccessToken;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author jprestes
 */
@WebServlet(name = "PayPalPostSale", urlPatterns = {"/paypalpostsale"})
public class PayPalPostSale extends HttpServlet {

    private static Logger LOGGER = Logger.getLogger("PayPalPostSale");
    
    private String mqttServer = "";
    private String mqttQueue = "";
    private String mqttPort = "";
    private String mqttClientId = "";
    
    @Override
    public void init (ServletConfig cfg) throws ServletException    {
        
        try {
            //InputStream inputStream = Checkout.class.getResourceAsStream("/var/candies/candies.properties");
            File arq = new File("/var/candies/candies.properties");
            FileReader inputStream = new FileReader(arq);
            Properties prop = new Properties();
            prop.load(inputStream);
            
            this.mqttQueue = prop.getProperty("mqttqueue");
            this.mqttServer = prop.getProperty("mqttserver");
            this.mqttClientId = prop.getProperty("mqttclientid");
            this.mqttPort = prop.getProperty("mqttport");
            
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Checkout Servlet could not load MQTT Properties. Loading default...", ex);
            this.mqttQueue = "jeffprestes/candies/world";
            this.mqttServer = "iot.eclipse.org";
            this.mqttClientId = "candies-jeff-localserver";
            this.mqttPort = "1883";
        }
        
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        APIContext apiContext = null;
        String accessToken = null;
        HttpSession session = request.getSession();        
        
        try {
            accessToken = GenerateAccessToken.getAccessToken();

            apiContext = new APIContext(accessToken);

            if (request.getParameter("PayerID") != null) {
                
                Payment payment = new Payment();
                payment.setId((String) session.getAttribute("paymentId"));

                PaymentExecution paymentExecution = new PaymentExecution();
                paymentExecution.setPayerId(request.getParameter("PayerID"));
                payment.execute(apiContext, paymentExecution);
                
                payment = Payment.get(accessToken, payment.getId());
                
                String transactionStatus = "created";
                
                if (!payment.getTransactions().isEmpty())  {
                    if (!payment.getTransactions().get(0).getRelatedResources().isEmpty())  {
                        transactionStatus = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState();
                        LOGGER.log(Level.INFO, "Transaction details: " + payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getState());
                    }
                }
                
                if (transactionStatus.equals("completed"))  {
                    if (this.releaseCandies())      {
                        request.setAttribute("response", "{status: success!}");
                    }   else    {
                        request.setAttribute("error", "{status: candies don't released!}");
                    }
                }  else {
                    request.setAttribute("error", "{status: payment wasn't completed!}");
                }
                
            }
                
        } catch (PayPalRESTException e) {
                LoggingManager.debug(PayPalPostSale.class, e.getLocalizedMessage());
                request.setAttribute("error", e.getMessage());
        }
        
        request.getRequestDispatcher("response.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean releaseCandies()    {
        String topic        = this.mqttQueue;   
        int qos             = 2;
        String broker       = "tcp://" + this.mqttServer + ":" + this.mqttPort;
        String clientId     = this.mqttClientId;
        String msg          = "release";
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient client;
        boolean rtn = false;

        try     {
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            client.connect(connOpts);

            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(qos);

            client.publish(topic, message);

            client.disconnect();

            client.close();
            
            LOGGER.log(Level.INFO, "Message sent to machine to release the candy");
            rtn = true;
            
        } catch (MqttException me)     {
            LOGGER.log(Level.SEVERE, "reason "+me.getReasonCode());
            LOGGER.log(Level.SEVERE, "msg "+me.getMessage());
            LOGGER.log(Level.SEVERE, "loc "+me.getLocalizedMessage());
            LOGGER.log(Level.SEVERE, "cause "+me.getCause());
            LOGGER.log(Level.SEVERE, "Error sending message to Machine", me);
        }
        client = null;

        return rtn;
    }
    
}
