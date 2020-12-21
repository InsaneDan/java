package ru.geekbrains.java2.chat.server.chat;


import ru.geekbrains.java2.chat.server.chat.auth.AuthService;
import ru.geekbrains.java2.chat.server.chat.auth.BaseAuthService;
import ru.geekbrains.java2.chat.server.chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyServer {

    private final List<ClientHandler> clients = new ArrayList<>();
    private final AuthService authService;

    public MyServer() {
        this.authService = new BaseAuthService();
    }

    public void start(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер был запущен");
            runServerMessageThread();
            authService.start();
            //noinspection InfiniteLoopStatement
            while (true) {
                waitAndProcessNewClientConnection(serverSocket);
            }
        } catch (IOException e) {
            System.err.println("Failed to accept new connection");
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    private void runServerMessageThread() {
        Thread serverMessageThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String serverMessage = scanner.next();
                try {
                    broadcastMessage("Сервер: " + serverMessage, null);
                } catch (IOException e) {
                    System.err.println("failed to process serverMessage");
                    e.printStackTrace();
                }
            }
        });
        serverMessageThread.setDaemon(true);
        serverMessageThread.start();
    }

    private void waitAndProcessNewClientConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Ожидание нового подключения....");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Клиент подключился");// /auth login password
        processClientConnection(clientSocket);
    }

    private void processClientConnection(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {

// реализация пересылки ЛИЧНЫХ СООБЩЕНИЙ
        String person = "";
        String personalMessage = "";

        // если сообщение, начинается с "/w", то парсим на части
        // Note: в ClientHandler в начало сообщения добавили имя отправителя
        if (message.contains(": /w ")) {
            String[] parts = message.split(" ", 4);
            person = parts[2];
            personalMessage = parts[0] + " " + parts[3];
        }

        // пробегаем по списку клиентов
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }

            // если получатель сообщения (person) не указан - отправляем всем
            if (person.equals("")) {
                client.sendMessage(message);
            }
            // если если указан получатель и никнейм совпадает с ником из списка - отправляем (остальных пропускаем)
            else  {
                if (client.getNickname().equals(person)) {
                    client.sendMessage(personalMessage);
                }
            }
        }
    }

    public synchronized void subscribe(ClientHandler handler) {
        clients.add(handler);
    }

    public synchronized void unsubscribe(ClientHandler handler) {
        clients.remove(handler);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isNickBusy(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
}
