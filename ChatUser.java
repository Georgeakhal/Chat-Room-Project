package ChatPackage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatUser {
    private static int amount;
    private String name;
    private int id;
    private Socket userSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public ChatUser(Socket userSocket) throws IOException {
        amount ++;
        setId(amount);
        this.userSocket = userSocket;
        this.in = new DataInputStream(new BufferedInputStream(userSocket.getInputStream()));
    }


    public void writeMessage(String line, ArrayList<ChatUser> members, boolean send2CurrentUser){
        if (send2CurrentUser){
            for (ChatUser member : members){
                try {
                    this.out = new DataOutputStream(member.getUserSocket().getOutputStream());
                    out.writeUTF(line);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            for (ChatUser member : members){
                try {
                    if (getId() != member.getId()){
                        this.out = new DataOutputStream(member.getUserSocket().getOutputStream());
                        out.writeUTF(line);
                    } else{
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void writeMessage(String line, ArrayList<ChatUser> members, String name){
        for (ChatUser member : members){
            try {
                if (name.equals(member.getName())){
                    this.out = new DataOutputStream(member.getUserSocket().getOutputStream());
                    out.writeUTF(line);
                    break;
                } else{
                    continue;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void readMessages(ArrayList<ChatUser> members) throws IOException {
        writeMessage("[ " + getName() + " has Connected!] Members: " + members.size(), members, true);
        String line = "";

        while(!line.equals("/close")){
            try {
                line = in.readUTF();
            } catch (IOException e) {
                break;
            }
            line = lineChecker(line, members);
        }
        writeMessage("[ " + getName() + " has left this chat ] Members: " + members.size(), members, false);
        System.out.println("[ " + getName() + " has left this chat ]");
    }

    public String lineChecker(String line, ArrayList<ChatUser> members){
        if (command(line)[0].equalsIgnoreCase("/setName") & command(line).length == 2){
            String oldName = getName();
            setName(command(line)[1]);
            line = "[ " + oldName + " changed name to: " + getName() + " ]";
            writeMessage(line, members, true);
            System.out.println(line);
            return line;
        } else if (command(line)[0].equalsIgnoreCase("/sendTo") & command(line).length == 3) {
            String name = command(line)[1];
            line = "Private Message from: " + getName() + ": " + command(line)[2];
            writeMessage(line, members, name);
        } else {
            line = getName() + ": " + line;
            writeMessage(line, members, false);
            System.out.println(line);
            return line;
        }
        return "Something went wrong";
    }

    public void close() throws IOException {
        userSocket.close();
        in.close();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public void setUserSocket(Socket userSocket) {
        this.userSocket = userSocket;
    }

    public String[] command(String line){
        String[] text = line.split("-");
        return text;
    }
}
