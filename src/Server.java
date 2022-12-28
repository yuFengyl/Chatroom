import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    ArrayList<ClientHandler> clientList;
    static final int PORT = 5721;

    Server(){
        clientList = new ArrayList<>();
    }
    void go() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server has been created");
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("A client has been connected: " + socket.getRemoteSocketAddress());
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

    void sendMessage(String message){
        for (ClientHandler i:clientList){
            PrintWriter out = i.getOutStream();
            Thread t = new Thread(new Sender(out, message));
            t.start();
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