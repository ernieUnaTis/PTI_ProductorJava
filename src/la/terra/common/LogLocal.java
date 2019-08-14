/*
 * LogLocal.java
 *
 * Created on 26 de noviembre de 2007, 11:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package la.terra.common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author isanjuan
 */
public class LogLocal {

    String servicio;
    String path;
    
    /** Creates a new instance of LogLocal */
    public LogLocal(String path, String servicio) throws NullPointerException
    {
        if( path==null || servicio==null)
            throw new NullPointerException("LogLocal::LogLocal - ERROR: path o servicio en valor nulo");

        this.servicio = servicio;
        this.path = path;
    }
    
    
    public void write(String texto)
    {
        Calendar fechaActual = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaLarga = formatter.format(fechaActual.getTime());
        String fechaCorta = fechaLarga.substring(0, 10);
        
        try
        {
            //FileWriter outFile = new FileWriter("D:/smsgateway/TomcatMO/webapps/smsgwweb/logs/filtro_jamba_imi_cl-"+ fechaCorta +".log", true);
            FileWriter outFile = new FileWriter(this.path + this.servicio + fechaCorta +".log", true);
            BufferedWriter buffer = new BufferedWriter(outFile);
            buffer.write(fechaLarga + " | ");
            buffer.write(texto);
            buffer.write("\n");
            buffer.close();
        }
        catch(Exception error)
        {
            error.printStackTrace();
        }
    }
    
    public void info(String texto)
    {
        Calendar fechaActual = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaLarga = formatter.format(fechaActual.getTime());
        String fechaCorta = fechaLarga.substring(0, 10);
        
        try
        {
            //FileWriter outFile = new FileWriter("D:/smsgateway/TomcatMO/webapps/smsgwweb/logs/filtro_jamba_imi_cl-"+ fechaCorta +".log", true);
            FileWriter outFile = new FileWriter(this.path + this.servicio + fechaCorta +".log", true);
            BufferedWriter buffer = new BufferedWriter(outFile);
            buffer.write(fechaLarga + " | ");
            buffer.write(texto);
            buffer.write("\n");
            buffer.close();
        }
        catch(Exception error)
        {
            Utils.getStackTraceAsString(error);
        }
    }

    /**
     * Sobrecarga de metodo info para formatear linea de log con clase y metodo llamante
     * @param strClase Nombre de clase desde la cual se llama al metodo
     * @param strMetodo Nombre del metodo
     * @param texto texto a enviar a archivo de Log
     */
    public void info(String strClase, String strMetodo, String texto)
    {
        
        this.info(strClase + " [" + strMetodo + "] - " + texto);
        

    }
    
}
