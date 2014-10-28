<%-- 
    Document   : confirmation
    Created on : Oct 27, 2014, 5:58:18 PM
    Author     : jprestes
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Candies</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href='http://fonts.googleapis.com/css?family=Lobster' rel='stylesheet' type='text/css'>
        <style type="text/css">
            div   {
                position:  absolute;
                top: 50%;
                left: 50%;
                text-align: center;
            }
            
            #splash {
                margin-left: -75px;
                margin-top: -170px;
            }
            
            #form   {
                margin-left: -75px;
                margin-top: -200px;
            }
            
            #btn {
                border: none;
                background: url('https://www.paypalobjects.com/webstatic/en_US/btn/btn_buynow_pp_142x27.png') no-repeat top left;
                padding: 2px 8px;
                width: 142px;
                height: 27px;
            }
            
            #message    {
                font-family: 'Lobster', cursive;
                font-size: x-large;
                font-color: brown;
            }
        </style>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
        <script type="text/javascript">    
            function tradicional(){
                jQuery('<div class="sa_payPal_overlay" style="visibility:visible;position:fixed; width:100%; height:100%; filter:progid:DXImageTransform.Microsoft.Gradient(GradientType=1, StartColorStr=\'#88ffffff\', EndColorStr=\'#88ffffff\'); background: rgba(255,255,255,0.8); top:0; left:0; z-index: 999999;"><div style=" background: #FFF; background-image: linear-gradient(top, #FFFFFF 45%, #E9ECEF 80%);background-image: -o-linear-gradient(top, #FFFFFF 45%, #E9ECEF 80%);background-image: -moz-linear-gradient(top, #FFFFFF 45%, #E9ECEF 80%);background-image: -webkit-linear-gradient(top, #FFFFFF 45%, #E9ECEF 80%);background-image: -ms-linear-gradient(top, #FFFFFF 45%, #E9ECEF 80%);background-image: -webkit-gradient(linear, left top,left bottom,color-stop(0.45, #FFFFFF),color-stop(0.8, #E9ECEF));display: block;margin: auto;position: fixed; margin-left:-220px; left:45%;top: 40%;text-align: center;color: #2F6395;font-family: Arial;padding: 15px;font-size: 15px;font-weight: bold;width: 530px;-webkit-box-shadow: 3px 2px 13px rgba(50, 50, 49, 0.25);box-shadow: rgba(0, 0, 0, 0.2) 0px 0px 0px 5px;border: 1px solid #CFCFCF;border-radius: 6px;"><img style="display:block;margin:0 auto 10px" src="https://www.paypalobjects.com/en_US/i/icon/icon_animated_prog_dkgy_42wx42h.gif"><h2>Wait few seconds.</h2> <div style="margin:30px auto 0;"><img src="https://www.paypal-brasil.com.br/logocenter/util/img/logo_paypal.png"/></div></div></div>').appendTo('body');
                document.frmPurchase.submit();
            }
        </script>
    </head>
    <body>
        <div id="splash">
            <img src="images/gumball-machine.gif" id="imgcandy" />
            <br />
            <br />
            <span id="message">Choose your location</span>
            <form id="frmPurchase" name="frmPurchase" action="paypalsale" method="POST">
                <select id="location" name="location">
                    <option value="jeffprestes/candies/paulista" selected="selected">Brazil, Sao Paulo, Paulista</option>
                </select>
                <br /><br />
                <input type="button" id="btn" value="" onclick="tradicional()" />
            </form>
        </div>
    </body>
</html>
