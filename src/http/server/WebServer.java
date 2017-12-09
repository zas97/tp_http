///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * <p>
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {


    String content;
    String head;

    /**
     * WebServer constructor.
     */
    protected void start() {
        content = "<!doctype html>\n" +
                "<html>\n" +
                "\n" +
                "\t<head>\n" +
                "\t\t<title>Adding two numbers</title>\n" +
                "\t</head>\n" +
                "\n" +
                "\t<body>\n" +
                "\t\tEnter First Number: <input id=\"first\">\n" +
                "\t\tEnter Second Number: <input id=\"second\">\n" +
                "\t\t<button onclick=\"add()\">Add</button>\n" +
                "\t\tResult is: <input id=\"answer\">\n" +
                "\n" +
                "\t\t<script>\n" +
                "\t\t\tfunction add(){\n" +
                "\t\t\tvar a,b,c;\n" +
                "\t\t\ta=Number(document.getElementById(\"first\").value);\n" +
                "\t\t\tb=Number(document.getElementById(\"second\").value);\n" +
                "\t\t\tc= a + b;\n" +
                "\t\t\tdocument.getElementById(\"answer\").value= c;\n" +
                "\t\t\t}\n" +
                "\t\t</script>\n" +
                "\n" +
                "\t</body>\n" +
                "\n" +
                "</html>";
        ServerSocket s;

        System.out.println("Webserver starting up on port 80");
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(2000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                // read the data sent. We basically ignore it,
                // stop reading once a blank line is hit. This
                // blank line signals the end of the client HTTP
                // headers.
                String str = ".";
                String request = "";
                boolean done = false;
                while (!done) {
                    str = in.readLine();
                    if (!str.equals(".")) {
                        String[] parts = str.split(" ");
                        request = parts[0];
                        System.out.println(request);

                        switch (request) {
                            case "GET":
                                while (!str.equals("")) {
                                    str = in.readLine();
                                }
                                break;

                            case "PUT":

                                RequestPut(in);

                                break;


                            case "POST":

                                RequestPost(in);
                                break;

                            case "HEAD":
                                while (!str.equals("")) {
                                    str = in.readLine();
                                }
                                RequestHead(out);
                                break;

                            case "DELETE":
                                while (!str.equals("")) {
                                    str = in.readLine();
                                }
                                RequestDelete();
                                break;



                            default:
                                break;
                        }
                        RequestGet(out);
                        done = true;

                    }
                }




                out.flush();
                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    public void RequestGet(PrintWriter out) {
        // Send the response
        out.println("HTTP/1.0 200 OK");
        // Send the headers
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        out.println(content);

    }

    public void RequestPost(BufferedReader in) {
        try {
            String line = in.readLine();
            String type="";
            while (!line.equals("")) {
                System.out.println(line);
                String parts[] = line.split(" ");
                if(parts[0].equals("content-type:")){
                    type = parts[1];
                }
                line = in.readLine();
            }
            switch(type){
                case "url":
                    String url = in.readLine();
                    content += "<img src=\"" +url +"\"> \n";
                    System.out.println(content);
                    break;
                default:
                    line = in.readLine();
                    while (!line.equals("")){
                        System.out.println(line);
                        content += "<H1>" + line + "</H2>" + "\n";
                        line = in.readLine();
                    }
                    break;
            }


        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void RequestDelete(){
        content = "";

    }

    public void RequestPut(BufferedReader in){
        RequestDelete();
        RequestPost(in);
    }

    public void RequestHead(PrintWriter out){
        // Send the response
        out.println("HTTP/1.0 200 OK");
        // Send the headers
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Non capturing open tag. Non-capturing mean it won't be included in result when we match it against some text.
        System.out.println(getHead());
        out.println(getHead());

    }
    private String getHead(){
        // Non capturing open tag. Non-capturing mean it won't be included in result when we match it against some text.
        String open = "(?<=\\{\\[head\\]\\})";

        // Content between open and close tag.
        String inside = ".*?";

        // Non capturing close tag.
        String close = "(?=\\{\\[/head\\]\\})";
        // Final regex
        String regex = open + inside + close;

        // Usage
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(content);

        return matcher.group().trim();

    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
