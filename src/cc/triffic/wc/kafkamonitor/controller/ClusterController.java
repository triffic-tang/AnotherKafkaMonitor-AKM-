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

import cc.triffic.wc.kafkamonitor.service.ClusterService;
import cc.triffic.wc.kafkamonitor.utils.GzipUtils;

@Controller
public class ClusterController {
	private static final Logger LOG = LoggerFactory.getLogger(ClusterController.class);

	@RequestMapping(value = { "/cluster/info" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView clusterView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/cluster/cluster");
		return mav;
	}

	@RequestMapping(value = { "/cluster/zkcli" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public ModelAndView zkCliView(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/cluster/zkcli");
		return mav;
	}

	@RequestMapping(value = { "/cluster/info/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void clusterAjax(HttpServletResponse response,
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
			byte[] output = GzipUtils.compressToByte(ClusterService.getCluster());
			response.setContentLength((output == null) ? "NULL".toCharArray().length : output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/cluster/zk/islive/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void zkCliLiveAjax(HttpServletResponse response,
			HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");
		try {
			byte[] output = GzipUtils.compressToByte(ClusterService.zkCliIsLive().toJSONString());
			response.setContentLength((output == null) ? "NULL".toCharArray().length : output.length);
			OutputStream out = response.getOutputStream();
			out.write(output);

			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping(value = { "/cluster/zk/cmd/ajax" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public void zkCliCmdAjax(HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Charset", "utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Encoding", "gzip");

		String cmd = request.getParameter("cmd");
		String type = request.getParameter("type");
		try {
			byte[] output = GzipUtils.compressToByte(ClusterService.getZKMenu(cmd, type));

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