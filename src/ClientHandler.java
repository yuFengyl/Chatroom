import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    Socket socket;
    Server server;
    BufferedReader in;
    PrintWriter out;
    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    @Override
    public void run() {
        try {
            while (true){
                String message = in.readLine();
                System.out.println("receive message" + message + "from" + socket.getRemoteSocketAddress());
                if (message.equals("exit")){
                    socket.close();
                    break;
                }
                server.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            server.removeClient(this);
        }
    }

    public PrintWriter getOutStream(){
        return out;
    }
}