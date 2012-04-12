package it.paoloyx.blobcrud.model;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "DOCUMENT_BLOBCRUD")
public class Document {

    private Long id;

    private Integer version;

    private String title;

    private Blob content;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "DOCUMENT_ID", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    @Column(name = "DOCUMENT_VERSION")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Column(name = "DOCUMENT_TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Type(type = "it.paoloyx.blobcrud.usertypes.BlobUserType")
    @Column(name = "DOCUMENT_CONTENT")
    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

}
