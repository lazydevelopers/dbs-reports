/**
 * 
 */
package pl.com.dbs.reports.profile.web.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import pl.com.dbs.reports.access.domain.Access;
import pl.com.dbs.reports.authority.domain.Authority;
import pl.com.dbs.reports.profile.domain.ProfileCreation;
import pl.com.dbs.reports.profile.domain.ProfileGroup;
import pl.com.dbs.reports.support.web.file.FileMeta;
import pl.com.dbs.reports.support.web.form.AForm;

/**
 * New profile form.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
public class ProfileNewForm extends AForm implements ProfileCreation, Serializable {
	private static final long serialVersionUID = -3562062478732925527L;

	public static final String KEY = "profileNewForm";
	
	private String login;
	private String passwd;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	
	private String street;
	private String city;
	private String state;
	private String zipCode;
	private boolean accepted = false;
	protected boolean global = false;
	
	protected Set<Access> accesses = Sets.newHashSet();
	protected List<Authority> authorities = new ArrayList<Authority>();
    protected Set<Long> groups = Sets.newHashSet();
	
	private FileMeta  photo;
	
	public ProfileNewForm() {}
	
	public void reset() {
		super.reset();
		this.login = null;
		this.passwd = null;
		this.firstName = null;
		this.lastName = null;
		this.email = null;
		this.phone = null;
		this.street = null;
		this.city = null;
		this.state = null;
		this.zipCode = null;
		this.photo = null;
		this.accepted = false;
		this.accesses = Sets.newHashSet();
		this.authorities = new ArrayList<Authority>();
        this.groups = Sets.newHashSet();
	}
	

	@Override	
	public String getLogin() {
		return login;
	}

	@Override
	public String getPasswd() {
		return passwd;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPhone() {
		return phone;
	}
	
	@Override
	public Address getAddress() {
		if (StringUtils.isBlank(street)&&StringUtils.isBlank(city)&&StringUtils.isBlank(state)&&StringUtils.isBlank(zipCode)) return null;
		
		return new Address() {
			@Override
			public String getStreet() {
				return street;
			}
			@Override
			public String getCity() {
				return city;
			}
			@Override
			public String getState() {
				return state;
			}
			@Override
			public String getZipCode() {
				return zipCode;
			}
		};
	}

	@Override
	public File getPhoto()  {
		return this.photo!=null?this.photo.getFile():null;
	}
	
	@Override
	public List<Authority> getAuthorities() {
		return authorities;
	}

	@Override
	public Set<Access> getAccesses() {
		return accesses;
	}

	@Override
	public boolean isAccepted() {
		return accepted;
	}
	
	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public void setAccesses(Set<Access> accesses) {
		this.accesses = accesses;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}
	
	public void addPhoto(FileMeta file) {
		this.photo = file;
	}
	
	public boolean isPhoto() {
		return this.photo!=null;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	public boolean getAccepted() {
		return accepted;
	}

	public boolean isGlobal() {
		return global;
	}


    @Override
    public Set<Long> getGroups() {
        return groups;
    }

    public boolean hasGroups() {
        return groups!=null&&!groups.isEmpty();
    }

    public void setGroups(Set<Long> groups) {
        this.groups = groups;
    }
}
