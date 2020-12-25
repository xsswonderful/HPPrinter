package test;

//package test;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

class Variable implements Runnable {
    Socket socket = null;
    File f=null;

    public Variable(Socket socket,File f) {
        this.socket = socket;

        this.f=f;

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
            while(true) {
                doRead(in);
            }

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


    public boolean doWrite(OutputStream out){

        try {

            byte[] bytes = Files.readAllBytes(f.toPath());

            out.write(27); //esc
            out.write("%-12345X@PJL\r\n".getBytes());


            out.write("@PJL USTATUS JOB = ON \r\n".getBytes());

            //out.write(bytes);
            out.write(27); //esc
            out.write("%-12345X@PJL\n".getBytes());
            out.write("@PJL SET DUPLEX=ON \n".getBytes());
            out.write("@PJL ENTER LANGUAGE=PDF\n".getBytes());
            out.write(bytes);
            out.write(27); //esc
            out.write("%-12345X".getBytes());

            out.write(27); //esc
            out.write("%-12345X@PJL\r\n".getBytes());
            out.write("@PJL EOJ \r\n".getBytes());
            out.write(27); //esc

            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }



}
