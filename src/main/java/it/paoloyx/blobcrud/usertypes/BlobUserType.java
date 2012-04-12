package it.paoloyx.blobcrud.usertypes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.BLOB;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.PGConnection;
import org.postgresql.core.BaseStatement;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

/**
 * Immutable Blob User type
 */
public class BlobUserType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.BLOB };
    }

    @Override
    public Class returnedClass() {
        return BlobUserType.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (x == null || y == null)
            return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Blob blob = rs.getBlob(names[0]);
        if (blob == null)
            return null;

        return blob;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, sqlTypes()[0]);
        } else {
            InputStream in = null;
            OutputStream out = null;
            BLOB tempBlob = null;
            LargeObject obj = null;
            try {
                // InputStream from blob
                Blob valueAsBlob = (Blob) value;
                in = valueAsBlob.getBinaryStream();
                // Retrieves information about the database
                DatabaseMetaData dbMetaData = session.connection().getMetaData();
                String dbProductName = dbMetaData.getDatabaseProductName();
                if (dbProductName.toUpperCase().contains("ORACLE")) {
                    OraclePreparedStatement oraclePreparedStatement = st.unwrap(OraclePreparedStatement.class);
                    tempBlob = BLOB.createTemporary(oraclePreparedStatement.getConnection(), true, BLOB.DURATION_SESSION);
                    tempBlob.open(BLOB.MODE_READWRITE);
                    out = tempBlob.setBinaryStream(1);
                    IOUtils.copy(in, out);
                    out.flush();
                    st.setBlob(index, tempBlob);
                } else if (dbProductName.toUpperCase().contains("POSTGRES")) {
                    BaseStatement pgStatement = st.unwrap(BaseStatement.class);
                    PGConnection connection = (PGConnection) pgStatement.getConnection();
                    LargeObjectManager lobj = connection.getLargeObjectAPI();
                    long oid = lobj.createLO();
                    obj = lobj.open(oid, LargeObjectManager.WRITE);
                    out = obj.getOutputStream();
                    IOUtils.copy(in, out);
                    out.flush();
                    st.setLong(index, oid);
                } else {
                    throw new RuntimeException("Database " + dbProductName + " is currently not supported");
                }
            } catch (Exception e) {
                // Eccezione non recuperabile, la rilanciamo come
                // runtimeException
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                if (obj != null) {
                    obj.close();
                }
                if (tempBlob != null && tempBlob.isOpen()) {
                    tempBlob.close();
                }
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(in);
            }
        }

    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
