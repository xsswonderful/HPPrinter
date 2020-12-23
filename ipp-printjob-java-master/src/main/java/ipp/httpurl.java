package ipp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class httpurl {

    private static final String TAG = "netUtils";

    /**
     * 使用post方式登陆
     *
     * @param username
     * @param password
     * @return
     */
    public static void main(String []args){
        httpurl test=new httpurl();
        test.loginOfPost("niha","qwer1234");
    }
    public static String loginOfPost(String username, String password) {
        HttpURLConnection conn = null;
        try {
            // 创建一个URL对象
            //URL mURL = new URL("http://localhost:12001");
            URL mURL = new URL("http://192.168.223.1:9100");
            // 调用URL的openConnection()方法,获取HttpURLConnection对象
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("POST");// 设置请求方法为post
           /* conn.setReadTimeout(5000);// 设置读取超时为5秒
            conn.setConnectTimeout(10000);// 设置连接网络超时为10秒*/
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容

            int len=0;
            //String path="E:\\new\\HPPrinter\\ipp-printjob-java-master\\demo\\A4-blank.pdf";
            // post请求的参数
            byte[] buf= new byte[1024];
            //FileInputStream fis=new FileInputStream(path);
//            while((len = fis.read(buf)) != -1){
//                System.out.print(len);
//            }
//            String data = "username=" + username + "&password=" + password;
            // 获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
            OutputStream out = conn.getOutputStream();// 获得一个输出流,向服务器写数据
            out.write(27); //esc
            out.write("%-12345X@PJL\n".getBytes());
            out.write("test".getBytes());
            out.write(27); //esc
            out.write("%-12345X".getBytes());
//            out.write(data.getBytes());
            out.write(buf);
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                return state;
            } else {
//                Log.i(TAG, "访问失败" + responseCode);
                System.out.print("访问失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();// 关闭连接
            }
        }

        return null;
    }

    /**
     * 使用get方式登陆
     *
     * @param username
     * @param password
     * @return
     */
    public static String loginOfGet(String username, String password) {
        HttpURLConnection conn = null;

        String data = "username=" + URLEncoder.encode(username) + "&password="+ URLEncoder.encode(password);
        String url = "http://192.168.0.100:8080/android/servlet/LoginServlet?"+ data;
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String state = getStringFromInputStream(is);
                return state;
            } else {
//                Log.i(TAG, "访问失败" + responseCode);
                System.out.print("访问失败");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }

    /**
     * 根据流返回一个字符串信息         *
     * @param is
     * @return
     * @throws IOException
     */
    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        // 一定要写len=is.read(buffer)
        // 如果while((is.read(buffer))!=-1)则无法将数据写入buffer中
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }
}

