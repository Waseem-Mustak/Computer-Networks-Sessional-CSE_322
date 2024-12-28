import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;

public class Server implements Runnable{
    private Socket socket;
    FileWriter fileWriter;
    public Server(Socket socket) throws IOException {
        this.socket=socket;
        this.fileWriter=new FileWriter("log.txt",true);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(5091);
        System.out.println("Server running");
        serverSocket.accept();
        System.out.println("Ac");

        while (true)
        {
            Socket socketTem=serverSocket.accept();
            Server server=new Server(socketTem);
            Thread thread=new Thread(server);
            thread.start();
        }
    }


    @Override
    public void run() {
        try {
//            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            InputStream inputStream=socket.getInputStream();
            StringBuilder stringBuilder=new StringBuilder();
            int bytes;
            while (true)
            {
                bytes=inputStream.read();
                if(bytes==-1)break;
                char ch=(char) bytes;
                if(ch=='\n')break;
                else stringBuilder.append(ch);
            }
            String input=stringBuilder.toString().trim();
            DataInputStream dataInputStream=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            PrintWriter printWriter=new PrintWriter(socket.getOutputStream());
//            String input=bufferedReader.readLine();
            System.out.println("Request from client: "+input);

            OutputStream outputStream=socket.getOutputStream();

            if(input==null)System.out.println("Input file is null");
            else{

                if(input.startsWith("GET"))
                {
                    String responseString="<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                            "\t\t<link rel=\"icon\" href=\"data:,\"></head><br>\n";
                    responseString+="<body><ul>\n";
                    String[] str=input.split(" ");
                    String str3=str[1].substring(1);
                    File directory=new File(str3);
                    System.out.println(str3);
                    System.out.println(directory);

                    if(str3=="")
                    {
                        responseString+="<li><b><i><a href=\" \\" + "uploaded" + "\">" + "uploaded" + "</a></i></b></li><br>\n";
                        responseString +="</ul></body></html>";
                        String string="";
                        string+="HTTP/1.0 200 OK\r\n";
                        string+="Server: Java HTTP Server: 1.0\r\n";
                        string+="Date: " + new Date() + "\r\n";
                        string+="Content-Length: " + responseString.length() + "\r\n";
                        string+="Content-Type: text/html\r\n";
                        string+="\r\n";
                        String stringForFileWrite="\r\n";
                        stringForFileWrite+=string;
                        stringForFileWrite+="\r\n";
                        string+=responseString;
                        outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();

                        fileWriter.write(input);
                        fileWriter.write(stringForFileWrite);

                    }
                    else if (directory.exists())
                    {
                        if (directory.isDirectory())
                        {
                            File[] files=directory.listFiles();
                            for(File f:files)
                            {
                                if (f.exists())
                                {
                                    if (f.isDirectory())
                                        responseString+="<li><b><i><a href=\" \\" + f.getPath() + "\">" + f.getName() + "</a></i></b></li><br>\n";
                                    else
                                        responseString+="<li><a href=\" \\" + f.getPath() + "\">" + f.getName() + "</a></li><br>\n";
                                }
                            }

                            responseString +="</ul></body></html>";
                            String string="";
                            string+="HTTP/1.0 200 OK\r\n";
                            string+="Server: Java HTTP Server: 1.0\r\n";
                            string+="Date: " + new Date() + "\r\n";
                            string+="Content-Length: " + responseString.length() + "\r\n";
                            string+="Content-Type: text/html\r\n";
                            string+="\r\n";
                            String stringForFileWrite="\r\n";
                            stringForFileWrite+=string;
                            stringForFileWrite+="\r\n";
                            string+=responseString;
                            outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                            outputStream.flush();

                            fileWriter.write(input);
                            fileWriter.write(stringForFileWrite);

                        } else {
                            String tem="";
                            String[] splittedString=str3.split("\\.");
                            tem=splittedString[1];
                            if(tem.equals("txt"))
                            {
                                File file = new File(directory.getPath());
                                if (file.exists() && file.isFile()) {
                                    BufferedReader reader = new BufferedReader(new FileReader(file));
                                    StringBuilder responseString1 = new StringBuilder();
                                    String line;

                                    while ((line = reader.readLine()) != null) {
                                        responseString1.append(line).append("\n");
                                    }

                                    String string="";
                                    string+="HTTP/1.0 200 OK\r\n";
                                    string+="Server: Java HTTP Server: 1.0\r\n";
                                    string+="Date: " + new Date() + "\r\n";
                                    string+="Content-Length: " + responseString1.length() + "\r\n";
                                    string+="Content-Type: text/plain\r\n";
                                    string+="\r\n";
                                    String stringForFileWrite="\r\n";
                                    stringForFileWrite+=string;
                                    stringForFileWrite+="\r\n";
                                    string+=(responseString1.toString());
                                    outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                                    outputStream.flush();

                                    fileWriter.write("Request: " + input + "\r\n");
                                    fileWriter.write(stringForFileWrite);
                                    fileWriter.flush();

                                    reader.close();
                                }

                            }
                            else if (tem.equals("jpg")  || tem.equals("png") ||tem.equals("jpeg") || tem.equals("gif") || tem.equals("bmp") ) {

                                File file = new File(directory.getPath());
                                if (file.exists() && file.isFile()) {
                                    byte[] fileBytes = Files.readAllBytes(file.toPath());


                                    String string="";
                                    string+="HTTP/1.0 200 OK\r\n";
                                    string+="Server: Java HTTP Server: 1.0\r\n";
                                    string+="Date: " + new Date() + "\r\n";
                                    string+="Content-Length: " + fileBytes.length + "\r\n";
                                    string+="Content-Type: image/png\r\n";
                                    string+="Content-Transfer-Encoding: binary\r\n";
                                    string+="\r\n";
                                    String stringForFileWrite="\r\n";
                                    stringForFileWrite+=string;
                                    stringForFileWrite+="\r\n";
                                    outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                                    outputStream.write(fileBytes);
                                    outputStream.flush();
                                    fileWriter.write("Request: " + input + "\r\n");
                                    fileWriter.write(stringForFileWrite);
                                    fileWriter.flush();
                                }


                            } else {
                                long fileSize=directory.length();
                                int length;
                                byte[] buffer=new byte[4096];
                                String mime=Files.probeContentType(directory.toPath());
                                try {
                                    FileInputStream fileInputStream=new FileInputStream(directory);
                                    DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());


                                    String string="";
                                    string+="HTTP/1.0 200 OK\r\n";
                                    string+="Server: Java HTTP Server: 1.0\r\n";
                                    string+="Date: " + new Date() + "\r\n";
                                    string+="Content-Length: " + fileSize + "\r\n";
                                    string+="Content-Type: " + mime + "\r\n";
                                    string+="Content-Transfer-Encoding: binary\r\n";
                                    string+="Content-Disposition: attachment; filename=\"" + directory.getName() + "\"\r\n";
                                    string+="\r\n";
                                    String stringForFileWrite="\r\n";
                                    stringForFileWrite+=string;
                                    stringForFileWrite+="\r\n";
                                    outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                                    outputStream.flush();



                                    fileWriter.write("\r\n");
                                    fileWriter.write("Request: " + input + "\r\n");
                                    fileWriter.write(stringForFileWrite);
                                    fileWriter.write("\r\n");
                                    while ((length = fileInputStream.read(buffer)) > 0) {
                                        dataOutputStream.write(buffer, 0, length);
                                    }
                                    dataOutputStream.flush();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        System.out.println("404: Page not found.");
                        responseString += "</ul><p>404: PAGE NOT FOUND.<br>Request with a valid directory.</p></body></html>";
                        String string="";
                        string+="HTTP/1.0 404 PAGE NOT FOUND\r\n";
                        string+="Server: Java HTTP Server: 1.0\r\n";
                        string+="Date: " + new Date() + "\r\n";
                        string+="Content-Length: " + responseString.length() + "\r\n";
                        string+="Content-Type: null\r\n";
                        string+="\r\n";
                        String stringForFileWrite="\r\n";
                        stringForFileWrite+=string;
                        stringForFileWrite+="\r\n";
                        string+=(responseString.toString());
                        outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();

                        fileWriter.write("\r\n");
                        fileWriter.write("Request: " + input + "\r\n");
                        fileWriter.write(stringForFileWrite);
                        fileWriter.write("\r\n");
                    }
                }else if (input.startsWith("UPLOAD")) {
                    try {

                        String[] str = input.split(" ");
                        byte[] buffer = new byte[4096];
                        File file = new File("uploaded/" + str[1]);
                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                        int length;
                        while ((length=dataInputStream.read(buffer, 0, 4096)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (input.startsWith("NOTFOUND")) {
                    System.out.println("File Not Found!");
                }
            }
            socket.close();
        }
        catch (IOException e){
            System.err.println("Server Connection error :"+e.getMessage());
        }
    }
}
