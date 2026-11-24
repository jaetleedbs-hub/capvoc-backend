package buttle.apptpl.common.apiclient.base;

import java.util.concurrent.CompletableFuture;

public interface HostSocketHandler<T,R> {

    R request(T request);

    CompletableFuture<R> requestAsync(T request);

    public void init();

}
