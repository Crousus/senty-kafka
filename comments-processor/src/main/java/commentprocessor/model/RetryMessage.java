package commentprocessor.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class RetryMessage<T> implements Serializable {
    private final long retryTime;
    private final T value;

}

