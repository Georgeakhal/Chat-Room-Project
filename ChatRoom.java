package ChatPackage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Scanner;


public class ChatRoom {
    private ArrayList<ChatUser> members = new ArrayList<>();
    private ServerSocket server;


    public ChatRoom(int port) throws IOException {
        this.server = new ServerSocket(port);
        System.out.println("This Chat Room is running on Port: " + port);
        while (true){
            initConnections();
        }
    }

    public void initConnections() throws IOException {
        if (members.size() < 50){
            Socket userSocket = server.accept();
            ChatUser user = new ChatUser(userSocket);

            if(userSocket.isConnected()){
                members.add(user);
                new Thread(()->{
                    try {
                        user.setName("client #" + user.getId());
                        System.out.println("[ " + user.getName() + " has Connected! ]");
                        user.readMessages(members);
                        members.remove(user);
                        user.close();
                    } catch (IOException e) {
                        System.out.println("[ " + user.getName() + " has left this chat ]");
                    }
                }).start();

            }
        }
    }

    public static void main(String[] args) throws IOException {
        while (true){
            System.out.print("Enter port to start the Server: ");
            Scanner sc = new Scanner(System.in);
            try {
                int port = sc.nextInt();
                ChatRoom chatRoom = new ChatRoom(port);
                break;
            } catch (Exception e){
                System.out.println(sc.nextLine() + " wasn't Right input, please try again.");
            }

        }
    }
}
