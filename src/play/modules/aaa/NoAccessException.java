package play.modules.aaa;

public class NoAccessException extends RuntimeException {

   private static final long serialVersionUID = 1080270792010078044L;

   public NoAccessException(Throwable cause) {
      super(cause);
   }

   public NoAccessException(String message) {
      super(message);
   }
   
   public NoAccessException(String message, Throwable cause) {
      super(message, cause);
   }

}
