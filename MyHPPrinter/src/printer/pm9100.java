package printer;

import snmp.SNMPBadValueException;
import snmp.SNMPGetException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.TreeMap;

public class pm9100 {
    private static void print(File document, String printerIpAddress, boolean staple)
    {
        try (Socket socket = new Socket(printerIpAddress, 9100))
        {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            byte[] bytes = Files.readAllBytes(document.toPath());

            out.write(27); //esc
            out.write("%-12345X@PJL\n".getBytes());
            //out.write("@PJL SET DUPLEX=ON\n".getBytes());

//            if (staple)
//            {
//                out.write("@PJL SET STAPLEOPTION=ONE\n".getBytes());
//            }
            out.write("@PJL ENTER LANGUAGE=PDF\n".getBytes());
            out.write(bytes);
            out.write(27); //esc
            out.write("%-12345X".getBytes());
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }


    ;
//    static {
//        try {
//            printerSnmpUtil = new PrinterSnmpUtil("192.168.223.1");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) throws SNMPBadValueException, IOException, SNMPGetException {
        PrinterSnmpUtil printerSnmpUtil= new PrinterSnmpUtil("192.168.223.1");;
        TreeMap<String, Object> stats = printerSnmpUtil.getPrinterStats();
        System.out.printf("%15s: %s%n", "Status", stats.get("status"));
        String status=(String)stats.get("status");

        //System.out.printf(status);
        if(status.contains("就绪")){
            File f=new File("E:\\new\\hp printer\\pjl-tool-master\\pjl-tool-master\\1_1_200913185354.pdf");
            print(f,"192.168.223.1",false);
        }else{
            System.out.printf("%15s: %s%n", "Status", stats.get("status"));
        }

    }
}
