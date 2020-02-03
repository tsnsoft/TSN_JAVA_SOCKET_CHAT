package tsn_java_net_socket;

import static java.lang.System.exit;
import java.util.Scanner;

/**
 * Стартовая точка программы. Содержит единственный метод main
 *
 */
public class Start {

    /**
     * Спрашивает пользователя о режиме работы (сервер или клиент) и передает
     * управление соответствующему классу
     *
     * @param args параметры командной строки
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Запустить программу в режиме сервера или клиента? (s(erver) / c(lient)) / q(uit)");
        while (true) {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            switch (answer) {
                case 's':
                    System.out.println("Запуск сервера ...");
                    new MyServer();
                    break;
                case 'c':
                    System.out.println("Запуск клиента ...");
                    new MyClient();
                    break;
                case 'q':
                    exit(0);
                default:
                    System.out.println("Некорректный ввод. Повторите.");
                    break;
            }
        }
    }

}
