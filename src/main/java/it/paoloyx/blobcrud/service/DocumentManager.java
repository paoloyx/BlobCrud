package it.paoloyx.blobcrud.service;

import it.paoloyx.blobcrud.model.Document;

public interface DocumentManager {

	public Document saveOrUpdate(Document doc);

	public Document findByPk(Long id);
}
