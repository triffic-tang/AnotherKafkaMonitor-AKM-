package cc.triffic.wc.kafkamonitor.controller;

import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cc.triffic.wc.kafkamonitor.service.OffsetService;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class OffsetController {
	private static final Logger LOG = LoggerFactory.getLogger(OffsetController.class);

	@RequestMapping(value = { "/consumers/offset/{group}/{topic}/" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView activeConsumersView(
			@PathVariable("group") String group,
			@PathVariable("topic") String topic, HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		ModelAndView mav = new ModelAndView();
		if (OffsetService.isGroupTopic(group, topic, ip))
			mav.setViewName("/consumers/offset_consumers");
		else {
			mav.setViewName("/error/404");
		}
		return mav;
	}

	@RequestMapping(value = { "/consumers/offset/{group}/{topic}/realtime" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView offsetsRealtimeView(
			@PathVariable("group") String group,
			@PathVariable("topic") String topic, HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		ModelAndView mav = new ModelAndView();
		if (OffsetService.isGroupTopic(group, topic, ip))
			mav.setViewName("/consumers/offset_realtime");
		else {
			mav.setViewName("/error/404");
		}
		return mav;
	}

	@RequestMapping(value = { "/consumer/offset/{group}/{topic}/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void offsetDetailAjax(@PathVariable("group") String group,
			@PathVariable("topic") String topic, HttpServletResponse response,
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
		for (Iterator<?> localIterator1 = jsonArray.iterator(); localIterator1
				.hasNext();) {
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

		JSONArray ret = JSON.parseArray(OffsetService.getLogSize(topic, group,
				ip));
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2.hasNext();) {
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((offset < iDisplayLength + iDisplayStart)
					&& (offset >= iDisplayStart)) {
				JSONObject obj = new JSONObject();
				obj.put("partition", tmp2.getInteger("partition"));
				if (tmp2.getLong("logSize").longValue() == 0L)
					obj.put("logsize",
							"<a class='btn btn-warning btn-xs'>0</a>");
				else {
					obj.put("logsize", tmp2.getLong("logSize"));
				}
				if (tmp2.getLong("offset").longValue() == -1L)
					obj.put("offset", "<a class='btn btn-warning btn-xs'>0</a>");
				else {
					obj.put("offset",
							new StringBuilder()
									.append("<a class='btn btn-success btn-xs'>")
									.append(tmp2.getLong("offset"))
									.append("</a>").toString());
				}
				obj.put("lag",
						new StringBuilder()
								.append("<a class='btn btn-danger btn-xs'>")
								.append(tmp2.getLong("lag")).append("</a>")
								.toString());
				obj.put("owner", tmp2.getString("owner"));
				obj.put("created", tmp2.getString("create"));
				obj.put("modify", tmp2.getString("modify"));
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
			response.setContentLength((output == null) ? "NULL".toCharArray().length
					: output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/consumer/offset/{group}/{topic}/realtime/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void offsetGraphAjax(@PathVariable("group") String group,
			@PathVariable("topic") String topic, HttpServletResponse response,
			HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:")
				.append((ip == null) ? request.getRemoteAddr() : ip).toString());
		try {
			byte[] output = GzipUtils.compressToByte(OffsetService.getOffsetsGraph(group, topic));
			response.setContentLength((output == null) ? "NULL".toCharArray().length : output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}