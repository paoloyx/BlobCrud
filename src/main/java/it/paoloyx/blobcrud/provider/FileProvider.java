package it.paoloyx.blobcrud.provider;


public class FileProvider {

	private String absolutePath;

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	public String getFileToPersistAbsolutePath() {
		return this.absolutePath;
	}
}
