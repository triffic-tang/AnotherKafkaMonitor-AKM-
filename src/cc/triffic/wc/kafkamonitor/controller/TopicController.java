package cc.triffic.wc.kafkamonitor.controller;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cc.triffic.wc.kafkamonitor.service.TopicService;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;
import cc.triffic.wc.kafkamonitor.utils.KafkaCommandUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class TopicController {
	private static final Logger LOG = LoggerFactory.getLogger(TopicController.class);

	@RequestMapping(value = { "/topic/create" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView topicCreateView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/topic/create");
		return mav;
	}

	@RequestMapping(value = { "/topic/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView topicListView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/topic/list");
		return mav;
	}

	@RequestMapping(value = { "/topic/meta/{tname}/" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView topicMetaView(@PathVariable("tname") String tname,
			HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:")
				.append((ip == null) ? request.getRemoteAddr() : ip).toString());

		ModelAndView mav = new ModelAndView();
		if (TopicService.findTopicName(tname, ip))
			mav.setViewName("/topic/topic_meta");
		else {
			mav.setViewName("/error/404");
		}

		return mav;
	}

	@RequestMapping(value = { "/topic/create/success" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView successView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/topic/add_success");
		return mav;
	}

	@RequestMapping(value = { "/topic/create/failed" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView failedView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/topic/add_failed");
		return mav;
	}

	@RequestMapping(value = { "/topic/meta/{tname}/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void topicMetaAjax(@PathVariable("tname") String tname,
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

		String str = TopicService.topicMeta(tname, ip);
		JSONArray ret = JSON.parseArray(str);
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2.hasNext();) {
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((offset < iDisplayLength + iDisplayStart)
					&& (offset >= iDisplayStart)) {
				JSONObject obj = new JSONObject();
				obj.put("topic", tname);
				obj.put("partition", tmp2.getInteger("partitionId"));
				obj.put("leader", tmp2.getInteger("leader"));
				obj.put("replicas", tmp2.getString("replicas"));
				obj.put("isr", tmp2.getString("isr"));
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

	@RequestMapping(value = { "/topic/list/table/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void topicListAjax(HttpServletResponse response,
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
		String search = "";
		for (Iterator<?> localIterator1 = jsonArray.iterator(); localIterator1
				.hasNext();) {
			Object obj = localIterator1.next();
			JSONObject jsonObj = (JSONObject) obj;
			if ("sEcho".equals(jsonObj.getString("name")))
				sEcho = jsonObj.getIntValue("value");
			else if ("iDisplayStart".equals(jsonObj.getString("name")))
				iDisplayStart = jsonObj.getIntValue("value");
			else if ("iDisplayLength".equals(jsonObj.getString("name")))
				iDisplayLength = jsonObj.getIntValue("value");
			else if ("sSearch".equals(jsonObj.getString("name"))) {
				search = jsonObj.getString("value");
			}
		}

		JSONArray ret = JSON.parseArray(TopicService.list());
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2
				.hasNext();) {
			JSONObject obj;
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((search.length() > 0)
					&& (search.equals(tmp2.getString("topic")))) {
				obj = new JSONObject();
				obj.put("id", tmp2.getInteger("id"));
				obj.put("topic",
						new StringBuilder()
								.append("<a href='/AnotherKafkaMonitor/topic/meta/")
								.append(tmp2.getString("topic"))
								.append("/' target='_blank'>")
								.append(tmp2.getString("topic")).append("</a>")
								.toString());
				obj.put("partitions",
						(tmp2.getString("partitions").length() > 50) ? new StringBuilder()
								.append(tmp2.getString("partitions").substring(
										0, 50)).append("...").toString()
								: tmp2.getString("partitions"));
				obj.put("partitionNumbers", tmp2.getInteger("partitionNumbers"));
				obj.put("created", tmp2.getString("created"));
				obj.put("modify", tmp2.getString("modify"));
				retArr.add(obj);
			} else if (search.length() == 0) {
				if ((offset < iDisplayLength + iDisplayStart)
						&& (offset >= iDisplayStart)) {
					obj = new JSONObject();
					obj.put("id", tmp2.getInteger("id"));
					obj.put("topic",
							new StringBuilder()
									.append("<a href='/AnotherKafkaMonitor/topic/meta/")
									.append(tmp2.getString("topic"))
									.append("/' target='_blank'>")
									.append(tmp2.getString("topic"))
									.append("</a>").toString());
					obj.put("partitions",
							(tmp2.getString("partitions").length() > 50) ? new StringBuilder()
									.append(tmp2.getString("partitions")
											.substring(0, 50)).append("...")
									.toString() : tmp2.getString("partitions"));
					obj.put("partitionNumbers",
							tmp2.getInteger("partitionNumbers"));
					obj.put("created", tmp2.getString("created"));
					obj.put("modify", tmp2.getString("modify"));
					retArr.add(obj);
				}
				++offset;
			}
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

	@RequestMapping(value = { "/topic/create/form" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView topicAddForm(HttpSession session,
			HttpServletResponse response, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String ke_topic_name = request.getParameter("ke_topic_name");
		String ke_topic_partition = request.getParameter("ke_topic_partition");
		String ke_topic_repli = request.getParameter("ke_topic_repli");
		Map<?, ?> map = KafkaCommandUtils.create(ke_topic_name, ke_topic_partition,
				ke_topic_repli);
		if ("success".equals(map.get("status"))) {
			session.removeAttribute("Submit_Status");
			session.setAttribute("Submit_Status", map.get("info"));
			mav.setViewName("redirect:/topic/create/success");
		} else {
			session.removeAttribute("Submit_Status");
			session.setAttribute("Submit_Status", map.get("info"));
			mav.setViewName("redirect:/topic/create/failed");
		}
		return mav;
	}
}