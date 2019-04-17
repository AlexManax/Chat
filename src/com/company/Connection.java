package com.company;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private  final Socket socket;
    private  final ObjectOutputStream out;
    private  final ObjectInputStream in;

    public Connection(Socket socket)  {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            Error er = new Error(e);
            throw er;
        }
    }

    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (in) {
            return (Message) in.readObject();
        }
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public void close() throws IOException {
        try {
            socket.close();
        } catch (Throwable th){}

        try {
            out.close();
        } catch (Throwable th){}

        try {
            in.close();
        } catch (Throwable th){}
    }

}
