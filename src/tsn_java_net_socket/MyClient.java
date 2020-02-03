package tsn_java_net_socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обеспечивает работу программы в режиме клиента
 *
 */
public class MyClient {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    /**
     * Запрашивает у пользователя ник и организовывает обмен сообщениями с
     * сервером
     */
    public MyClient() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Введите IP для подключения к серверу.");
        System.out.println("Формат: xxx.xxx.xxx.xxx");
        System.out.println("(локальный адрес: 127.0.0.1)");
        System.out.println("для выхода из чата наберите 'exit'");

        String ip = scan.nextLine();

        try {
            // Подключаемся в серверу и получаем потоки(in и out) для передачи
            // сообщений
            socket = new Socket(ip, Const.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Введите свой ник:");
            out.println(scan.nextLine());

            // Запускаем вывод всех входящих сообщений в консоль
            Resender resend = new Resender();
            resend.start();

            // Пока пользователь не введёт "exit" отправляем на сервер всё, что
            // введено из консоли
            String str = "";
            while (!str.equals("exit")) {
                str = scan.nextLine();
                out.println(str);
            }
            resend.setStop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * Закрывает входной и выходной потоки и сокет
     */
    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    /**
     * Класс в отдельной нити пересылает все сообщения от сервера в консоль.
     * Работает пока не будет вызван метод setStop().
     *
     */
    private class Resender extends Thread {

        private boolean stoped;

        /**
         * Прекращает пересылку сообщений
         */
        public void setStop() {
            stoped = true;
        }

        /**
         * Считывает все сообщения от сервера и печатает их в консоль.
         * Останавливается вызовом метода setStop()
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            try {
                while (!stoped) {
                    String str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }

}
