package cc.triffic.wc.kafkamonitor.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cc.triffic.wc.kafkamonitor.service.DashboardService;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;

@Controller
public class DashboardController {
	private static final Logger LOG = LoggerFactory
			.getLogger(DashboardController.class);

	@RequestMapping(value = { "/" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView indexView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/main/index");
		return mav;
	}

	@RequestMapping(value = { "/dash/kafka/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void dashboardAjax(HttpServletResponse response,
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
			byte[] output = GzipUtils.compressToByte(DashboardService
					.getDashboard());
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
}