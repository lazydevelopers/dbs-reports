/**
 * 
 */
package pl.com.dbs.reports.profile.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;

import pl.com.dbs.reports.support.db.domain.IEntity;

/**
 * Note for profile.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
@Entity
@Table(name = "tpr_note")
public class ProfileNote implements IEntity {
	private static final long serialVersionUID = -6508129145627001423L;

	@Id
	@Column(name = "id")
	@SequenceGenerator(name = "sg_note", sequenceName = "spr_note", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sg_note")
	private Long id;	
	
	@Column(name = "content")
	private String content;
	
	
	@OneToOne(fetch=FetchType.LAZY, orphanRemoval=false)
	@JoinColumn(name="editor_id")	
	private Profile editor;		
	//@Column(name = "editor_id")
	//private long editorId;
	
	@Column(name = "edit_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date editDate;
	
	public ProfileNote() {/*JPA*/}
	
	public ProfileNote(String content, Profile editor) {
		this.content = content;
		this.editor = editor;
		this.editDate = new Date();
	}
	
	public ProfileNote modify(String content, Profile editor) {
		this.content = content;
		this.editor = editor;
		this.editDate = new Date();
		return this;
	}

	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public Profile getEditor() {
		return editor;
	}

	public Date getEditDate() {
		return editDate;
	}
	
	public boolean isBlank() {
		return StringUtils.isBlank(content);
	}
	
}
