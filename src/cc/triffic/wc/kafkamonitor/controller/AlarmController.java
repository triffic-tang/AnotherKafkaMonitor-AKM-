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

import cc.triffic.wc.kafkamonitor.domain.AlarmDomain;
import cc.triffic.wc.kafkamonitor.service.AlarmService;
import cc.triffic.wc.kafkamonitor.utils.CalendarUtils;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class AlarmController
{	
	private static final Logger LOG = LoggerFactory.getLogger(AlarmController.class);

	@RequestMapping(value = { "/alarm/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView addView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/alarm/add");
		return mav;
	}

	@RequestMapping(value = { "/alarm/modify" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView indexView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/alarm/modify");
		return mav;
	}

	@RequestMapping(value = { "/alarm/create/success" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView successView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/alarm/add_success");
		return mav;
	}

	@RequestMapping(value = { "/alarm/create/failed" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView failedView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/alarm/add_failed");
		return mav;
	}

	@RequestMapping(value = { "/alarm/topic/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void alarmTopicAjax(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:").append((ip == null) ? request.getRemoteAddr() : ip).toString());
		try {
			byte[] output = GzipUtils.compressToByte(AlarmService.getTopics(ip));
			response.setContentLength((output == null) ? "NULL".toCharArray().length : output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/alarm/add/form" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView alarmAddForm(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
		Object object;
		JSONObject obj;
		ModelAndView mav = new ModelAndView();
		String ke_group_alarms = request.getParameter("ke_group_alarms");
		String ke_topic_alarms = request.getParameter("ke_topic_alarms");
		String ke_topic_lag = request.getParameter("ke_topic_lag");
		String ke_topic_email = request.getParameter("ke_topic_email");
		JSONArray topics = JSON.parseArray(ke_topic_alarms);
		JSONArray groups = JSON.parseArray(ke_group_alarms);
		AlarmDomain alarm = new AlarmDomain();
		for (Iterator<?> localIterator1 = groups.iterator(); localIterator1.hasNext();) {
			object = localIterator1.next();
			obj = (JSONObject) object;
			alarm.setGroup(obj.getString("name"));
		}
		for (Iterator<?> localIterator2 = topics.iterator(); localIterator2.hasNext();) {
			object = localIterator2.next();
			obj = (JSONObject) object;
			alarm.setTopics(obj.getString("name"));
		}
		try {
			alarm.setLag(Long.parseLong(ke_topic_lag));
		} catch (Exception ex) {
			LOG.error(new StringBuilder()
					.append("Parse long has error,msg is ")
					.append(ex.getMessage()).toString());
		}
		alarm.setModifyDate(CalendarUtils.getNormalDate());
		alarm.setOwners(ke_topic_email);

		Map<String, Object> map = AlarmService.addAlarm(alarm);
		if ("success".equals(map.get("status"))) {
			session.removeAttribute("Alarm_Submit_Status");
			session.setAttribute("Alarm_Submit_Status", map.get("info"));
			mav.setViewName("redirect:/alarm/create/success");
		} else {
			session.removeAttribute("Alarm_Submit_Status");
			session.setAttribute("Alarm_Submit_Status", map.get("info"));
			mav.setViewName("redirect:/alarm/create/failed");
		}
		return mav;
	}

	@RequestMapping(value = { "/alarm/list/table/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void alarmTopicListAjax(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String ip = request.getHeader("x-forwarded-for");
		LOG.info(new StringBuilder().append("IP:").append((ip == null) ? request.getRemoteAddr() : ip).toString());

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

		JSONArray ret = JSON.parseArray(AlarmService.list());
		int offset = 0;
		JSONArray retArr = new JSONArray();
		for (Iterator<?> localIterator2 = ret.iterator(); localIterator2.hasNext();) {
			JSONObject obj;
			Object tmp = localIterator2.next();
			JSONObject tmp2 = (JSONObject) tmp;
			if ((search.length() > 0) && (search.equals(tmp2.getString("topic")))) {
				obj = new JSONObject();
				obj.put("group", tmp2.getString("group"));
				obj.put("topic", tmp2.getString("topic"));
				obj.put("lag", tmp2.getLong("lag"));
				obj.put("owner", tmp2.getString("owner"));
				obj.put("created", tmp2.getString("created"));
				obj.put("modify", tmp2.getString("modify"));
				obj.put("operate",
						new StringBuilder()
								.append("<a name='remove' href='#")
								.append(tmp2.getString("group"))
								.append("/")
								.append(tmp2.getString("topic"))
								.append("' class='btn btn-danger btn-xs'>Remove</a>&nbsp")
								.toString());
				retArr.add(obj);
			} else if (search.length() == 0) {
				if ((offset < iDisplayLength + iDisplayStart)
						&& (offset >= iDisplayStart)) {
					obj = new JSONObject();
					obj.put("group", tmp2.getString("group"));
					obj.put("topic", tmp2.getString("topic"));
					obj.put("lag", tmp2.getLong("lag"));
					obj.put("owner", tmp2.getString("owner"));
					obj.put("created", tmp2.getString("created"));
					obj.put("modify", tmp2.getString("modify"));
					obj.put("operate",
							new StringBuilder()
									.append("<a name='remove' href='#")
									.append(tmp2.getString("group"))
									.append("/")
									.append(tmp2.getString("topic"))
									.append("' class='btn btn-danger btn-xs'>Remove</a>&nbsp")
									.toString());
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

	@RequestMapping(value = { "/alarm/{group}/{topic}/del" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView alarmDelete(@PathVariable("group") String group,
			@PathVariable("topic") String topic) {
		AlarmService.delete(group, topic);
		return new ModelAndView("redirect:/alarm/modify");
	}
}