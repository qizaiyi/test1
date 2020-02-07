package cn.tedu.sp11.fallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import cn.tedu.web.util.JsonResult;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemServiceFallback implements FallbackProvider {
	// 星号和null都表示所有微服务失败都应用当前降级类
	// "*"; //null;
	@Override
	public String getRoute() {
		// 当执行item-service失败，
		// 应用当前这个降级类
		return "item-service";
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		return response();
	}

	private ClientHttpResponse response() {
		return new ClientHttpResponse() {

			@Override
			public HttpHeaders getHeaders() {
				//设置content-type application/json
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				return headers;
			}

			@Override
			public InputStream getBody() throws IOException {
				log.info("fallback body");
				//JsonResult获取json格式字符串
				String json = JsonResult.err("获取商品后台服务失败").toString();
				//将json转换成流
				return new ByteArrayInputStream(json.getBytes("UTF-8"));
			}

			@Override
			public String getStatusText() throws IOException {
				return HttpStatus.OK.getReasonPhrase(); // "OK"
			}

			@Override
			public HttpStatus getStatusCode() throws IOException {
				return HttpStatus.OK; // 200 "OK"
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return HttpStatus.OK.value(); // 200
			}

			@Override
			public void close() {
			}
		};
	}

}
