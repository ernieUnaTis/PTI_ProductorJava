package la.terra.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import la.terra.common.*;
/**
 * Servlet implementation class Productor
 */
@WebServlet("/Productor")
public class Productor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	static LogLocal logger = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            long timerBillIni = (new Date()).getTime();
            
            String dirLog = ("/var/log/camel/");
            String archivo = ("produceramq_");
            logger = new LogLocal(dirLog, archivo);
            
            String ipAcceso = request.getRemoteAddr();
            String idInterno = Utils.generarIdInterno();
            String msisdn = request.getParameter("msisdn");
            String carrier = request.getParameter("carrier");
            String operador = request.getParameter("operador");
            
            if(operador.equalsIgnoreCase("722200")) {
                msisdn = Utils.politicaMovil(msisdn, operador); 
            }
            
            
            ProducerImpl prod = new ProducerImpl();
            try {
                prod.enviar(carrier,msisdn,operador,idInterno,logger);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           
            long timerBillFin = (new Date()).getTime();
            out.println("OK");
            out.close();
            logger.info("[ipAcceso:"+ipAcceso+"] [Msisdn:"+msisdn+"][carrier:"+carrier+"] [idInterno:"+idInterno+"]  [TiempoTx:"+ (timerBillFin - timerBillIni) + "]");
        }


    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                    processRequest(request, response);
            
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                    processRequest(request, response);
           
    }

}
