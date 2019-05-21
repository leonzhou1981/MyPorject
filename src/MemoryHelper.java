public class MemoryHelper {

    public static void main(String[] args) {
        System.out.println(Math.ceil(Runtime.getRuntime().totalMemory()/1024/1024));
        System.out.println(Math.ceil(Runtime.getRuntime().freeMemory()/1024/1024));

    }

}
