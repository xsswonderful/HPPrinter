package myclass;

import printer.PrinterSnmpUtil;
import snmp.SNMPBadValueException;
import snmp.SNMPGetException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.TreeMap;

public class printerType {


    public static void main(String[] args) throws SNMPBadValueException, IOException, SNMPGetException {

            Socket socket=null;
            try{

                    socket = new Socket("192.168.223.1",9100);
                    new Thread(new ClientThread(socket,"infomem")).start();

            }catch (IOException e){
                e.printStackTrace();
            }

    }
}
