import java.io.IOException;
import java.net.Socket;

public class Host {
    public static void main(String[] args) {

        Socket socket=null;
        try{

            socket = new  Socket("192.168.223.1",9100);
            new Thread(new Job(socket)).start();

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
