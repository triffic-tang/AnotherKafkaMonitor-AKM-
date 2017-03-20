package cc.triffic.wc.kafkamonitor.controller;

import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cc.triffic.wc.kafkamonitor.service.ConsumerService;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class ConsumersController {
	private static final Logger LOG = LoggerFactory.getLogger(ConsumersController.class);

	@RequestMapping(value = { "/consumers" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView consumersView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/consumers/consumers");
		return mav;
	}

	@RequestMapping(value = { "/consumers/offset" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView consumersOffsetView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/consumers/offset_consumers");
		return mav;
	}

	@RequestMapping(value = { "/consumers/realtime" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView consumersRealtimeView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/consumers/offset_realtime");
		return mav;
	}

	@RequestMapping(value = { "/consumers/info/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void consumersAjax(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:")
				.append((ip == null) ? request.getRemoteAddr() : ip).toString());
		try {
			byte[] output = GzipUtils.compressToByte(ConsumerService
					.getActiveTopic());
			response.setContentLength(output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/consumer/list/table/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void consumerListAjax(HttpServletResponse response,
			HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:")
				.append((ip == null) ? request.getRemoteAddr() : ip).toString());

		String aoData = request.getParameter("aoData");
		JSONArray jsonArray = JSON.parseArray(aoData);
		int sEcho = 0;
		int iDisplayStart = 0;
		int iDisplayLength = 0;
		for (Iterator<?> localIterator1 = jsonArray.iterator(); localIterator1.hasNext();) {
			Object obj = localIterator1.next();
			JSONObject jsonObj = (JSONObject) obj;
			if ("sEcho".equals(jsonObj.getString("name")))
				sEcho = jsonObj.getIntValue("value");
			else if ("iDisplayStart".equals(jsonObj.getString("name")))
				iDisplayStart = jsonObj.getIntValue("value");
			else if ("iDisplayLength".equals(jsonObj.getString("name"))) {
				iDisplayLength = jsonObj.getIntValue("value");
			}
		}

		JSONArray ret = JSON.parseArray(ConsumerService.getConsumer());
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2.hasNext();) {
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((offset < iDisplayLength + iDisplayStart)
					&& (offset >= iDisplayStart)) {
				JSONObject obj = new JSONObject();
				obj.put("id", tmp2.getInteger("id"));
				obj.put("group", new StringBuilder().append("<a class='link' href='#")
								.append(tmp2.getString("group")).append("'>")
								.append(tmp2.getString("group")).append("</a>")
								.toString());
				obj.put("topic", (tmp2.getString("topic").length() > 50) ? new StringBuilder()
								.append(tmp2.getString("topic")
										.substring(0, 50)).append("...")
								.toString() : tmp2.getString("topic"));
				obj.put("consumerNumber", tmp2.getInteger("consumerNumber"));
				retArr.add(obj);
			}
			++offset;
		}

		JSONObject obj = new JSONObject();
		obj.put("sEcho", Integer.valueOf(sEcho));
		obj.put("iTotalRecords", Integer.valueOf(ret.size()));
		obj.put("iTotalDisplayRecords", Integer.valueOf(ret.size()));
		obj.put("aaData", retArr);
		try {
			byte[] output = GzipUtils.compressToByte(obj.toJSONString());
			response.setContentLength(output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/consumer/{group}/table/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void consumerDetailListAjax(@PathVariable("group") String group,
			HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:")
				.append((ip == null) ? request.getRemoteAddr() : ip).toString());

		String aoData = request.getParameter("aoData");
		JSONArray jsonArray = JSON.parseArray(aoData);
		int sEcho = 0;
		int iDisplayStart = 0;
		int iDisplayLength = 0;
		for (Iterator<?> localIterator1 = jsonArray.iterator(); localIterator1.hasNext();) {
			Object obj = localIterator1.next();
			JSONObject jsonObj = (JSONObject) obj;
			if ("sEcho".equals(jsonObj.getString("name")))
				sEcho = jsonObj.getIntValue("value");
			else if ("iDisplayStart".equals(jsonObj.getString("name")))
				iDisplayStart = jsonObj.getIntValue("value");
			else if ("iDisplayLength".equals(jsonObj.getString("name"))) {
				iDisplayLength = jsonObj.getIntValue("value");
			}
		}

		JSONArray ret = JSON.parseArray(ConsumerService.getConsumerDetail(group, ip));
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2.hasNext();) {
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((offset < iDisplayLength + iDisplayStart)
					&& (offset >= iDisplayStart)) {
				JSONObject obj = new JSONObject();
				String topic = tmp2.getString("topic");
				obj.put("id", tmp2.getInteger("id"));
				obj.put("topic", topic);
				if (tmp2.getBoolean("isConsumering").booleanValue())
					obj.put("isConsumering",
							new StringBuilder()
									.append("<a href='/AnotherKafkaMonitor/consumers/offset/")
									.append(group)
									.append("/")
									.append(topic)
									.append("/' target='_blank' class='btn btn-success btn-xs'>Running</a>")
									.toString());
				else {
					obj.put("isConsumering",
							new StringBuilder()
									.append("<a href='/AnotherKafkaMonitor/consumers/offset/")
									.append(group)
									.append("/")
									.append(topic)
									.append("/' target='_blank' class='btn btn-danger btn-xs'>Pending</a>")
									.toString());
				}
				retArr.add(obj);
			}
			++offset;
		}

		JSONObject obj = new JSONObject();
		obj.put("sEcho", Integer.valueOf(sEcho));
		obj.put("iTotalRecords", Integer.valueOf(ret.size()));
		obj.put("iTotalDisplayRecords", Integer.valueOf(ret.size()));
		obj.put("aaData", retArr);
		try {
			byte[] output = GzipUtils.compressToByte(obj.toJSONString());
			response.setContentLength(output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}