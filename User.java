package ChatPackage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class User {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner sc;

    public User() throws IOException {
        this.sc = new Scanner(System.in);
        connect();
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writeMessages();
        readMessages();
    }

    public void readMessages(){
        new Thread(()->{
            String line = "";
            while(!line.equals("/close")){
                try {
                    line = in.readUTF();
                    System.out.println(line);
                } catch (IOException e) {
                    break;
                }
            }
        }).start();
    };
    public void writeMessages() throws IOException {
        new Thread(()->{
            String line = "";
            while(!line.equals("/close")){
                line = sc.nextLine();
                try {
                    out.writeUTF(line);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                close();
                User user = new User();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void close() throws IOException {
        socket.close();
        out.close();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void connect(){
        while (true){

            System.out.print("Please enter Host and Port to join the server: ");
            String input = sc.nextLine();
            try {
                String[] string = input.split(":");
                setSocket(new Socket(string[0], Integer.parseInt(string[1])));
                break;
            } catch (Exception e){
                System.out.println(input + " wasn't Right input, please try again.");
            }

        }
    }

    public static void main(String[] args) throws IOException {
        User user = new User();
    }
}
