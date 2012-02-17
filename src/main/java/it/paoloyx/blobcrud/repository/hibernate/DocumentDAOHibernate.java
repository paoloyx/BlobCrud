package it.paoloyx.blobcrud.repository.hibernate;

import it.paoloyx.blobcrud.model.Document;
import it.paoloyx.blobcrud.repository.DocumentDAO;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentDAOHibernate implements DocumentDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	public Document saveOrUpdate(Document doc) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(doc);
		return doc;
	}

	public Document findByPk(Long id) {
		return (Document) this.sessionFactory.getCurrentSession().get(Document.class, id);
	}

}
