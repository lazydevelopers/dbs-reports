/**
 * 
 */
package pl.com.dbs.reports.report.pattern.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import pl.com.dbs.reports.api.report.ReportFormat;
import pl.com.dbs.reports.api.report.pattern.PatternManifest;
import pl.com.dbs.reports.profile.domain.Profile;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
final class PatternBuilder {
	private static final Logger logger = Logger.getLogger(PatternBuilder.class);
	
	private byte[] content;	
	private Profile profile;
	private List<ReportPatternTransformate> transformates;
	private List<ReportPatternInflater> inflaters;
	private List<ReportPatternForm> forms;
	private Map<String, byte[]> inits;
	
	private PatternManifestBuilder manifestBuilder;
	
	private ReportPattern pattern;
	
	public PatternBuilder(final File file, Profile profile) throws IOException {
		this(FileUtils.readFileToByteArray(file));
		this.profile = profile;
	}
	
	private PatternBuilder(final byte[] content) {
		Validate.notNull(content, "A content is no more!");
		Validate.isTrue(content.length>0, "A content is 0 size!");
		this.content = content;
		this.transformates = new ArrayList<ReportPatternTransformate>();
		this.inflaters = new ArrayList<ReportPatternInflater>();
		this.forms = new ArrayList<ReportPatternForm>();
		this.inits = new HashMap<String, byte[]>();
		
		manifestBuilder = new PatternManifestBuilder(content);
	}
	
	public PatternBuilder build() throws IOException {
		manifestBuilder.build();
		
		resolveTransformates();
		resolveInflaters();		
		resolveForms();
		resolveInitFiles();
		
		/**
		 * FIXME: assigment of inflaters..
		 */
		for (ReportPatternTransformate t : transformates) t.setInflaters(inflaters);
		
		this.pattern = new ReportPattern(content,
										profile,
										manifestBuilder.getName(),
										manifestBuilder.getVersion(),
										manifestBuilder.getAuthor(),
										manifestBuilder.getFactory(),
										manifestBuilder.getManifest(),
										manifestBuilder.getAccesses(),
										transformates,
										forms);
		this.pattern.addInits(inits);
		return this;
	}
	
