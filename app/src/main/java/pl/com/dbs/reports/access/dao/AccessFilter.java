/**
 * 
 */
package pl.com.dbs.reports.access.dao;

import pl.com.dbs.reports.access.domain.Access;
import pl.com.dbs.reports.access.domain.Access_;
import pl.com.dbs.reports.support.db.dao.AFilter;

/**
 * Access filter.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
public class AccessFilter extends AFilter<Access> {
	private static final long serialVersionUID = 1095344485559188536L;
	
	private static final int DEFAULT_PAGER_SIZE = 10;
	private String name;
	private String description;

	
	public AccessFilter() {
		getPager().setPageSize(DEFAULT_PAGER_SIZE);
		getSorter().add(Access_.name.getName(), true);
	}
	
	public String getName() {
		return name;
	}
	public void putName(String name) {
		this.name = name;
	}
	
	public AccessFilter putDescription(String desc) {
		this.description = desc;
        return this;
	}

	public String getDescription() {
		return description;
	}
}
