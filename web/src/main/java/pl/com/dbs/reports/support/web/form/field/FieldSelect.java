/**
 * 
 */
package pl.com.dbs.reports.support.web.form.field;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

import pl.com.dbs.reports.support.utils.separator.Separator;
import pl.com.dbs.reports.support.web.form.option.FieldOption;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


/**
 * Single select field.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlDiscriminatorValue("select")
public class FieldSelect extends AField<String> implements IFieldInflatable, IFieldSelectable {
	private static final long serialVersionUID = -606676031675990235L;
	@XmlAttribute(name="value")
	protected String value;		
	@XmlElement(name="option", namespace = "http://www.dbs.com.pl/reports/1.0/form")
	private List<FieldOption> options;
	@XmlAttribute(name="source")
	private String source;	
	
	public FieldSelect() {
		super();
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public String getValueAsString() {
		return hasValue()?value:"";
	}
	
	public String getValueAsLabel() {
		if (!hasValue()) return null;
		FieldOption option = find(this.value);
		return option!=null?option.getLabel():null;
	}	
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean hasValue() {
		return !StringUtils.isBlank(this.value);
	}		
	
	@Override
	public List<FieldOption> getOptions() {
		return options;
	}
	
	@Override
	public String getSource() {
		return source;
	}

	@Override
	public IFieldInflatable inflate(FieldOption option) {
		if (this.options == null)
			this.options = new LinkedList<FieldOption>();
		this.options.add(option);
		return this;
	}	
	
	@Override
	public String getTile() {
		return "tiles-field-select";
	}
	
	@Override
	public void init(LinkedList<AField<?>> fields) {
		super.init(fields);
		if (hasOptions()) for (FieldOption option : options) option.init();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (hasOptions()) {
			sb.append(";options:");
			Separator s = new Separator(",");
			for (FieldOption option: options) sb.append(s).append("[").append(option).append("]");
		}		

		sb.append(";value:"+(hasValue()?value:""));
		return sb.toString(); 
	}	
	
	private boolean hasOptions() {
		return this.options!=null&&!this.options.isEmpty();
	}

	private FieldOption find(final String value) {
		return Iterables.find(this.options, new Predicate<FieldOption>() {
			@Override
			public boolean apply(FieldOption input) {
				return input.isValue(value);
			}
			
		}, null);
	}

	
}
