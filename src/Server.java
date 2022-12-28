import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Runnable{
    ArrayList<ClientHandler> clientList;
    static final int PORT = 5721;

    final Object lock;

    Queue<String> messageQueue;

    Server(){
        clientList = new ArrayList<>();
        messageQueue = new LinkedList<>();
        lock = new Object();
    }
    void go() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server has been created");
        Thread sender = new Thread(this);
        sender.start();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("A client has been connected: " + socket.getRemoteSocketAddress());
                ClientHandler c = new ClientHandler(socket, this);
                clientList.add(c);
                Thread t = new Thread(c);
                t.start();
            }
        } finally {
            serverSocket.close();
        }
    }
    void removeClient(ClientHandler c){
        clientList.remove(c);
    }

    void addMessage(String message){
        synchronized (lock){
            messageQueue.offer(message);
            lock.notify();
        }
    }

    @Override
    public void run() {
        while(true){
            synchronized(lock) {
                while (messageQueue.isEmpty()) {
                    try {
                        lock.wait();
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
            String message;
            synchronized (lock){
                message = messageQueue.poll();
            }
            for (ClientHandler i:clientList){
                PrintWriter out = i.getOutStream();
                Thread t = new Thread(new Sender(out, message));
                t.start();
            }
        }
    }
}

class Sender implements Runnable{
    PrintWriter out;
    String message;
    Sender(PrintWriter out, String message){
        this.out = out;
        this.message = message;
    }
    @Override
    public void run() {
        out.println(message);
    }
}