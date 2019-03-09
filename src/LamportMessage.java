public class LamportMessage {
    private String message;
    private int tStamp;
    private int id;

    LamportMessage(String message, int tStamp, int id) {
        this.message = message;
        this.tStamp = tStamp;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public int gettStamp() {
        return tStamp;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return message + " " + tStamp + " " + id;
    }

    public static LamportMessage toLamportMessage(String string) {
        String[] seperated = string.split(" ");
        try {
            if (seperated.length != 3) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new LamportMessage(seperated[0], Integer.parseInt(seperated[1]), Integer.parseInt(seperated[2]));
    }
}
