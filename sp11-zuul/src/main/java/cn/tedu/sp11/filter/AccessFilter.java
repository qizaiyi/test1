package cn.tedu.sp11.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import ch.qos.logback.core.filter.Filter;
import cn.tedu.web.util.JsonResult;

@Component
public class AccessFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
		if ("item-service".equals(serviceId)) {
			return true;
		}
		//对指定的serviceid过滤，如果要过滤所有服务，直接返回 true
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest req = ctx.getRequest();
		String token = req.getParameter("token");
		if (token==null||token.length()==0) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonResult.err("please login").toString());
		}
		// zuul过滤器返回的数据设计为以后扩展使用，
		// 目前该返回值没有被使用
		return null;
	}

	@Override
	public String filterType() {
		// 过滤器类型,前置
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// 大于5,因为服务id在第5个过滤器才存在
		return 6;
	}

}
