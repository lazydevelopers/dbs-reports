/**
 * 
 */
package pl.com.dbs.reports.report.domain.builders;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import pl.com.dbs.reports.api.report.ReportParameter;
import pl.com.dbs.reports.api.report.pattern.PatternInflater;
import pl.com.dbs.reports.api.report.pattern.PatternTransformate;
import pl.com.dbs.reports.report.domain.builders.inflaters.ReportTextBlockInflater;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Report blocks builder for text formats.
 * 
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
@Slf4j
public class ReportTextBlocksBuilder implements ReportBlocksBuilder {
	private static final String BLOCK_START_PATTERN = "[BLOCK";
	private static final String BLOCK_END_PATTERN = "BLOCK]";
	private static final Pattern BLOCK_LABEL_PATTERN = Pattern.compile("^\\[BLOCK\\(([\\w\\d_]+)\\)[\\s\\S]*",  Pattern.CASE_INSENSITIVE);
	private static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("\\[BLOCK\\([\\w\\d_]+\\)([\\s\\S]*)",  Pattern.CASE_INSENSITIVE);
	private static final Pattern BLOCK_REST_PATTERN = Pattern.compile("^BLOCK\\]([\\s\\S]*)",  Pattern.CASE_INSENSITIVE);
	private static final Pattern IN_VARIABLE_PATTERN = Pattern.compile("\\^\\$([\\w\\d_]+)\\^",  Pattern.CASE_INSENSITIVE);
	private static final Pattern INFLATER_PATTERN = Pattern.compile("(?:^|\\r{0,1}\\n)(\\w+):\\s*(?:\\r{0,1}\\n)+(.+?)(?=;\\s*\\r{0,1}\\n|;\\s*$)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

	protected byte[] content;
	protected ReportTextBlock root;
	private ReportTextBlockInflater inflater;
	private List<ReportBlockInflation> inflations;
	private List<ReportParameter> parameters;
	private StringBuilder sb;
	
	public ReportTextBlocksBuilder(final PatternTransformate transformate) {
		content = transformate.getContent();
		parameters = Lists.newArrayList();
		sb = new StringBuilder();
		resolveInflations(transformate);
		resolveInput();
	}
	
	public ReportTextBlocksBuilder(final PatternTransformate transformate, ReportTextBlockInflater inflater, final List<ReportParameter> params) {
		this(transformate);
		this.inflater = inflater;
		addParameters(params);
	}
	
	private ReportTextBlocksBuilder addParameters(List<ReportParameter> parameters) {
		if (parameters!=null) {
			this.parameters.addAll(parameters);
		}
		return this;
	}	

	
	/**
	 * Content. Either before as well as after construction.
	 */
	@Override
	public byte[] getContent() {
		return content;
	}

	ReportTextBlock getRootBlock() {
		return root;
	}	
	
	/**
	 * Iterate thorough all .sql files and obtain all inflaters (labeled block) from them..
	 */
	private void resolveInflations(final PatternTransformate transformate) {
		this.inflations = new ArrayList<ReportBlockInflation>();
		
		for (PatternInflater inflater : transformate.getInflaters()) {
			resolveInflations(new String(inflater.getContent()));
		}
	}	
	
	/**
	 * Find all inflation blocks in provided content, like: 
	 * INIT: SELECT INTO OUT_VARIABLE1, OUT_VARIABLE2 FROM TABLE WHERE ID=^$IN_VARIABLE1^ AND DATE<^$IN_VARIABLE2^;
	 * -- label: INIT
	 * -- sql: SELECT INTO OUT_VARIABLE1, OUT_VARIABLE2 FROM TABLE WHERE ID=^$IN_VARIABLE1^ AND DATE<^$IN_VARIABLE2^;
	 */
	private void resolveInflations(String content) {
		log.debug("Resolving inflations..");
		Matcher inflations = INFLATER_PATTERN.matcher(content);
	    while (inflations.find()) {
	    	String label = StringUtils.trim(inflations.group(1));
	    	String sql = StringUtils.trim(inflations.group(2));
	    	ReportBlockInflation inflation = new ReportBlockInflation(label, sql);
	    	log.debug("Inflation found: "+inflation);
	    	this.inflations.add(inflation);
	    }
	}	
	
	/**
	 * Find all INPUT PARAMETERS in all inflations, like:
	 * SELECT INTO OUT_VARIABLE1, OUT_VARIABLE2 FROM TABLE WHERE ID=^$IN_VARIABLE1^ AND DATE<^$IN_VARIABLE2^;
	 * -- [IN_VARIABLE1, IN_VARIABLE2]
	 */
	private void resolveInput() {
		for (ReportBlockInflation inflater : this.inflations) {
			log.debug("Resolving input variables of "+inflater.getLabel());
			Matcher variables = IN_VARIABLE_PATTERN.matcher(inflater.getSql());
		    while (variables.find()) {
		    	String variable = StringUtils.trim(variables.group(1));
		    	log.debug("Input variable found: "+variable);
		    	inflater.addInput(variable);
		    }
		}
	}	
	

	
	@Override
	public ReportTextBlocksBuilder build() throws ReportBlockException {
		//..deconstruct first...
		deconstruct();
		
		//..build context..
		ReportBlocksBuildContext context = new ReportBlocksBuildContext(root, parameters, sb);
		
		//..inflate...
		if (inflater!=null) {
			try {
				inflater.inflate(context);
			} catch (ReportBlocksBuildTerminationException e) {
				log.info("Building terminated!", e);
			}
		}
		
		content = sb!=null?sb.toString().getBytes():null;
		return this;
	}

	private void deconstruct() {
		ReportTextBlock root = new ReportTextBlock();
		this.root = root;
		parse(new String(content));
		this.root = root;
		this.root.print();
	}
	
	/**
	 * Breakes content into blocks.
	 * All in memory - sic!
	 */
	private String parse(String content) {
		if (StringUtils.isBlank(content)) return null;
		
		Content data = new Content(content);
		String rest = null;
		
		if (data.isSimple()) {
			//..add to current block..
			ReportTextBlock block = new ReportTextBlock(this.root).addContent(data.getText());
			//resolve invariables..
			resolveInput(data.getText());
			//..put to parent..
			this.root.addBlock(block);
			rest = data.getContent();
		} else if (data.isBlockStart()) {
			String label = data.getBlockLabel();
			rest = data.getBlockContent();
			
			ReportTextBlock block = new ReportTextBlock(this.root, label);
			resolveInflation(block);
			this.root.addBlock(block);
			this.root = block;
		} else if (data.isBlockEnd()) {
			rest = data.getBlockRest();
			this.root = this.root.getParent();
		} else {
			throw new IllegalStateException("Unknown TextBlock build phase!");
		}
		
		return parse(rest);
	}
	
	/**
	 * Select inflater connected with root block (by label).
	 * If block is not inflatable (has no label) do nothing.
	 */
	private void resolveInflation(final ReportTextBlock block) {
		if (StringUtils.isBlank(block.getLabel())) return;
		
		block.addInflation(
			Iterables.find(inflations, new Predicate<ReportBlockInflation>() {
					@Override
					public boolean apply(ReportBlockInflation input) {
						return input.isApplicable(block.getLabel());
					}
			}, null));
	}	
	
	private void resolveInput(String content) {
		log.debug("Resolving input variables..");
		Matcher variables = IN_VARIABLE_PATTERN.matcher(content);
	    while (variables.find()) {
	    	String variable = StringUtils.trim(variables.group(1));
	    	log.debug("Input variable found: "+variable);
	    	root.addParameter(variable);
	    }
	}
	
	private class Content {
		private String text = null;
		private String content = null;
		
		Content(String txt) {
			int sdx = txt.indexOf(BLOCK_START_PATTERN);
			int edx = txt.indexOf(BLOCK_END_PATTERN);
			
			if (sdx<0&&edx<0) {
				text = txt;
				content = null;
			} else if (sdx==0||edx==0) {
				text = null;
				content = txt;
			} else {
				int idx = (sdx>=0&&edx>=0)? 
							(sdx<edx?sdx:edx):
								(sdx<0&&edx>=0)?edx:sdx;
				text = txt.substring(0, idx);
				content = txt.substring(idx);
			}
		}
		
		boolean isSimple() {
			return !StringUtils.isEmpty(text);
		}
		
		String getText() {
			return text;
		}
		
		String getContent() {
			return content;
		}
		
		boolean isBlockStart() {
			return content.startsWith(BLOCK_START_PATTERN);
		}
		
		boolean isBlockEnd() {
			return content.startsWith(BLOCK_END_PATTERN);
//			int edx = content.indexOf(BLOCK_END_PATTERN);
//			return edx>=0?content.substring(0, edx).indexOf(BLOCK_START_PATTERN)<0:false;
		}
		
		String getBlockLabel() {
			Matcher m = BLOCK_LABEL_PATTERN.matcher(content);
			return m.matches()?m.group(1):null;
		}
		
		String getBlockContent() {
			Matcher m = BLOCK_CONTENT_PATTERN.matcher(content);
			return m.matches()?m.group(1):null;
		}
		
		String getBlockRest() {
			Matcher m = BLOCK_REST_PATTERN.matcher(content);
			return m.matches()?m.group(1):null;
		}		
	}
}
