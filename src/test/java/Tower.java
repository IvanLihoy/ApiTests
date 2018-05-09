import java.util.Scanner;

public class Tower {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int b = scan.nextInt();
        String c = " ";
        int a;
        for (int i = 2; i <= b+1; i++) {
            a = b - i;
            for (int e = 0; e <= a; e++) {
                System.out.print(c);
            }
            for (int o = 0; o < i; o++) {
                System.out.print("*");
            }
            System.out.print(c);
            for (int o = 0; o < i; o++) {
                System.out.print("*");
            }
            System.out.print("\n");
        }
    }
}