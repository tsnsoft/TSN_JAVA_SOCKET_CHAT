package tsn_java_net_socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MyServer {

    private ServerSocket server;
    private List<ChatThread> connections = Collections.synchronizedList(new ArrayList<>());

    public MyServer() {
        try {
            server = new ServerSocket(Const.PORT);
            while (true) {
                Socket socket = server.accept();
                ChatThread con = new ChatThread(socket);
                connections.add(con);
                con.start();
                System.out.println("Сервер активен на порту: " + Const.PORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();
            synchronized (connections) {
                Iterator<ChatThread> iter = connections.iterator();
                while (iter.hasNext()) {
                    ((ChatThread) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private class ChatThread extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nikname = "";

        public ChatThread(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            try {
                nikname = in.readLine();
                synchronized (connections) {
                    Iterator<ChatThread> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((ChatThread) iter.next()).out.println(nikname + " зашел в чат!");
                    }
                }

                String str = "";
                while (true) {
                    str = in.readLine();
                    if (str.equals("exit")) {
                        break;
                    }

                    synchronized (connections) {
                        Iterator<ChatThread> iter = connections.iterator();
                        while (iter.hasNext()) {
                            ((ChatThread) iter.next()).out.println(nikname + ": " + str);
                        }
                    }
                }

                synchronized (connections) {
                    Iterator<ChatThread> iter = connections.iterator();
                    while (iter.hasNext()) {
                        ((ChatThread) iter.next()).out.println(nikname + " вышел с чата");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        public void close() {
            try {
                in.close();
                out.close();
                socket.close();
                connections.remove(this);
                if (connections.size() == 0) {
                    MyServer.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }
        }
    }
}
