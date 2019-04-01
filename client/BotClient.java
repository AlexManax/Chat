package com.javarush.task.task30.task3008.client;


import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            super.processIncomingMessage(message);
            if (message.contains(":")) {
                ConsoleHelper.writeMessage(message);
                switch (message.split(": ")[1].trim()) {
                    case "дата":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("d.MM.YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case "день":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("d").format(Calendar.getInstance().getTime()));
                        break;
                    case "месяц":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()));
                        break;
                    case "год":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case "время":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("H:mm:ss").format(Calendar.getInstance().getTime()));
                        break;
                    case "час":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("H").format(Calendar.getInstance().getTime()));
                        break;
                    case "минуты":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("m").format(Calendar.getInstance().getTime()));
                        break;
                    case "секунды":
                        sendTextMessage("Информация для " + message.split(": ")[0] + ": " + new SimpleDateFormat("s").format(Calendar.getInstance().getTime()));
                        break;
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    public static void main(String[] args) {
        new BotClient().run();
    }
}
