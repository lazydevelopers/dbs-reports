/**
 * 
 */
package pl.com.dbs.reports.profile.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import pl.com.dbs.reports.profile.domain.ProfilePhoto;
import pl.com.dbs.reports.support.db.dao.ADao;

/**
 * Profile photo CRUD.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2013
 */
@Repository
public class ProfilePhotoDao extends ADao<ProfilePhoto, Long> {

	@PersistenceContext
	private EntityManager em;	
	
	@Override
	public EntityManager getEntityManager() {
		return em;
	}
}
