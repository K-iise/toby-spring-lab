package hello.springbook.user.sqlservice.exception;

public class SqlRetrievalFailureException extends RuntimeException{
    public SqlRetrievalFailureException(String message){
        super(message);
    }

    public SqlRetrievalFailureException(String message, Throwable cause){
        super(message, cause);
    }

    public SqlRetrievalFailureException(Throwable cause) {super(cause);}
}
