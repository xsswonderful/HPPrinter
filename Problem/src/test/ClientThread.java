package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ClientThread implements Runnable {
    Socket socket = null;

    String Command=null;

    public ClientThread(Socket socket, String command) {
        this.socket = socket;
        this.Command=command;

    }

    @Override
    public void run() {
        OutputStream out = null;
        InputStream in = null;
        System.out.println("Begin to Chat to server...");
        try {
            out =  socket.getOutputStream();
            in =  socket.getInputStream();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doWrite(out);
                System.out.println("begin read message from server.");
                doRead(in);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean doRead(InputStream in){
        byte[] bytes=new byte[1024];
        try{
            in.read(bytes);
            System.out.println(new String(bytes));
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

//    <ESC>%-12345X@PJL <CR><LF>
//            @PJL USTATUS TIMED = 0 <CR><LF>
//<ESC>%-12345X
    public boolean doWrite(OutputStream out){
        //String c=getCommand(command);
        //String c=getCommand(this.Command);
        try {

            out.write(27); //esc
            out.write("%-12345X@PJL\r\n".getBytes());

            //out.write("@PJL INFO CONFIG\n".getBytes());
            out.write("@PJL USTATUS TIMED = 0\r\n".getBytes());
            out.write(27); //esc
            out.write("%-12345X".getBytes());
            out.flush();
            //out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String getCommand(String command){
        String pre="@PJL ";
        if(command.equals("infoconf")){
            return pre+"INFO CONFIG"+"\n";
        }else if(command.equals("infostatus")){
            return pre+"INFO STATUS"+"\n";
        }else if(command.equals("infoustatus")){
            return  pre+"INFO USTATUS"+"\n";
        }else if(command.equals("infoid")){
            return pre+"INFO ID"+"\n";
        }else if(command.equals("infofsys")){
            return pre+"INFO FILESYS"+"\n";
        }else if(command.equals("infomem")){
            return pre+" INFO MEMORY"+"\n";
        }else if(command.equals("infopcount")){
            return pre+"INFO PAGECOUNT"+"\n";
        }else if(command.equals("infovars")){
            return pre+"INFO VARIABLES"+"\n";
        }else if(command.equals("fsdownload")){
            return pre+"FSDOWNLOAD FORMAT:BINARY NAME = "+"\n";
        }else if(command.equals("fsdirlist")){
            return pre+"FSDIRLIST NAME = "+"\n";
        }else if(command.equals("fsupload")){
            return pre+"FSUPLOAD NAME = "+"\n";
        }else if(command.equals(("fsquery"))){
            return pre+"FSQUERY NAME = "+"\n";
        }else if(command.equals("fsmkdir")){
            return pre+"FSMKDIR NAME = "+"\n";
        }else if(command.equals("fsappend")){
            return pre+"FSAPPEND FORMAT:BINARY NAME = "+"\n";
        }else if(command.equals("fsdelete")){
            return pre+"FSDELETE NAME = "+"\n";
        }else if(command.equals("ustatuson")){
            return pre+ "USTATUS"+"\n";
        }else if(command.equals("ustatusoff")){
            return pre+  "USTATUSOFF"+"\n";
        }else if(command.equals("rdymsg")){
            return pre+ "RDYMSG DISPLAY = "+"\n";
        }else if(command.equals("opmsg")){
            return pre+ "OPMSG DISPLAY = "+"\n";
        }else if(command.equals("stmsg")){
            return pre+ "STMSG DISPLAY = "+"\n";
        }
        else {
            return "";
        }
    }

}
