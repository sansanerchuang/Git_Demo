public class Test2{

    public static void main(String[] args) {
        Ticket12306 ticket12306 = new Ticket12306();
        new Thread(ticket12306, "携程").start();
        new Thread(ticket12306, "飞猪").start();
        
    }
}