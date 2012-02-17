package it.paoloyx.blobcrud.service.impl;

import it.paoloyx.blobcrud.model.Document;
import it.paoloyx.blobcrud.repository.DocumentDAO;
import it.paoloyx.blobcrud.service.DocumentManager;

public class DocumentManagerImpl implements DocumentManager {

	private DocumentDAO documentDAO;
	
	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public Document saveOrUpdate(Document doc) {
		return documentDAO.saveOrUpdate(doc);
	}

	public Document findByPk(Long id) {
		return documentDAO.findByPk(id);
	}

}