	public ReportPattern getPattern() {
		return pattern;
	}

	
	private void resolveInflaters() throws IOException {
		Validate.notNull(content, "Content is no more!");
		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content));
		Validate.notNull(zip, "A zip file is no more!");
		
		logger.info("Resolving inflaters..");
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) { 
            	continue;
            } else if (isInflater(entry.getName())) {
            	logger.info("Inflater found in file: "+entry.getName());
           		inflaters.add(new ReportPatternInflater(readZipEntry(zip, entry), entry.getName()));
            }
        }				
	}
	
	private void resolveTransformates() throws IOException {
		Validate.notNull(content, "Content is no more!");
		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content));
		Validate.notNull(zip, "A zip file is no more!");

		logger.info("Resolving transformates..");
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) { 
            	continue;
            } else if (isTransformate(entry.getName())) {
            	ReportFormat type = resolveType(entry.getName());
            	logger.info("Transformate found in file: "+entry.getName()+" as :"+(type!=null?type.getExt():"?"));
           		transformates.add(new ReportPatternTransformate(readZipEntry(zip, entry), entry.getName(), type));
            }
        }
	}		
	
	/**
	 * Input forms definitions..
	 */
	private void resolveForms() throws IOException {
		Validate.notNull(content, "Content is no more!");
		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content));
		Validate.notNull(zip, "A zip file is no more!");

		logger.info("Resolving forms..");
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) { 
            	continue;
            } else if (isForm(entry.getName())) {
            	logger.info("Form found in file: "+entry.getName());
           		forms.add(new ReportPatternForm(readZipEntry(zip, entry), entry.getName()));
            }
        }
	}
	
	/**
	 * Some init sqls?
	 */
	private void resolveInitFiles() throws IOException {
		Validate.notNull(content, "Content is no more!");
		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content));
		Validate.notNull(zip, "A zip file is no more!");

		logger.info("Resolving init files..");
		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) { 
            	continue;
            } else if (isInitFile(entry.getName())) {
            	logger.info("Init file found in file: "+entry.getName());
            	this.inits.put(entry.getName(), readZipEntry(zip, entry));
            }
        }
	}		

	/**
	 * Is this init sql file?
	 */
	private boolean isInitFile(String filename) {
		PatternManifest manifest = manifestBuilder.getManifest();
		Validate.notNull(manifest, "Manifest is no more!");
		String sqls = manifest.getPatternAttribute(ReportPatternManifest.ATTRIBUTE_INIT_SQL);		

		if (!StringUtils.isBlank(sqls)) {
			StringTokenizer st = new StringTokenizer(sqls, ";");
			while (st.hasMoreTokens()) {
				final String token = StringUtils.trim(st.nextToken());
				if (token.equalsIgnoreCase(filename)) return true;
			}
		}
        return false;
	}		
	
	/**
	 * Is this inflater file?
	 */
	private boolean isInflater(String filename) {
		return ReportPatternManifest.INFLATER_PATTERN.matcher(filename).find()&&!isInitFile(filename);
	}
	
	/**
	 * Is this form file?
	 */
	private boolean isForm(String filename) {
		PatternManifest manifest = manifestBuilder.getManifest();
		Validate.notNull(manifest, "Manifest is no more!");
		
		String forms = manifest.getPatternAttribute(ReportPatternManifest.ATTRIBUTE_FORM_FILENAME);		

		if (!StringUtils.isBlank(forms)) {
			StringTokenizer st = new StringTokenizer(forms, ";");
			while (st.hasMoreTokens()) {
				final String token = StringUtils.trim(st.nextToken());
				if (token.equalsIgnoreCase(filename)) return true;
			}
		}
        return false;
	}	
	
	/**
	 * Is this manifest file name?
	 */
	private static boolean isManifestFile(String filename) {
        return ReportPatternManifest.MANIFEST_PATTERN.matcher(filename).find();
	}	
	
	/**
	 * Transformate can be anything..
	 */
	private boolean isTransformate(String filename) {
		return !isManifestFile(filename)
				&&!isInitFile(filename)
				&&!isInflater(filename)
				&&!isForm(filename);
	}		
	
	private ReportFormat resolveType(final String name) throws IOException {
		PatternManifest manifest = manifestBuilder.getManifest();
		Validate.notNull(manifest, "Manifest is no more!");
		String exts = manifest.getPatternAttribute(ReportPatternManifest.ATTRIBUTE_EXTENSION_MAP);	
		
		if (!StringUtils.isBlank(exts)) {
			//.resolve from map..
			StringTokenizer st = new StringTokenizer(exts, ";");
			while (st.hasMoreTokens()) {
				final String token = StringUtils.trim(st.nextToken());
				/**
				 * Find pairs filename=ext
				 */
			    Matcher m = ReportPatternManifest.EXTENSION_PATTERN.matcher(token);
			    m.reset();
			    while (m.find()) {
			    	/**
			    	 * Confirm if file exist with given filename...
			    	 */
			    	final String filename = StringUtils.trim(m.group(1));
			    	final String ext = StringUtils.trim(m.group(2));
			    	if (name.equalsIgnoreCase(filename)) return ReportFormat.of(ext);
			    }
			}
		}
		//..resolve from extension..
		return ReportFormat.of(name);
	}	
	

	/**
	 * Read entry part of zip.
	 */
	private byte[] readZipEntry(final ZipInputStream zip, final ZipEntry entry) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytes;
//		long count = 0;
//		long size = entry.getSize();
//	    while (-1 != (bytes = zip.read(buffer)) && count < size) {
//	        output.write(buffer, 0, bytes);
//	        count += bytes;
//	    }		
		while ((bytes = zip.read(buffer)) != -1) 
	        output.write(buffer, 0, bytes);
	    
	    output.close();
	    return output.toByteArray();
	}
}