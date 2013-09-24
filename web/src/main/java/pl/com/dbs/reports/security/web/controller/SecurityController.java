/**
 * 
 */
package pl.com.dbs.reports.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pl.com.dbs.reports.support.web.alerts.Alerts;

/**
 * Logowanie itd
 * 
 * @author krzysztof.kaziura@gmail.com
 */
@Controller
public class SecurityController {
	@Autowired private Alerts webmessages;
	
	@RequestMapping(value="/security/login", method = RequestMethod.GET)
    public String login(RedirectAttributes ra) {
		return "security/login";
    }
	
	@RequestMapping(value="/security/loginfailed", method = RequestMethod.GET)
    public String loginfailed(RedirectAttributes ra, @RequestParam(value="code", required=false) String code) {
		if (StringUtils.isEmpty(code)) webmessages.addError(ra, code);	
		return "redirect:/security/login";
    }
	
	@RequestMapping(value="/security/noaccess", method = RequestMethod.GET)
    public String noaccess(ModelMap model, @RequestParam(value="code", required=false) String code, RedirectAttributes ra) {
		if (!StringUtils.isEmpty(code)) webmessages.addError(ra, code);
		return "security/noaccess";
	}	
	
//	@InitBinder
//	protected void initBinder(WebDataBinder binder) {
//		if (binder.getTarget() instanceof PasswordChangeForm) binder.setValidator(validator);
//	}
}