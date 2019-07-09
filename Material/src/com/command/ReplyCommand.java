package com.command;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.common.ActionForward;
import com.common.Command;
import com.dao.MaterialDAO;
import com.dao.MaterialEntity;
import com.dao.MemberEntity;
import com.oreilly.servlet.MultipartRequest;

public class ReplyCommand implements Command {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		MemberEntity member = (MemberEntity) session.getAttribute("member");

		ActionForward action = new ActionForward();

		if (member == null) {
			action.setPath("loginForm.jsp");
			action.setSend(true);
			
		} else {
			String savePath = "D:/eclipse/jee-2018-12/workspace/Material/upload";

			try {
				MultipartRequest mr = new MultipartRequest(request, savePath, 1024 * 1024 * 10, "UTF-8");

				String mno = mr.getParameter("mno");
				String title = mr.getParameter("title");
				String content = mr.getParameter("content");
				String realName = mr.getFilesystemName("realName");
				String maskName = null;

				if (realName != null) {
					maskName = System.currentTimeMillis() + realName.substring(realName.lastIndexOf("."));

					File file = new File(savePath, realName);
					file.renameTo(new File(savePath, maskName));
				}

				MaterialDAO dao = new MaterialDAO();
				System.out.println(mno);
				MaterialEntity parent = dao.select(mno);
				System.out.println(parent);
				dao.positionUpdate(parent.getRef(), parent.getPosition());

				boolean result = dao.reply(mno, title, content, member.getId(), parent.getRef(), parent.getStep() + 1,
						parent.getPosition() + 1, maskName, realName);

				if (result) {
					action.setPath("materialList.do");
					action.setSend(true);
				} else {
					action.setPath("WEB-INF/error.jsp");
					action.setSend(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return action;
	}

}
