package org.pudding.serialization.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.log4j.Logger;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化/反序列化实现.
 *
 * @author Yohann.
 */
public class HessianSerializer implements Serializer {
    private static final Logger logger = Logger.getLogger(HessianSerializer.class);

    @Override
    public byte type() {
        return SerializerType.HESSIAN.value();
    }

    @Override
    public <T> byte[] writeObject(T object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(bos);
        try {
            output.writeObject(object);
            output.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.warn("HessianSerializer.writeObject()", e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return null; // never get here
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(bis);
        try {
            Object object = input.readObject();
            return clazz.cast(object);
        } catch (IOException e) {
            logger.warn("HessianSerializer.readObject()", e);
        } finally {
            input.close();
        }
        return null; // never get here
    }
}
