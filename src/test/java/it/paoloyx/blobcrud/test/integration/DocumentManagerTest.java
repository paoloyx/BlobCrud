package it.paoloyx.blobcrud.test.integration;

import it.paoloyx.blobcrud.model.Document;
import it.paoloyx.blobcrud.provider.FileProvider;
import it.paoloyx.blobcrud.repository.DocumentDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;

import junit.framework.Assert;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/applicationContext*.xml" })
@Transactional
public class DocumentManagerTest {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DocumentDAO documentDao;
	
	@Autowired
	private FileProvider fileProvider;

	@Test
	public void testInsertDocument() {
		Document doc = new Document();
		File veryBigFile = new File(fileProvider.getAbsolutePath());
		FileInputStream finStream = null;
		try {
			try {
				finStream = new FileInputStream(veryBigFile);
				Session session = this.sessionFactory.getCurrentSession();
				Blob blob = Hibernate.getLobCreator(session).createBlob(
						finStream, veryBigFile.length());
				doc.setContent(blob);
				logger.info("Created blob from file: " + fileProvider.getAbsolutePath());
			} catch (FileNotFoundException e) {
				doc.setContent(null);
				e.printStackTrace();
				logger.warn("Warning: file " + fileProvider.getAbsolutePath() + " could not be found. Persisting null binary content");
			}
			// persist document
			Document persistedDoc = documentDao.saveOrUpdate(doc);
			
			// flushing the session
			this.sessionFactory.getCurrentSession().flush();
			
			// We clear Hibernate Session, then we retrieve the persisted document
			this.sessionFactory.getCurrentSession().clear();
			Document retrievedDoc = documentDao.findByPk(persistedDoc.getId());
			
			// Assertion
			Assert.assertEquals(retrievedDoc.getId(), persistedDoc.getId());
		}
		finally {
			try {
				finStream.close();
			} catch (IOException e) {
				// Unrecoverable error...
				e.printStackTrace();
			}
		}
	}
}
