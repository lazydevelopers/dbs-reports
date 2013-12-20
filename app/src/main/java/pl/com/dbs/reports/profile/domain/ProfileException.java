/**
 * 
 */
package pl.com.dbs.reports.profile.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
public class ProfileAddException extends Exception {
	private static final long serialVersionUID = 1068361460625579431L;
	private String code;
	private List<String> params = new ArrayList<String>();
	
	public ProfileAddException(String code) {
		super();
		this.code = code;
	}
	
	public ProfileAddException(String code, List<String> params) {
		this(code);
		this.params = params;
	}
	

	public String getCode() {
		return code;
	}

	public List<String> getParams() {
		return params;
	}
}
