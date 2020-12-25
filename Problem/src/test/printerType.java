package test;


import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class printerType {




    public static void main(String[] args){

            Socket socket=null;
            try{
                    socket = new Socket("192.168.223.1",9100);
                     File f=new File("C:\\Users\\ADMIN\\Downloads\\电子合同.pdf");
                    new Thread(new Variable(socket,f)).start();
            }catch (IOException e){
                e.printStackTrace();
            }

    }
}
