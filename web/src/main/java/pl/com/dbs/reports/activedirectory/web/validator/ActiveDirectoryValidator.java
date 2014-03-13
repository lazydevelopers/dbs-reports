/**
 * 
 */
package pl.com.dbs.reports.activedirectory.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pl.com.dbs.reports.activedirectory.web.form.ActiveDirectoryListForm;

/**
 * ActiveDirectory data.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2014
 */
public class ActiveDirectoryValidator implements Validator {
	public ActiveDirectoryValidator() {}
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return ActiveDirectoryListForm.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		ActiveDirectoryListForm form = (ActiveDirectoryListForm)target;

		if (errors.hasErrors()) return;
		
		form.getFilter().putValue(form.getValue());
	}
}