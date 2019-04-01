package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> pair : Server.connectionMap.entrySet()) {
            try {
                pair.getValue().send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage(pair.getKey() + " - message wasn't sent");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int x = ConsoleHelper.readInt();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(x);
            ConsoleHelper.writeMessage(serverSocket.toString() + " - Server is On");

            while (!serverSocket.isClosed()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                new Handler(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            serverSocket.close();
            ConsoleHelper.writeMessage(e.getMessage());
        }
    }


    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message request = new Message(MessageType.NAME_REQUEST);
            Message answer;
            do {
                connection.send(request);
                answer = connection.receive();
            }
            while (answer.getType() != MessageType.USER_NAME || answer.getData().isEmpty() || connectionMap.containsKey(answer.getData()));
            connectionMap.put(answer.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return answer.getData();
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : Server.connectionMap.entrySet()) {
                if (!pair.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message receive = connection.receive();
                if (receive.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + receive.getData()));
                } else
                    ConsoleHelper.writeMessage("Message error!");
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage(socket.getRemoteSocketAddress().toString());
            try (Connection connection = new Connection(socket)) {
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            } catch (
                    IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error");
            }


        }
    }
}