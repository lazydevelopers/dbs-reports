/**
 * 
 */
package pl.com.dbs.reports.support.web.form;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
public abstract class AForm {
	private int page = 1;
	
	public void reset() {
		this.page = 1;
	}
	
	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}	
}