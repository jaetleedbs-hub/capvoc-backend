package buttle.apptpl.common.apiclient.base;

import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebClientHandler {
    
    protected WebClient webClient;
    public WebClientHandler(WebClient webClient) {
        if( webClient == null ) {
            log.warn("지원하지 않는 Interface [{}] 입니다. ", webClient);
        } else {
            this.webClient = webClient;
        }
    }


    public <R> R post(String url, Class<R> responseType) {
        return post(url, null, null, responseType);
    }

    /**
     * POST 요청
     * @param <R>
     * @param url
     * @param request
     * @param responseType
     * @return
     */
    public <R, T> R post(String url, T request, Class<R> responseType) {
        return post(url, null, request, responseType);
    }
    /**
     * POST 요청
     * @param <R>
     * @param url
     * @param request
     * @param responseType
     * @return
     */
    public <R, T> R post(String url, Map<String, String> headers, T request, Class<R> responseType) {
        if( webClient == null ) {
            log.warn("지원하지 않는 Interface [{}] 입니다. ", url);
            return null;
        }

        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        headers.forEach((key, value) -> {
                            httpHeaders.add(key, value);
                        });
                    }
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * GET 요청
     * @param <R>
     * @param url
     * @param responseType
     * @return
     */
    public <R> R get(String url, Class<R> responseType) {
        return get(url, null, responseType);
    }

    /**
     * GET 요청
     * @param <R>
     * @param url
     * @param headers
     * @param responseType
     * @return
     */
    public <R> R get(String url, Map<String, String> headers, Class<R> responseType) {

        if( webClient == null ) {
            log.warn("지원하지 않는 Interface Adapter[{}] 입니다. ", url);
            return null;
        }

        return webClient.get()
                .uri(url)
                .headers(httpHeaders -> {
                    if (headers != null) {
                        headers.forEach((key, value) -> {
                            httpHeaders.add(key, value);
                        });
                    }
                })
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

}
