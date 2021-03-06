/**
 * 
 */
package pl.com.dbs.reports.report.pattern.domain;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.Validate;
import pl.com.dbs.reports.access.domain.Access;
import pl.com.dbs.reports.api.report.ReportType;
import pl.com.dbs.reports.api.report.pattern.Pattern;
import pl.com.dbs.reports.api.report.pattern.PatternFormat;
import pl.com.dbs.reports.api.report.pattern.PatternManifest;
import pl.com.dbs.reports.api.report.pattern.PatternTransformate;
import pl.com.dbs.reports.profile.domain.Profile;
import pl.com.dbs.reports.security.domain.SessionContext;
import pl.com.dbs.reports.support.db.domain.AEntity;
import pl.com.dbs.reports.support.utils.separator.Separator;

import javax.persistence.*;
import java.util.*;

/**
 * Encja z wzocem raportu.
 * Obiekt zawiera paczke danych tak jak zostala dostarczona (czyli spakowane).
 * Dla optymalizacji dane z manifestu sa dostepne "od reki".
 *
 * 
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
@Entity
@Table(name = "tre_pattern")
public class ReportPattern extends AEntity implements Pattern {
	private static final long serialVersionUID = 8993097483507616245L;
	
	@Id
	@Column(name = "id")
	@SequenceGenerator(name = "sg_pattern", sequenceName = "sre_pattern", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sg_pattern")
	private Long id;
	
	@Column(name = "active")
	private boolean active;	
	
	@Column(name = "upload_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date uploadDate;

	@OneToOne(fetch=FetchType.EAGER, orphanRemoval=false)
    @JoinColumn(name="creator_id")
    private Profile creator;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "author")
	private String author;
	
	@Column(name = "factory")
	private String factory;
	
	@Convert(converter=ReportPatternManifestConverter.class)
	private ReportPatternManifest manifest;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "tre_pattern_access", joinColumns = @JoinColumn(name = "pattern_id"))
	@Column(name = "name")
    private List<String> accesses = new ArrayList<String>();
	
	@OneToMany(mappedBy="pattern", orphanRemoval=true)
	//@OrderBy("name")  
    private List<ReportPatternTransformate> transformates = new ArrayList<ReportPatternTransformate>();
	
	@OneToMany(mappedBy="pattern", orphanRemoval=true)
    private List<ReportPatternForm> forms = new ArrayList<ReportPatternForm>();
	
	/**
	 * Init sqls. Accessed only while import (NOT PERSIST) 
	 */
	@Transient
	private Map<String, byte[]> inits = new HashMap<String, byte[]>();
	
	@Column(name = "filename")
	private String filename;	
	
    /**
     * http://stackoverflow.com/questions/10108533/jpa-should-i-store-a-blob-in-the-same-table-with-fetch-lazy-or-should-i-store-i
     * http://www.hostettler.net/blog/2012/03/22/one-to-one-relations-in-jpa-2-dot-0/
     */
    @Lob
    @Basic(optional = true, fetch = FetchType.EAGER)
    @Column(name = "content", nullable = true)
	private byte[] content;	
	
	public ReportPattern() {/* JPA */}
	
	public ReportPattern(ReportPatternCreation context) {
		Validate.isTrue(context.getContent().length>0, "A content is 0!");
		Validate.notNull(context.getCreator(), "Creator is no more!");
	
		this.uploadDate = new Date();
		this.creator = context.getCreator();
		this.name = context.getName();		
		this.version = context.getVersion();
		this.author = context.getAuthor();
		this.factory = context.getFactory();
		this.manifest = (ReportPatternManifest)context.getManifest();
		this.accesses = context.getAccesses();
		this.transformates = context.getTransformates();
		this.filename = context.getFilename();
		this.content = context.getContent();
		this.forms = context.getForms();
		this.active = true;
		
		for (ReportPatternTransformate t : context.getTransformates()) t.setPattern(this);
		for (ReportPatternForm f : context.getForms()) f.setPattern(this);
	}
	
	/**
	 * Deactivate entity.
	 */
	public void deactivate() {
		this.active = false;
	}

	@Override
	public Date getUploadDate() {
		return uploadDate;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getFactory() {
		return factory;
	}

	@Override
	public PatternManifest getManifest() {
		return manifest;
	}

	@Override
	public List<? extends PatternTransformate> getTransformates() {
		return transformates;
	}
	
	@Override
	public List<ReportPatternForm> getForms() {
		return forms;
	}

	/**
	 * Get first form.
	 */
	public ReportPatternForm getForm() {
		return forms!=null&&!forms.isEmpty()?forms.get(0):null;
	}

	public long getId() {
		return id;
	}
	
	public boolean hasId() {
		return id!=null;
	}

	public boolean isActive() {
		return active;
	}

	public String getAuthor() {
		return author;
	}
	
	public Profile getCreator() {
		return creator;
	}	
	
	public byte[] getContent() {
		return content;
	}
	
	public String getFilename() {
		return filename;
	}

	public List<String> getAccesses() {
		return accesses;
	}
	
	public String getAccessesAsString() {
		StringBuffer sb = new StringBuffer();
		Separator s = new Separator(", ");
		for (String access : accesses) sb.append(s).append(access);
		return sb.toString();
	}	
	
	/**
	 * Is pattern accessigle for given accesses collection?
	 */
	public boolean isAccessible(final Set<Access> accesses) {
		for (final Access access : accesses)
			if (Iterables.find(this.accesses, new Predicate<String>() {
					@Override
					public boolean apply(String input) { return input.equals(access.getName()); }
				}, null)!=null) return true;
		return false;
	}
	
	/**
	 * Is accessible for session user?
	 */
	public boolean isAccessible() {
		return SessionContext.getProfile()!=null&&isAccessible(SessionContext.getProfile().getAccesses());
	}
	
	public List<PatternFormat> getFormats() {
		Set<PatternFormat> result = new HashSet<PatternFormat>();
		for (ReportPatternTransformate transformate : transformates) 
			result.add(transformate.getFormat());

		Ordering<PatternFormat> ordering = Ordering.natural().onResultOf(new Function<PatternFormat, String>() {
		    public String apply(PatternFormat format) {
		        return format.getReportExtension();
		    }
		});
		
		return ImmutableSortedSet.orderedBy(ordering).addAll(result).build().asList();
	}
	
//	public PatternFormat getFormat(final ReportType type) {
//		return Iterables.find(getFormats(), new Predicate<PatternFormat>() {
//			@Override
//			public boolean apply(PatternFormat input) { 
//				return input.getReportType().equals(type); 
//			}
//		}, null);
//	}
	
	public String getFormatsAsString() {
		StringBuffer sb = new StringBuffer();
		Separator s = new Separator(",");
		for (PatternFormat format : getFormats()) 
			sb.append(s).append(format.getReportExtension());
		return sb.toString();		
	}
	
	/**
	 * Find transformate for given pattern..
	 */
	public PatternTransformate getTransformate(PatternFormat format) {
		for (PatternTransformate transformate : getTransformates())		
			if (transformate.getFormat().equals(format)) return transformate;
		return null;
	}
	
	public PatternTransformate getTransformate(final ReportType format) {
		return Iterables.find(getTransformates(), new Predicate<PatternTransformate>() {
			@Override
			public boolean apply(PatternTransformate input) { 
				return input.getFormat().getReportType().equals(format); 
			}
		}, null);		
	}
	

	public void addInits(Map<String, byte[]> inits) {
		if (inits != null) this.inits.putAll(inits);
	}

	public Map<String, byte[]> getInits() {
		return this.inits;
	}
	
}
