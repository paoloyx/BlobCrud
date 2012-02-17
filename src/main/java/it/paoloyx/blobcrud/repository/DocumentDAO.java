package it.paoloyx.blobcrud.repository;

import it.paoloyx.blobcrud.model.Document;

public interface DocumentDAO {

	public Document saveOrUpdate(Document doc);
	
	public Document findByPk(Long id);
}
