package it.valvorobica.thip.base.portal.web;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.web.WebFormModifier;

import it.valvorobica.thip.base.portal.YImgCatalogoPortale;

public class YImgCatalogoPortaleFormModifier extends WebFormModifier {

	@Override
	public void writeHeadElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeBodyStartElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeFormStartElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeFormEndElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeBodyEndElements(JspWriter out) throws IOException {
		YImgCatalogoPortale img = (YImgCatalogoPortale) this.getBODataCollector().getBo();
		if (img.isOnDB()) {
			if (img.getUrlImg() != null) {
				out.println("<script>");
//				String url = this.getServletEnvironment().getWebApplicationPath() + img.getUrlImg().substring(img.getUrlImg().indexOf("it"), img.getUrlImg().length());
//				out.println("document.getElementById('imgSuDB').src = '"+WebElement.formatStringForHTML(url)+"';");
				out.println("</script>");
			} else {
				out.println("<script>");
				out.println("document.getElementById('imgSuDB').parentNode.parentNode.style.display = 'none';");
				out.println("</script>");

			}
		} else {
			out.println("<script>");
			out.println("document.getElementById('imgSuDB').parentNode.parentNode.style.display = 'none';");
			out.println("</script>");
		}
	}

}
