<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Candies</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href='http://fonts.googleapis.com/css?family=Lobster' rel='stylesheet' type='text/css'>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <script type="text/javascript">
        function stringify() {
            var request = '<%=request.getAttribute("request")%>';
            var response = '<%=request.getAttribute("response")%>';
            var error ='<%=request.getAttribute("error")%>';

            /* 
             * 
            var req = document.getElementById("request");
            if (request != null) {
                    req.innerHTML = request;
            } else {
                    req.innerHTML = "No payload for this request";
            }

            var resp = document.getElementById("response");
            */

            $('#splash').hide();
            $('#failed').hide();
            //alert(response);
            //alert(response == "{status: success!}")
            if (response != null && response == "{status: success!}") {
                $('#splash').show();
            } else if (error != null) {
                $('#splash').hide();
                $('#failed').val(response + "<br />" + error);
                $('#failed').show();
            }
        }
    </script>

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
    
</head>
<body onload="stringify();">
    <div id="splash">
        <img src='https://openclipart.org/image/300px/svg_to_png/173135/Happy_Tiger.png'>
        <br />
        <br />
        <span id="message">Take your candies!</span>

        <br />
        <br/>
	<a href="index.html">Buy again</a>
    </div>
    <div id="failed">
        &nbsp;
        <br />
        <br/>
	<a href="index.html">Buy again</a>
    </div>
</body>
</html>