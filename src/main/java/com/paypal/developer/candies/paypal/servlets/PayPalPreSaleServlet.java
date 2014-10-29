/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paypal.developer.candies.paypal.servlets;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.LoggingManager;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.core.rest.PayPalResource;
import com.paypal.developer.candies.paypal.util.GenerateAccessToken;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author jprestes
 */
@WebServlet(name = "PayPalPreSaleServlet", urlPatterns = {"/paypalpresale"})
public class PayPalPreSaleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger LOGGER = Logger.getLogger("PayPalPreSaleServlet");
    
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
		// ##Load Configuration
		// Load SDK configuration for
		// the resource. This intialization code can be
		// done as Init Servlet.
		InputStream is = PayPalPreSaleServlet.class
				.getResourceAsStream("/sdk_config.properties");
		try {
			PayPalResource.initConfig(is);
		} catch (PayPalRESTException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
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
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();        
        APIContext apiContext = null;
        String accessToken = null;
        
        try {
            accessToken = GenerateAccessToken.getAccessToken();

            // ### Api Context
            // Pass in a `ApiContext` object to authenticate
            // the call and to send a unique request id
            // (that ensures idempotency). The SDK generates
            // a request id if you do not pass one explicitly.
            apiContext = new APIContext(accessToken);
            
            // Use this variant if you want to pass in a request id
            // that is meaningful in your application, ideally
            // a order id.
            /*
             * String requestId = Long.toString(System.nanoTime(); APIContext
             * apiContext = new APIContext(accessToken, requestId ));
             */
            
            // ###Amount
            // Let's you specify a payment amount.
            Amount amount = new Amount();
            amount.setCurrency("BRL");
            // Total must be equal to sum of shipping, tax and subtotal.
            amount.setTotal("1");

            // ###Transaction
            // A transaction defines the contract of a
            // payment - what is the payment for and who
            // is fulfilling it. Transaction is created with
            // a `Payee` and `Amount` types
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Cup of delicious candies");

            // The Payment creation API requires a list of
            // Transaction; add the created `Transaction`
            // to a List
            List<Transaction> transactions = new ArrayList<Transaction>();
            transactions.add(transaction);

            // ###Payer
            // A resource representing a Payer that funds a payment
            // Payment Method
            // as 'paypal'
            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            // ###Payment
            // A Payment Resource; create one using
            // the above types and intent as 'sale'
            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);
            
            // ###Redirect URLs
            RedirectUrls redirectUrls = new RedirectUrls();
            String guid = UUID.randomUUID().toString().replaceAll("-", "");
            redirectUrls.setCancelUrl(request.getScheme() + "://"
                            + request.getServerName() + ":" + request.getServerPort()
                            + request.getContextPath() + "/paypalpostsale?guid=" + guid);
            redirectUrls.setReturnUrl(request.getScheme() + "://"
                            + request.getServerName() + ":" + request.getServerPort()
                            + request.getContextPath() + "/paypalpostsale?guid=" + guid);
            payment.setRedirectUrls(redirectUrls);

            // Create a payment by posting to the APIService
            // using a valid AccessToken
            // The return object contains the status;
            Payment createdPayment = payment.create(apiContext);
            LOGGER.info("Created payment with id = "
                            + createdPayment.getId() + " and status = "
                            + createdPayment.getState());

            session.setAttribute("paymentId", createdPayment.getId());

            // ###Payment Approval Url
            Iterator<Links> links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                    Links link = links.next();
                    if (link.getRel().equalsIgnoreCase("approval_url")) {
                            session.setAttribute("redirectURL", link.getHref());
                    }
            }
            request.setAttribute("response", Payment.getLastResponse());
            
        } catch (PayPalRESTException e) {
            LoggingManager.debug(PayPalPreSaleServlet.class, e.getLocalizedMessage());
            LOGGER.log(Level.SEVERE, Payment.getLastRequest() + " - " + Payment.getLastResponse());
            request.setAttribute("error", e.getMessage());
        }
        
        request.setAttribute("request", Payment.getLastRequest());
        request.getRequestDispatcher("confirmation.jsp").forward(request, response);
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

}
