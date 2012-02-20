package it.paoloyx.blobcrud.usertypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
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
            // oracle.sql.BLOB
            OraclePreparedStatement oraclePreparedStatement = st.unwrap(OraclePreparedStatement.class);
            BLOB tempBlob = BLOB.createTemporary(oraclePreparedStatement.getConnection(), true, BLOB.DURATION_SESSION);
            tempBlob.open(BLOB.MODE_READWRITE);
            out = tempBlob.getBinaryOutputStream();
            Blob valueAsBlob = (Blob) value;
            in = valueAsBlob.getBinaryStream();
            try {
                IOUtils.copy(in, out);
                out.flush();
            } catch (IOException e) {
                // Eccezione non recuperabile, la rilanciamo come
                // runtimeException
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            IOUtils.closeQuietly(out);
            tempBlob.close();
            st.setBlob(index, tempBlob);
            IOUtils.closeQuietly(in);
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

